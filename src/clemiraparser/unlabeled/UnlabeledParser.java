/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.unlabeled;

import clemiraparser.CLEMIRAParser;
import clemiraparser.DependencyInstance;
import clemiraparser.dictionary.MSTParserUnlabeledDependencyDictionary;
import clemiraparser.util.MySparseVector;
import clemiraparser.unlabeled.miraoptimizationproblem.ConstraintType;
import clemiraparser.unlabeled.miraoptimizationproblem.LossFunction;
import clemiraparser.unlabeled.miraoptimizationproblem.KBestChooser;
import clemiraparser.unlabeled.miraoptimizationproblem.MIRAConstraintType;
import clemiraparser.unlabeled.miraoptimizationproblem.McDonaldHammingLoss;
import clemiraparser.unlabeled.miraoptimizationproblem.Chooser;
import clemiraparser.dictionary.UnlabeledDependencyDictionary;
import clemiraparser.unlabeled.miraoptimizationproblem.EdgeFactorizedLoss;
import clemiraparser.unlabeled.miraoptimizationproblem.KBestLWorstChooser;
import clemiraparser.unlabeled.miraoptimizationproblem.KLossMarkedUpBestChooser;
import clemiraparser.unlabeled.miraoptimizationproblem.LWorstChooser;
import clemiraparser.unlabeled.miraoptimizationproblem.ModifiedConstraintType;
import clemiraparser.unlabeled.miraoptimizationproblem.RootPreferredLossFunction;
import com.google.common.collect.ImmutableMap;
import edu.cmu.cs.ark.cle.Arborescence;
import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.ark.cle.KBestArborescences;
import edu.cmu.cs.ark.cle.graph.DenseWeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class UnlabeledParser extends CLEMIRAParser{
    public static final long serialVersionUID = -1095251421236354213L;
    
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
    public static Parameter parameter(int dim){
        if (scoreFunction.equals("original")){
            return new Parameter(dim);
        }else if (scoreFunction.equals("rootrelaxed")){
            return new RootRelaxedParameter(dim, scoreGamma);
        }else {
            throw new IllegalArgumentException("unknown score function " + scoreFunction);
        }
    }
    
    UnlabeledDependencyDictionary dictionary;
    Parameter parameter;
    

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {

            long start = System.currentTimeMillis();
            UnlabeledDependencyDictionary dictionary = new MSTParserUnlabeledDependencyDictionary();
            dictionary.add(instances);
            
            long end = System.currentTimeMillis();
            System.out.println("creating dictionary took: " + (end-start));
            System.out.println("Num Feats: " + dictionary.getSize());
                        
            System.out.println("training...");
            train(dictionary, instances, lossFunction(), chooser(), constraint(), numIters);
            System.out.println("done");
    }
    
    public void train(UnlabeledDependencyDictionary dictionary,
            List<DependencyInstance> instances,
            LossFunction lossFunction,
            Chooser chooser,
            ConstraintType constraint,
            int numIter){
        this.dictionary = dictionary;
        parameter = parameter(dictionary.getSize());
        int T = instances.size();
        RealVector v = new MySparseVector(dictionary.getSize());
        
        //optimization for feature vectors creation
        List<DependencyInstanceFeatureVectors> fvs = new ArrayList<>(instances.size());
        int count = 0;
        System.out.print("creating feature Vectors");
        long fvstart = System.currentTimeMillis();
        for (DependencyInstance instance : instances){
            System.out.print(" " + count);count++;
            DependencyInstanceFeatureVectors featureVector = dictionary.featureVectors(instance);
            fvs.add(featureVector);
        }
        long fvend = System.currentTimeMillis();
        System.out.println("done");
        System.out.println("creating feature Vectors took: " +(fvend-fvstart));
              
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
                DependencyInstanceFeatureVectors instancefv = fvs.get(t-1);

                parameter.update(constraint, lossFunction, chooser, instancefv, instance.getDep());
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
    public static int sizeof(Object obj) throws IOException {

        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);

        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
        objectOutputStream.close();

        return byteOutputStream.toByteArray().length;
    }
    
    public List<DependencyInstance> parse(DependencyInstance instance, int k){
        DependencyInstanceFeatureVectors instancefv = dictionary.featureVectors(instance);
                double [][] scoreTable = parameter.getScoreTable(instancefv);
        DenseWeightedGraph g = DenseWeightedGraph.from(scoreTable);
        
        List<Weighted<Arborescence<Integer>>> kBestArborescences = KBestArborescences.getKBestArborescences(g, 0, k);
        
        List<DependencyInstance> retval = new ArrayList<>(k);
        for (Weighted<Arborescence<Integer>> arborescence : kBestArborescences){
            int [] deppred = new int[instancefv.getN()+1];
            ImmutableMap<Integer,Integer> parents = arborescence.val.parents;
            for (int i=1;i<=instancefv.getN();i++){
                deppred[i]=parents.get(i);
            }
            retval.add(new DependencyInstance(instance.getLength(),instance.getWord(),
            instance.getPos(),deppred));
        }
        return retval;
    }
    
    @Override
    public DependencyInstance parse(DependencyInstance instance){
        DependencyInstanceFeatureVectors instancefv = dictionary.featureVectors(instance);
        double[][] scoreTable = parameter.getScoreTable(instancefv);
        DenseWeightedGraph g = DenseWeightedGraph.from(scoreTable);
        Weighted<Arborescence<Integer>> maxArborescence = ChuLiuEdmonds.getMaxArborescence(g, 0);
        
        int [] deppred = new int[instance.getLength()+1];
        ImmutableMap<Integer,Integer> parents = maxArborescence.val.parents;
        for (int i=1;i<=instance.getLength();i++){
            deppred[i]=parents.get(i);
        }
        DependencyInstance retval = new DependencyInstance(instance.getLength(), instance.getWord(), instance.getPos(), deppred);
        
        return retval;
    }

    @Override
    public void optimizeForSerialization() {
        parameter.optimizeForSerialization();
    }
}
