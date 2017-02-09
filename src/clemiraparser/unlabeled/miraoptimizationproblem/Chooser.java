/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.unlabeled.miraoptimizationproblem;

import clemiraparser.unlabeled.DependencyInstanceFeatureVectors;
import clemiraparser.unlabeled.Parameter;
import java.util.List;

/**
 *
 * @author nizami
 */
public interface Chooser {
    public List<int[]> choosePredictions(DependencyInstanceFeatureVectors instance, int [] dep, Parameter p);
}
