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
public class RootPreferredLossFunction extends EdgeFactorizedLoss{
    
    double lambda;
    
    public RootPreferredLossFunction(double lambda){
        if (lambda<=1){
            throw new IllegalArgumentException("lambda must be >1. value: " + lambda);
        }
        this.lambda=lambda;
    }

    @Override
    public double loss(int [] data, int ppred, int cpred) {
        return ppred!=data[cpred]?(ppred==0?lambda:1):0;
    }
}
