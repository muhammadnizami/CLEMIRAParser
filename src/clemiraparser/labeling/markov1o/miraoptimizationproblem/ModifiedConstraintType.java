/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.markov1o.miraoptimizationproblem;

import clemiraparser.labeling.markov1o.DependencyLabelsFeatureVectors;
import clemiraparser.unlabeled.DependencyInstanceFeatureVectors;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class ModifiedConstraintType implements ConstraintType{
    
    double alpha;
    
    public ModifiedConstraintType(double alpha){
        if (alpha<1){
            throw new IllegalArgumentException("alpha must be >= 1. value: " + alpha);
        }
        this.alpha=alpha;
    }

    @Override
    public double[] getb(LossFunction lossFunction, int[] act, List<int[]> preds) {
        int predsSize = preds.size();
        int bSize = preds.size()*2;
        double[] loss = new double[predsSize];
        for (int i=0;i<predsSize;i++){
            loss[i]=lossFunction.loss(act, preds.get(i));
        }
        
        double[] b = new double[bSize];
        for (int i=0;i<predsSize;i++){
            double entry = -loss[i];
            b[i]= entry;
        }
        for (int i=predsSize;i<bSize;i++){
            double entry = alpha*loss[i-predsSize];
            b[i]=entry;
        }
        return b;
    }

    @Override
    public RealVector[] getA(LossFunction lossFunction, DependencyLabelsFeatureVectors fvs,
            int [] act,
            List<int[]> preds){
        int predsSize = preds.size();
        int ASize = preds.size()*2;
        
        RealVector [] lossVectors = new RealVector[predsSize];
        for (int i=0;i<predsSize;i++){
            int [] pred = preds.get(i);
            lossVectors[i]=fvs.lossVector(act, pred);
        }
        
        RealVector[] retval = new RealVector[ASize];
        
        for (int i=0;i<predsSize;i++){
            retval[i]=lossVectors[i].mapMultiply(-1);
        }
        
        for (int i=predsSize;i<ASize;i++){
            retval[i]=lossVectors[i-predsSize];
        }
        return retval;
    }
    
}
