/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple;

/**
 *
 * @author nizami
 */
public class RootRelaxedParameter extends Parameter{
    
    double gamma;
    
    public RootRelaxedParameter(int n, double gamma) {
        super(n);
        if (gamma>=1)
            throw new IllegalArgumentException("score-gamma must be <1. got: "+ gamma);
        this.gamma = gamma;
    }
    
    @Override
    public double [][] getScoreTable(DependencyLabelsFeatureVectors instance){
        double [][] scoreTable = super.getScoreTable(instance);
        for (int j=0;j<scoreTable[0].length;j++)
            scoreTable[0][j]*=gamma;
        return scoreTable;
    }
    
}