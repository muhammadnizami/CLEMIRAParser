/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling;


import clemiraparser.CLEMIRAParser;
import clemiraparser.DependencyInstance;
import clemiraparser.dictionary.DependencyLabelingDictionary;
import clemiraparser.labeling.miraoptimizationproblem.Chooser;
import clemiraparser.labeling.miraoptimizationproblem.ConstraintType;
import clemiraparser.labeling.miraoptimizationproblem.KBestChooser;
import clemiraparser.labeling.miraoptimizationproblem.LossFunction;
import clemiraparser.labeling.miraoptimizationproblem.MIRAConstraintType;
import clemiraparser.labeling.miraoptimizationproblem.McDonaldHammingLoss;
import clemiraparser.util.MySparseVector;
import clemiraparser.util.ViterbiProblem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class DependencyLabeler extends CLEMIRAParser{
    
    DependencyLabelingDictionary dictionary;
    Parameter parameter;

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {
        long start = System.currentTimeMillis();
        DependencyLabelingDictionary dictionary = new DependencyLabelingDictionary();
        dictionary.add(instances);
        long end = System.currentTimeMillis();
        System.out.println("creating dictionary took: " + (end-start));
        System.out.println("Num Feats: " + dictionary.getSize());

        System.out.println("training...");
        train(dictionary, instances, new McDonaldHammingLoss(), new KBestChooser(1), new MIRAConstraintType(), numIters);
        System.out.println("done");
    }
    
    public void train(DependencyLabelingDictionary dictionary,
            List<DependencyInstance> instances,
            LossFunction lossFunction,
            Chooser chooser,
            ConstraintType constraint,
            int numIter) throws Exception{
        this.dictionary = dictionary;
        parameter = new Parameter(dictionary.getSize());
        int T = instances.size();
        int numUpdates=0;
        RealVector v = new MySparseVector(dictionary.getSize());
                
        //optimization for feature vectors creation
        List<List<DependencyLabelsFeatureVectors>> fvs = new ArrayList<>(instances.size());
        int count = 0;
        System.out.print("creating feature Vectors");
        for (DependencyInstance instance : instances){
            List<DependencyLabelsFeatureVectors> nodefvs = new ArrayList<>();
            for (int j=0;j<=instance.getLength();j++){
                DependencyLabelsFeatureVectors instancefv = dictionary.featureVectors(instance, j);
                nodefvs.add(instancefv);
            }
            fvs.add(nodefvs);
            System.out.print(" " + count);count++;
        }
        
        for (int i=0;i<numIter;i++){
            
            System.out.println("========================\n" +
                "Iteration: "+i+"\n" +
                "========================");
            System.out.print("Processed:");
            int t=1;
            int iterNumUpdates=0;
            long start = System.currentTimeMillis();
            for (DependencyInstance instance : instances){
                List<DependencyLabelsFeatureVectors> nodefvs = fvs.get(t-1);
                for (int j=0;j<=instance.getLength();j++){
                    DependencyLabelsFeatureVectors instancefv = nodefvs.get(j);
                    if (instancefv!=null){
                        int [] labs = dictionary.labels(instance, j);
                        parameter.update(constraint, lossFunction, chooser, instancefv, labs);
                        v = v.add(parameter.getWeightVector());
                        iterNumUpdates++;
                    }
                }

                t++;
                if ((t)%500==0){
                    System.out.println("\t"+(t)+"\t"+iterNumUpdates);
                }
            }
            if (T%500!=0){
                System.out.println("\t"+(t)+"\t"+iterNumUpdates);
            }
            numUpdates+=iterNumUpdates;
            long end = System.currentTimeMillis();
            System.out.println("Training iter took: "+(end-start));
        }
        
        RealVector w = v.mapDivide(numUpdates);
        parameter.setWeightVector(w);
    }

    @Override
    public void optimizeForSerialization() {
        parameter.optimizeForSerialization();
    }

    @Override
    public DependencyInstance parse(DependencyInstance instance) {
        DependencyInstance ret = new DependencyInstance(instance.getLength(), instance.getWord(), instance.getPos(), instance.getDep());
        for (int i=0;i<=instance.getLength();i++){
            DependencyLabelsFeatureVectors fv = dictionary.featureVectors(instance, i);
            if (fv!=null){
                ViterbiProblem viterbiProblem = parameter.getViterbiProblem(fv);
                int [] viterbi_labs = viterbiProblem.bestPath();

                //applying the labels
                int k=0;
                for (int j=1;j<=instance.getLength();j++){
                    if (instance.getDep()[j]==i){
                        int lab = viterbi_labs[k];
                        ret.getDep_type()[j]=dictionary.label(lab);
                        k++;
                    }
                }
            }
        }
        return ret;
    }
    
}
