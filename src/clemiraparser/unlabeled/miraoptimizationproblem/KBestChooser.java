/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.unlabeled.miraoptimizationproblem;

import clemiraparser.unlabeled.DependencyInstanceFeatureVectors;
import clemiraparser.unlabeled.Parameter;
import com.google.common.collect.ImmutableMap;
import edu.cmu.cs.ark.cle.Arborescence;
import edu.cmu.cs.ark.cle.KBestArborescences;
import edu.cmu.cs.ark.cle.graph.DenseWeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nizami
 */
public class KBestChooser implements Chooser{

    int k;
    
    public KBestChooser(int k){
        this.k = k;
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyInstanceFeatureVectors instance, int [] dep, Parameter parameter) {
        double [][] scoreTable = parameter.getScoreTable(instance);
        DenseWeightedGraph g = DenseWeightedGraph.from(scoreTable);
        
        List<Weighted<Arborescence<Integer>>> kBestArborescences = KBestArborescences.getKBestArborescences(g, 0, k);
        
        List<int[]> retval = new ArrayList<>(k);
        for (Weighted<Arborescence<Integer>> arborescence : kBestArborescences){
            int [] deppred = new int[instance.getN()+1];
            ImmutableMap<Integer,Integer> parents = arborescence.val.parents;
            for (int i=1;i<=instance.getN();i++){
                deppred[i]=parents.get(i);
            }
            retval.add(deppred);
        }
        return retval;
    }
    
}
