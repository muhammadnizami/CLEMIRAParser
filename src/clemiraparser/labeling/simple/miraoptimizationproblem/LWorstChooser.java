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
import java.util.List;


/**
 *
 * @author nizami
 */
public class LWorstChooser implements Chooser{

    int l;
    
    public LWorstChooser(int l){
        this.l = l;
    }
    
    public static double [][] negate(double[][] m){
        double [][] r = new double[m.length][];
        for (int i=0;i<m.length;i++){
            r[i]=new double[m[i].length];
            for (int j=0;j<m[i].length;j++){
                r[i][j]=-m[i][j];
            }
        }
        
        return r;        
    }
    
    @Override
    public List<int[]> choosePredictions(DependencyLabelsFeatureVectors instance, int [] dep, int [] lab, Parameter parameter) {
        double [][] scoreTable = parameter.getScoreTable(instance);
        double [][] negScoreTable = negate(scoreTable);
        return KBestChooser.getKBestSequences(scoreTable, l);
    }
    
}

