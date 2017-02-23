/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple;

import clemiraparser.quadraticprogramming.HildrethSolver;
import clemiraparser.quadraticprogramming.HildrethSolver2;
import clemiraparser.labeling.simple.miraoptimizationproblem.*;
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
        weightVector = new ArrayRealVector(n);
    }
    
    public RealVector getWeightVector(){
        return weightVector;
    }
    
    public void setWeightVector(RealVector rv){
        if (rv.getDimension()!=n)
            throw new DimensionMismatchException(n,rv.getDimension());
        weightVector = rv;
    }
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, List<DependencyLabelsFeatureVectors> instances, List<int[]> deps, List<int[]> labs, int N){
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
                update(constraintType,lossFunction,chooser,instances.get(t),deps.get(t),labs.get(t));
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
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, DependencyLabelsFeatureVectors instance, int [] dep, int [] lab){
        List<int[]> chosenPreds = chooser.choosePredictions(instance, dep, lab, this);
        
        RealVector [] A = constraintType.getA(lossFunction, instance, lab, chosenPreds);
        double [] b = constraintType.getb(lossFunction, dep, lab, chosenPreds);
        
        HildrethSolver hildrethSolver = new HildrethSolver2();
        hildrethSolver.solveAddToX_0(weightVector, A, b);    }
    
    public double getScore(RealVector featureVector){
        return featureVector.dotProduct(weightVector);
    }
    
    public double[][] getScoreTable(DependencyLabelsFeatureVectors instance){
        double [][] scoreTable = new double[instance.getN()+1][instance.getNumLabs()];
        //everywhere else to root
        
        for (int i=1;i<=instance.getN();i++){
            for (int l=0;l<instance.getNumLabs();l++){
                RealVector featureVector = instance.getLabelFeatureVector(i, l);
                scoreTable[i][l]=getScore(featureVector);
            }
        }
        
        return scoreTable;
    }
    
    
    /**
     * changes the vector to ArrayRealVector which is faster for serialization
     */
    public void optimizeForSerialization(){
        double [] w = weightVector.toArray();
        setWeightVector(new ArrayRealVector(w));
    }
    
}
