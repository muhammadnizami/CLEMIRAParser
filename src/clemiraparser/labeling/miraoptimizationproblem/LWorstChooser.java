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
public class LWorstChooser implements Chooser{

    int l;
    
    public LWorstChooser(int l){
        this.l = l;
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, Parameter parameter) throws Exception{
        ViterbiProblem viterbiProblem = parameter.getViterbiProblem(instance);
        viterbiProblem.negate();
        List<int[]> retval = viterbiProblem.lBestPathsNaive(l);
        return retval;
    }
    
}

