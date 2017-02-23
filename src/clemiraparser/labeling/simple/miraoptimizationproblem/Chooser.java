/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.simple.Parameter;
import java.util.List;

/**
 *
 * @author nizami
 */
public interface Chooser {
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance,int [] dep, int [] lab, Parameter p);
}
