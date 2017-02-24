/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple.util;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nizami
 */
public class SequenceSearchTest {

    @Test
    public void testGetKBestSequences() {
        double[][] scoreTable = new double[][]{{0, 0, 0}, {1, 3, 5}, {2, 4, 5}, {1, 2.5, 3}};
        List<int[]> labs = SequenceSearch.getKBestSequences(scoreTable, 4);
        double prevScore = Double.POSITIVE_INFINITY;
        for (int[] ll : labs) {
            System.out.print("l: ");
            for (int l : ll) {
                System.out.print(" " + l);
            }
            double score = sumWeight(scoreTable, ll);
            System.out.println(", score: " + score);
            Assert.assertTrue(score <= prevScore);
            prevScore = score;
        }
    }

    public double sumWeight(double[][] scoreTable, int[] labs) {
        double sum = 0;
        for (int i = 1; i < labs.length; i++) {
            sum += scoreTable[i][labs[i]];
        }
        return sum;
    }
    
}
