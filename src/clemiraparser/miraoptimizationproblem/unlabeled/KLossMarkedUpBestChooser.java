/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.miraoptimizationproblem.unlabeled;

import clemiraparser.DependencyInstanceFeatureVectors;
import clemiraparser.Parameter;
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
public class KLossMarkedUpBestChooser implements Chooser{

    int k;
    EdgeFactorizedLoss lossFunction;
    
    public KLossMarkedUpBestChooser(int k, EdgeFactorizedLoss lossFunction){
        this.k = k;
        this.lossFunction=lossFunction;
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyInstanceFeatureVectors instance, int [] dep, Parameter parameter) {
        double [][] scoreTable = parameter.getScoreTable(instance);
        
        //marking up the scores
        for (int i=0;i<=instance.getN();i++){
            for (int j=1;j<=instance.getN();j++){
                scoreTable[i][j] += lossFunction.loss(dep, i, j);
            }
        }        
        
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
