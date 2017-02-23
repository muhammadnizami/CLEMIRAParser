/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.markov1o.miraoptimizationproblem;

import clemiraparser.labeling.markov1o.DependencyLabelsFeatureVectors;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public interface ConstraintType {
    public double[] getb(LossFunction lossFunction, int [] act, List<int[]> preds);
    public RealVector[] getA(LossFunction lossFunction, DependencyLabelsFeatureVectors fvs,
            int [] act,
            List<int[]> preds);
    
}
