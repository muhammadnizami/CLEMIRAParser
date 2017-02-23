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
public class McDonaldHammingLoss extends EdgeFactorizedLoss{

    @Override
    public double loss(int [] dep, int [] data, int i, int l) {
        return l!=data[i]?1:0;
    }

    
}
