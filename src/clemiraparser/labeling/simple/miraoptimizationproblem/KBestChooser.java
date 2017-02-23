/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.miraoptimizationproblem;

import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.labeling.simple.DependencyLabelsFeatureVectors;
import clemiraparser.labeling.simple.Parameter;
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
        return getKBestSequences(scoreTable,k);
    }
    
    public static List<int[]> getKBestSequences(double [][] scoreTable, int k){
        
        //preparations
        int length = scoreTable.length;
        List<List<Weighted<Integer>>> bestLabels = new ArrayList<>(length);
        for (int i=0;i<length;i++){
            List<Weighted<Integer>> l = new ArrayList<>(scoreTable[i].length);
            for (int j=0;j<scoreTable[i].length;j++){
                l.add(new Weighted<>(j,scoreTable[i][j]));
            }
            Collections.sort(l);
            bestLabels.add(l);
        }
        
        //parsing
        List<int[]> ret = new ArrayList<>(k);
        
        PriorityQueue<Weighted<int[]>> bestLabelsIndexQueue = Queues.newPriorityQueue();
        int[] firstBestLabels = new int[length];
        double firstBestLabelsWeight = 0;
        for (int i=1;i<length;i++){
            firstBestLabels[i] = 0;
            firstBestLabelsWeight+=bestLabels.get(i).get(0).weight;
        }
        bestLabelsIndexQueue.add(new Weighted<>(firstBestLabels,firstBestLabelsWeight));
        
        while (ret.size()<k && !bestLabelsIndexQueue.isEmpty()){
            Weighted<int[]> bestLabelsIndex = bestLabelsIndexQueue.poll();
            //add to ret
            int [] labels = new int[scoreTable.length];
            labels[0]=-1;
            for (int i=1;i<length;i++){
                int index = bestLabelsIndex.val[i];
                labels[i] = bestLabels.get(i).get(index).val;
            }
            ret.add(labels);
            
            //add to queue
            for (int i=1;i<length;i++){
                if (bestLabelsIndex.val[i]+1<bestLabels.get(i).size()){
                    int [] newBestLabelsIndex = bestLabelsIndex.val.clone();
                    newBestLabelsIndex[i]++;
                    double weightDifference = bestLabels.get(i).get(newBestLabelsIndex[i]).weight
                            - bestLabels.get(i).get(bestLabelsIndex.val[i]).weight;
                    bestLabelsIndexQueue.add(new Weighted<>(newBestLabelsIndex, bestLabelsIndex.weight+weightDifference));
                }
            }
        }
        return ret;
    }    
}
