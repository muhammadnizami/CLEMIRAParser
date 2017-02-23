/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.markov1o.miraoptimizationproblem;

import clemiraparser.labeling.markov1o.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.markov1o.Parameter;
import clemiraparser.util.ViterbiProblem;
import java.util.List;

/**
 *
 * @author nizami
 */
public class KLossMarkedUpBestChooser implements Chooser{

    int k;
    EdgeFactorizedLoss lossFunction;
    
    public KLossMarkedUpBestChooser(int k, EdgeFactorizedLoss lossFunction){
        this.k = k;
        this.lossFunction=lossFunction;
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, Parameter parameter) throws Exception{
        ViterbiProblem viterbiProblem = parameter.getViterbiProblem(instance);
        double [] initialScores = viterbiProblem.getInitialScores();
        List<double[][]> transitionScores = viterbiProblem.getTransitionScores();
        //marking up the scores
        for (int i=0;i<initialScores.length;i++){
            initialScores[i] += lossFunction.loss(dep,0,i);
        }
        for (int i=0;i<=transitionScores.size();i++){
            double [][] transitionScore = transitionScores.get(i);
            for (int j=0;j<=transitionScore.length;j++){
                for (int k=0;k<=transitionScore[j].length;k++){
                    transitionScore[j][k] += lossFunction.loss(dep, i,k);
                }
            }
        } 
        List<int[]> retval = viterbiProblem.lBestPathsNaive(k);
        
        return retval;
    }
}
