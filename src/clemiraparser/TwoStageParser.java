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
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nizami
 */
public class TwoStageParser extends CLEMIRAParser{
    
    public static final long serialVersionUID = 6408865468514452915L;
    
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
        List<DependencyInstance> unlabeledDeps = unlabeledParser.parse(instance,parseK);
        
        List<Weighted<DependencyInstance>> labeledDeps = new ArrayList<>(parseK);
        for (DependencyInstance unlabeledInstance : unlabeledDeps){
            Weighted<DependencyInstance> labeledDep = dependencyLabeler.parseWithScore(unlabeledInstance);
            labeledDeps.add(labeledDep);
        }
        
        double maxWeight = Double.NEGATIVE_INFINITY;
        DependencyInstance maxLabeledDep = null;
        for (Weighted<DependencyInstance> labeledDep : labeledDeps){
            if (maxWeight < labeledDep.weight){
                maxWeight = labeledDep.weight;
                maxLabeledDep = labeledDep.val;
            }
        }
        return maxLabeledDep;
    }

    @Override
    public void optimizeForSerialization() {
        unlabeledParser.optimizeForSerialization();
    }

    @Override
    public DependencyInstanceScores giveScores(DependencyInstance instance) {
        DependencyInstanceScores depScores = unlabeledParser.giveScores(instance);
        DependencyInstanceScores labelScores = dependencyLabeler.giveScores(instance);
        DependencyInstanceScores allScores = new DependencyInstanceScores(instance.getLength(),instance.getWord(),instance.getPos(),instance.getDep(),instance.getDep_type());
        allScores.setDep_score(depScores.getDep_score());
        allScores.setDep_type_score(labelScores.getDep_type_score());
        return allScores;
    }
    
}
