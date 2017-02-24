/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.simple.Parameter;
import clemiraparser.labeling.simple.util.SequenceSearch;
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
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, int [] lab, Parameter parameter) {
        double [][] scoreTable = parameter.getScoreTable(instance);
        
        //marking up the scores
        for (int i=0;i<=instance.getN();i++){
            for (int j=1;j<=instance.getN();j++){
                scoreTable[i][j] += lossFunction.loss(dep, lab, i, j);
            }
        }
        return SequenceSearch.getKBestSequences(scoreTable, k);
    }
}
