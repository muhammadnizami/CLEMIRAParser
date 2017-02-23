/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.miraoptimizationproblem;

import clemiraparser.labeling.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.miraoptimizationproblem.*;
import clemiraparser.labeling.Parameter;
import java.util.List;

/**
 *
 * @author nizami
 */
public interface Chooser {
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, Parameter p) throws Exception;
}
