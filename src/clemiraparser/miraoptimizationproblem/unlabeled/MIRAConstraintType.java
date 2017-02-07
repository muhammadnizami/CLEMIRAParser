/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.miraoptimizationproblem.unlabeled;

import clemiraparser.DependencyInstanceFeatureVectors;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class MIRAConstraintType implements ConstraintType{
    
    public MIRAConstraintType(){}

    @Override
    public double[] getb(LossFunction lossFunction, int[] act, List<int[]> preds) {
        double[] b = new double[preds.size()];
        for (int i=0;i<preds.size();i++){
            double entry = -lossFunction.loss(act, preds.get(i));
            b[i]= entry;
        }
        return b;
    }

    @Override
    public RealVector[] getA(LossFunction lossFunction, DependencyInstanceFeatureVectors fvs,
            int [] act,
            List<int[]> preds){
        
        RealVector[] retval = new RealVector[preds.size()];
        
        for (int i=0;i<preds.size();i++){
            int [] pred = preds.get(i);
            retval[i]=fvs.lossVector(act, pred).mapMultiply(-1);
        }
        return retval;
    }
    
}
