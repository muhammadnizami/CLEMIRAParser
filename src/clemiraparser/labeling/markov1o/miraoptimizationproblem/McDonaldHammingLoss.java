/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.markov1o.miraoptimizationproblem;

import clemiraparser.unlabeled.miraoptimizationproblem.*;

/**
 *
 * @author nizami
 */
public class McDonaldHammingLoss extends EdgeFactorizedLoss{

    @Override
    public double loss(int [] data, int ppred, int cpred) {
        return ppred!=data[cpred]?1:0;
    }

    
}
