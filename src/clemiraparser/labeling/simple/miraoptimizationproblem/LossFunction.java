/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.unlabeled.miraoptimizationproblem.*;

/**
 *
 * @author nizami
 */
public interface LossFunction {
    public double loss(int [] dep, int[] data, int[] pred);
}
