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
public abstract class EdgeFactorizedLoss implements LossFunction{

    @Override
    public double loss(int [] dep, int[] data, int[] pred) {
        double tot=0;
        assert(data.length==pred.length);
        for (int i=1;i<data.length;i++){
            tot+=loss(dep, data,i,pred[i]);
        }
        return tot;
    }
    
    public abstract double loss(int [] dep, int[] data, int i, int l);
    
}
