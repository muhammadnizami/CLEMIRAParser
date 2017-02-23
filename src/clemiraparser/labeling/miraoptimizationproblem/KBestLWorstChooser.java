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
public class KBestLWorstChooser implements Chooser {
    
    Chooser kBestChooser;
    Chooser lWorstChooser;
    
    public KBestLWorstChooser(int k, int l){
        kBestChooser = new KBestChooser(k);
        lWorstChooser = new LWorstChooser(l);
    }

    @Override
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int[] dep, Parameter p) throws Exception {
        List<int[]> a = kBestChooser.choosePredictions(instance, dep, p);
        List<int[]> b = lWorstChooser.choosePredictions(instance, dep, p);
        a.addAll(b);
        return a;
    }
    
}
