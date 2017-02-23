/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import java.util.List;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public interface ConstraintType {
    public double[] getb(LossFunction lossFunction, int [] dep, int [] act, List<int[]> preds);
    public RealVector[] getA(LossFunction lossFunction, DependencyLabelsFeatureVectors fvs,
            int [] act,
            List<int[]> preds);
    
}
