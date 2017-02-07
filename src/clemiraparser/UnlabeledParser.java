/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.util.MySparseVector;
import clemiraparser.miraoptimizationproblem.unlabeled.ConstraintType;
import clemiraparser.miraoptimizationproblem.unlabeled.LossFunction;
import clemiraparser.miraoptimizationproblem.unlabeled.KBestChooser;
import clemiraparser.miraoptimizationproblem.unlabeled.MIRAConstraintType;
import clemiraparser.miraoptimizationproblem.unlabeled.McDonaldHammingLoss;
import clemiraparser.miraoptimizationproblem.unlabeled.Chooser;
import clemiraparser.dictionary.UnlabeledDependencyDictionary;
import clemiraparser.miraoptimizationproblem.unlabeled.EdgeFactorizedLoss;
import clemiraparser.miraoptimizationproblem.unlabeled.KBestLWorstChooser;
import clemiraparser.miraoptimizationproblem.unlabeled.KLossMarkedUpBestChooser;
import clemiraparser.miraoptimizationproblem.unlabeled.LWorstChooser;
import clemiraparser.miraoptimizationproblem.unlabeled.ModifiedConstraintType;
import clemiraparser.miraoptimizationproblem.unlabeled.RootPreferredLossFunction;
import com.google.common.collect.ImmutableMap;
import edu.cmu.cs.ark.cle.Arborescence;
import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.ark.cle.graph.DenseWeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class UnlabeledParser extends CLEMIRAParser{
    
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
    
    UnlabeledDependencyDictionary dictionary;
    Parameter parameter;
    

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {

            long start = System.currentTimeMillis();
            UnlabeledDependencyDictionary dictionary = new UnlabeledDependencyDictionary();
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
        parameter = new Parameter(dictionary.getSize());
        int T = instances.size();
        RealVector v = new MySparseVector(dictionary.getSize());
        
        for (int i=0;i<numIter;i++){
            
            System.out.println("========================\n" +
                "Iteration: "+i+"\n" +
                "========================");
            System.out.print("Processed:");
            int t=1;
            long start = System.currentTimeMillis();            
            for (DependencyInstance instance : instances){
                DependencyInstanceFeatureVectors instancefv = dictionary.featureVectors(instance);
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
