/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple;


import clemiraparser.DependencyInstance;
import clemiraparser.dictionary.SimpleDependencyLabelingDictionary;
import clemiraparser.labeling.DependencyLabeler;
import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.util.MySparseVector;
import clemiraparser.util.ViterbiProblem;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class SimpleDependencyLabeler extends DependencyLabeler{
    
    public static LossFunction lossFunction(){
        if (lossFunction.equals("mcdonaldhamming")){
            return new McDonaldHammingLoss();
        }else if (lossFunction.equals("rootpreferred")){
            return new RootPreferredLossFunction(trainLambda);
        }else{
            throw new IllegalArgumentException("unknown loss function " + lossFunction);
        }
    }
    public static Chooser chooser(){
        if (chooser.equals("kbest")){
            return new KBestChooser(trainK);
        }else if (chooser.equals("lworst")){
            return new LWorstChooser(trainL);
        }else if (chooser.equals("kbestlworst")){
            return new KBestLWorstChooser(trainK,trainL);
        }else if (chooser.equals("klossmarkedupbest")){
            return new KLossMarkedUpBestChooser(trainK, (EdgeFactorizedLoss) lossFunction());
        }else{
            throw new IllegalArgumentException("unknown chooser " + chooser);
        }
    }
    public static ConstraintType constraint(){
        if (constraint.equals("original")){
            return new MIRAConstraintType();
        }else if (constraint.equals("modified")){
            return new ModifiedConstraintType(trainAlpha);
        }else {
            throw new IllegalArgumentException("unknown constraint " + constraint);
        }
    }
    
    SimpleDependencyLabelingDictionary dictionary;
    Parameter parameter;
    

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {

            long start = System.currentTimeMillis();
            SimpleDependencyLabelingDictionary dictionary = new SimpleDependencyLabelingDictionary();
            dictionary.add(instances);
            
            long end = System.currentTimeMillis();
            System.out.println("creating dictionary took: " + (end-start));
            System.out.println("Num Feats: " + dictionary.getSize());
                        
            System.out.println("training...");
            train(dictionary, instances, lossFunction(), chooser(), constraint(), numIters);
            System.out.println("done");
    }
    
    public void train(SimpleDependencyLabelingDictionary dictionary,
            List<DependencyInstance> instances,
            LossFunction lossFunction,
            Chooser chooser,
            ConstraintType constraint,
            int numIter){
        this.dictionary = dictionary;
        parameter = new Parameter(dictionary.getSize());
        int T = instances.size();
        RealVector v = new MySparseVector(dictionary.getSize());
        
        //optimization for feature vectors creation
        List<DependencyLabelsFeatureVectors> fvs = new ArrayList<>(instances.size());
        int count = 0;
        System.out.print("creating feature Vectors");
        for (DependencyInstance instance : instances){
            System.out.print(" " + count);count++;
            DependencyLabelsFeatureVectors featureVector = dictionary.featureVectors(instance);
            fvs.add(featureVector);
        }
              
        //training iterations
        for (int i=0;i<numIter;i++){
                     
            System.out.println("========================\n" +
                "Iteration: "+i+"\n" +
                "========================");
            System.out.print("Processed:");
            int t=1;
            long start = System.currentTimeMillis();            
            for (DependencyInstance instance : instances){
                
                //optimization for feature vectors creation
                DependencyLabelsFeatureVectors instancefv = fvs.get(t-1);

                int [] labs = dictionary.labels(instance);
                parameter.update(constraint, lossFunction, chooser, instancefv, instance.getDep(), labs);
                v = v.add(parameter.getWeightVector());
                
                t++;
                if ((t)%500==0){
                    System.out.println("\t"+(t));
                }
            }
            if (T%500!=0){
                System.out.println("\t"+T);
            }
            long end = System.currentTimeMillis();
            System.out.println("Training iter took: "+(end-start));
        }
        
        RealVector w = v.mapDivide(numIter*T);
        parameter.setWeightVector(w);
    }
    
    @Override
    public DependencyInstance parse(DependencyInstance instance){
        DependencyLabelsFeatureVectors instancefv = dictionary.featureVectors(instance);
        double [][] scoreTable = parameter.getScoreTable(instancefv);
        int [] labs = KBestChooser.getKBestSequences(scoreTable, 1).get(0);
        
        for (int i=1;i<=instance.getLength();i++)
            instance.getDep_type()[i]=dictionary.label(labs[i]);
        return instance;
    }

    @Override
    public void optimizeForSerialization() {
        parameter.optimizeForSerialization();
    }
}
