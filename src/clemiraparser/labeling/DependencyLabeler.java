/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling;

import clemiraparser.CLEMIRAParser;
import clemiraparser.DependencyInstance;
import edu.cmu.cs.ark.cle.util.Weighted;

/**
 *
 * @author nizami
 */
public abstract class DependencyLabeler extends CLEMIRAParser {
    public abstract Weighted<DependencyInstance> parseWithScore(DependencyInstance instance);
}
