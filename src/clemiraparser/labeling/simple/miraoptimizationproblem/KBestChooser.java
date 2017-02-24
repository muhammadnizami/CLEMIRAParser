/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.simple.Parameter;
import clemiraparser.labeling.simple.util.SequenceSearch;
import clemiraparser.util.ViterbiProblem;
import com.google.common.collect.Queues;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

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
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, int [] lab, Parameter parameter){
        double [][] scoreTable = parameter.getScoreTable(instance);
        return SequenceSearch.getKBestSequences(scoreTable,k);
    }
    
}
