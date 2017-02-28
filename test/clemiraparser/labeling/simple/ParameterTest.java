/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple;

import clemiraparser.DependencyInstance;
import clemiraparser.dictionary.SimpleDependencyLabelingDictionary;
import clemiraparser.labeling.simple.miraoptimizationproblem.*;
import clemiraparser.util.ArraySparseBinaryVector;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nizami
 */
public class ParameterTest {
    
    public static final double epsilon = 0.1;

    public static void testUpdate(Parameter parameter,
            ConstraintType constraintType,
            LossFunction lossFunction,
            Chooser chooser,
            DependencyLabelsFeatureVectors instance,
            int[] dep,
            int[] lab) {

        List<int[]> preds = chooser.choosePredictions(instance, dep, lab, parameter);

        parameter.update(constraintType, lossFunction, chooser, instance, dep, lab);

        double[] b = constraintType.getb(lossFunction, dep, lab, preds);
        RealVector[] A = constraintType.getA(lossFunction, instance, lab, preds);

        assert (b.length == A.length);
        for (int i = 0; i < b.length; i++) {
            Assert.assertTrue("preds[" + i % preds.size() + "]: " + preds.get(i % preds.size()) + ","
                    + "A[" + i + "]: " + A[i] + ",\n"
                    + " b[" + i + "]: " + b[i] + ",\n"
                    + "dotProduct: " + parameter.getScore(A[i]), parameter.getScore(A[i]) - b[i] <= epsilon);
        }
    }

    @Test
    public void test1() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new MIRAConstraintType(), new McDonaldHammingLoss(), new KBestChooser(3), fvs1, dep1, lab1);
    }

    @Test
    public void test2() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new ModifiedConstraintType(1.8), new McDonaldHammingLoss(), new KBestChooser(3), fvs1, dep1, lab1);
    }

    @Test
    public void test3() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new MIRAConstraintType(), new RootPreferredLossFunction(1.4), new KBestChooser(3), fvs1, dep1, lab1);
    }

    @Test
    public void test4() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new MIRAConstraintType(), new McDonaldHammingLoss(), new KBestLWorstChooser(3, 4), fvs1, dep1, lab1);
    }

    @Test
    public void test5() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new MIRAConstraintType(), new McDonaldHammingLoss(), new KLossMarkedUpBestChooser(3, new McDonaldHammingLoss()), fvs1, dep1, lab1);
    }

    @Test
    public void test6() {
        testUpdate(new Parameter(fvs1.featureVectorDimension), new ModifiedConstraintType(1.4), new RootPreferredLossFunction(1.3), new KLossMarkedUpBestChooser(3, new McDonaldHammingLoss()), fvs1, dep1, lab1);
    }
    
    public static DependencyInstance inst1 = new DependencyInstance("1	Klewonan	_	PROPN	_	_	0	root	_	_\n" +
"2	5	_	NUM	_	_	1	nummod	_	_\n" +
"3	.	_	PUNCT	_	_	1	punct	_	_");
    public static SimpleDependencyLabelingDictionary inst2dict(){
        SimpleDependencyLabelingDictionary  ret = new SimpleDependencyLabelingDictionary();
        List<DependencyInstance> inst = new ArrayList<>();
        inst.add(inst1);
        ret.add(inst);
        try {
            ret.addFromFile("data/id-ud-train.conllu");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ParameterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    public static SimpleDependencyLabelingDictionary dict1 = inst2dict();
    DependencyLabelsFeatureVectors fvs1 = dict1.featureVectors(inst1);
    int [] dep1 = inst1.getDep();
    int [] lab1 = dict1.labels(inst1);
    
}
