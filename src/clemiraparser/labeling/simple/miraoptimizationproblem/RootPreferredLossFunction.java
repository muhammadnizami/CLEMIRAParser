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
public class RootPreferredLossFunction extends EdgeFactorizedLoss{
    
    double lambda;
    
    public RootPreferredLossFunction(double lambda){
        if (lambda<=1){
            throw new IllegalArgumentException("lambda must be >1. value: " + lambda);
        }
        this.lambda=lambda;
    }

    @Override
    public double loss(int [] dep, int [] data, int i, int l) {
        return l!=data[i]?(dep[i]==0?lambda:1):0;
    }
}
