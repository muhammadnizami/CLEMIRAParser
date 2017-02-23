/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.miraoptimizationproblem;

import clemiraparser.labeling.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.Parameter;
import clemiraparser.util.ViterbiProblem;
import java.util.List;

/**
 *
 * @author nizami
 */
public class KBestChooser implements Chooser{

    int k;
    
    public KBestChooser(int k){
        this.k = k;
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, Parameter parameter) throws Exception{
        ViterbiProblem viterbiProblem = parameter.getViterbiProblem(instance);
        List<int[]> retval = viterbiProblem.lBestPathsNaive(k);
        return retval;
    }
    
}
