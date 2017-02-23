/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling;

import clemiraparser.quadraticprogramming.HildrethSolver;
import clemiraparser.quadraticprogramming.HildrethSolver2;
import clemiraparser.labeling.miraoptimizationproblem.Chooser;
import clemiraparser.labeling.miraoptimizationproblem.ConstraintType;
import clemiraparser.labeling.miraoptimizationproblem.LossFunction;
import clemiraparser.util.MySparseVector;
import clemiraparser.util.ViterbiProblem;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class Parameter implements Serializable{
    public static final long serialVersionUID = 0L;
    RealVector weightVector;
    int n;
    
    public Parameter(int n){
        this.n = n;
        weightVector = new MySparseVector(n);
    }
    
    public RealVector getWeightVector(){
        return weightVector;
    }
    
    public void setWeightVector(RealVector rv){
        if (rv.getDimension()!=n)
            throw new DimensionMismatchException(n,rv.getDimension());
        weightVector = rv;
    }
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, List<DependencyLabelsFeatureVectors> instances, List<int[]> labs, int N) throws Exception{
        if (instances.size() != labs.size())
            throw new DimensionMismatchException(instances.size(), labs.size());
        
        int T = instances.size();
        
        RealVector v = new MySparseVector(n);
        for (int i=0;i<N;i++){
            System.out.println("========================\n" +
"Iteration: "+i+"\n" +
"========================");
            System.out.print("Processed:");
            long start = System.currentTimeMillis();
            for (int t=0;t<T;t++){
                update(constraintType,lossFunction,chooser,instances.get(t),labs.get(t));
                v = v.add(weightVector);
                if ((t+1)%500==0){
                    System.out.println("\t"+(t+1));
                }
            }
            if (T%500!=0){
                System.out.println("\t"+T);
            }
            long end = System.currentTimeMillis();
            System.out.println("Training iter took: "+(end-start));
        }
        
        setWeightVector(v.mapDivide(N*T));
    }
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, DependencyLabelsFeatureVectors instance, int [] lab) throws Exception{
        List<int[]> chosenPreds = chooser.choosePredictions(instance, lab, this);
        
        RealVector [] A = constraintType.getA(lossFunction, instance, lab, chosenPreds);
        double [] b = constraintType.getb(lossFunction, lab, chosenPreds);
        
        HildrethSolver hildrethSolver = new HildrethSolver2();
        hildrethSolver.solveAddToX_0(weightVector, A, b);    }
    
    public double getScore(RealVector featureVector){
        return featureVector.dotProduct(weightVector);
    }
    
    public ViterbiProblem getViterbiProblem(DependencyLabelsFeatureVectors instance){
        int numNodesPerLayer = instance.getNumNodesPerLayer();
        int numLayers = instance.getNumLayers();
        ViterbiProblem viterbiProblem = new ViterbiProblem(numNodesPerLayer);
        
        //initial scores
        double[] initialScores = new double[numNodesPerLayer];
        for (int i=0;i<numNodesPerLayer;i++){
            initialScores[i] = getScore(instance.getInitialFeatureVectors()[i]);
        }
        viterbiProblem.setInitialScores(initialScores);
        
        //transition scores
        for (int i=1;i<numLayers;i++){
            RealVector[][] transitionFeatureVectors = instance.getTransitionFeatureVectors().get(i-1);
            
            double [][] transitionScores = new double[numNodesPerLayer][numNodesPerLayer];
            for (int k=0;k<numNodesPerLayer;k++){
                for (int l=0;l<numNodesPerLayer;l++){
                    transitionScores[k][l]=getScore(transitionFeatureVectors[k][l]);
                }
            }
            viterbiProblem.grow(transitionScores);
        }
        
        return viterbiProblem;
    }
    
    
    /**
     * changes the vector to ArrayRealVector which is faster for serialization
     */
    public void optimizeForSerialization(){
        double [] w = weightVector.toArray();
        setWeightVector(new ArrayRealVector(w));
    }
    
}
