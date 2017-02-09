/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.unlabeled.UnlabeledParser;
import java.util.List;

/**
 *
 * @author nizami
 */
public class TwoStageParser extends CLEMIRAParser{
    
    UnlabeledParser unlabeledParser;

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {
        System.out.println("===unlabeled parser===");
        unlabeledParser = new UnlabeledParser();
        unlabeledParser.train(instances);
    }

    @Override
    public DependencyInstance parse(DependencyInstance instance) {
        DependencyInstance unlabeledDep = unlabeledParser.parse(instance);
        return unlabeledDep;
    }

    @Override
    public void optimizeForSerialization() {
        unlabeledParser.optimizeForSerialization();
    }
    
}
