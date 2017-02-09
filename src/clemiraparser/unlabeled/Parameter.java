/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.unlabeled;

import clemiraparser.util.MySparseVector;
import clemiraparser.quadraticprogramming.HildrethSolver2;
import clemiraparser.quadraticprogramming.HildrethSolver;
import clemiraparser.unlabeled.miraoptimizationproblem.ConstraintType;
import clemiraparser.unlabeled.miraoptimizationproblem.LossFunction;
import clemiraparser.unlabeled.miraoptimizationproblem.Chooser;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class Parameter implements java.io.Serializable{
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
    
    public RealVector nextWeightVector(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, DependencyInstanceFeatureVectors instance, int [] dep){
        List<int[]> chosenPreds = chooser.choosePredictions(instance, dep, this);
        
        RealVector [] A = constraintType.getA(lossFunction, instance, dep, chosenPreds);
        double [] b = constraintType.getb(lossFunction, dep, chosenPreds);
        
        HildrethSolver hildrethSolver = new HildrethSolver2();
        RealVector nextWeightVector = hildrethSolver.solve(weightVector, A, b);
        
        return nextWeightVector;
        
    }
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, List<DependencyInstanceFeatureVectors> instances, List<int[]> deps, int N){
        if (instances.size() != deps.size())
            throw new DimensionMismatchException(instances.size(), deps.size());
        
        int T = instances.size();
        
        RealVector v = new MySparseVector(n);
        for (int i=0;i<N;i++){
            System.out.println("========================\n" +
"Iteration: "+i+"\n" +
"========================");
            System.out.print("Processed:");
            long start = System.currentTimeMillis();
            for (int t=0;t<T;t++){
                update(constraintType,lossFunction,chooser,instances.get(t),deps.get(t));
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
    
    public void update(ConstraintType constraintType, LossFunction lossFunction, Chooser chooser, DependencyInstanceFeatureVectors instance, int [] dep){
        setWeightVector(nextWeightVector(constraintType, lossFunction, chooser, instance, dep));
    }
    
    public double getScore(RealVector featureVector){
        return featureVector.dotProduct(weightVector);
    }
    
    public double [][] getScoreTable(DependencyInstanceFeatureVectors instance){
        double [][] scoreTable = new double[instance.getN()+1][instance.getN()+1];
        //everywhere else to root
        
        
        for (int i=1;i<=instance.getN();i++){
            scoreTable[i][0]=0;
        }
        for (int i=0;i<=instance.getN();i++){
            for (int j=1;j<=instance.getN();j++){
                RealVector featureVector = instance.getEdgeFeatureVector(i, j);
                scoreTable[i][j]=getScore(featureVector);
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
