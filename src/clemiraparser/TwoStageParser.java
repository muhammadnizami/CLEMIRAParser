/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.labeling.DependencyLabeler;
import clemiraparser.unlabeled.UnlabeledParser;
import java.util.List;

/**
 *
 * @author nizami
 */
public class TwoStageParser extends CLEMIRAParser{
    
    UnlabeledParser unlabeledParser;
    DependencyLabeler dependencyLabeler;

    @Override
    public void train(List<DependencyInstance> instances) throws Exception {
        System.out.println("===unlabeled parser===");
        unlabeledParser = new UnlabeledParser();
        unlabeledParser.train(instances);
        System.out.println("===dependency labeler===");
        dependencyLabeler = new DependencyLabeler();
        dependencyLabeler.train(instances);
    }

    @Override
    public DependencyInstance parse(DependencyInstance instance) {
        DependencyInstance unlabeledDep = unlabeledParser.parse(instance);
        DependencyInstance labeledDep = dependencyLabeler.parse(unlabeledDep);
        return labeledDep;
    }

    @Override
    public void optimizeForSerialization() {
        unlabeledParser.optimizeForSerialization();
    }
    
}
