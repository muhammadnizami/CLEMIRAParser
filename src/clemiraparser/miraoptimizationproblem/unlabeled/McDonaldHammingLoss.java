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
public class McDonaldHammingLoss extends EdgeFactorizedLoss{

    @Override
    public double loss(int [] data, int ppred, int cpred) {
        return ppred!=data[cpred]?1:0;
    }

    
}
