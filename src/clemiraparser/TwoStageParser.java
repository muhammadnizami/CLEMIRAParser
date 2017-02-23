/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser;

import clemiraparser.labeling.DependencyLabeler;
import clemiraparser.labeling.markov1o.Markov1ODependencyLabeler;
import clemiraparser.labeling.simple.SimpleDependencyLabeler;
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
        unlabeledParser = new UnlabeledParser();
        if (stages.contains("simple"))
            dependencyLabeler = new SimpleDependencyLabeler();
        else if (stages.contains("markov1o"))
            dependencyLabeler = new Markov1ODependencyLabeler();
        else
            throw new IllegalArgumentException("unknown labeler " + stages.replace("two", "").replace("-", ""));
        System.out.println("===unlabeled parser===");
        unlabeledParser.train(instances);
        System.out.println("===dependency labeler===");
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
