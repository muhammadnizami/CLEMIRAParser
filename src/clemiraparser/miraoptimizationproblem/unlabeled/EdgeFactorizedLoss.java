/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.miraoptimizationproblem.unlabeled;

/**
 *
 * @author nizami
 */
public abstract class EdgeFactorizedLoss implements LossFunction{

    @Override
    public double loss(int[] data, int[] pred) {
        double tot=0;
        assert(data.length==pred.length);
        for (int i=1;i<data.length;i++){
            tot+=loss(data,pred[i],i);
        }
        return tot;
    }
    
    public abstract double loss(int[] data, int ppred, int cpred);
    
}
