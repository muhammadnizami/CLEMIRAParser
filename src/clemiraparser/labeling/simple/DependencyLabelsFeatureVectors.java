/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.simple;

import clemiraparser.util.ArraySparseBinaryVector;
import clemiraparser.util.MySparseVector;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class DependencyLabelsFeatureVectors{
    ArraySparseBinaryVector [][] labelFeatureVectors;
    private final int n;
    private final int numLabs;
    final int featureVectorDimension;
    
    public DependencyLabelsFeatureVectors(int n, int numLabs, int featureVectorDimension){
        this.n = n;
        this.numLabs = numLabs;
        labelFeatureVectors = new ArraySparseBinaryVector[n+1][numLabs];
        this.featureVectorDimension = featureVectorDimension;
    }
    
    public void setLabelFeatureVector(int i, int l, ArraySparseBinaryVector labelFeatureVector){
        if (labelFeatureVector.getDimension()!=featureVectorDimension)
            throw new DimensionMismatchException(labelFeatureVector.getDimension(),featureVectorDimension);
        
        checkRange(i,l);
        labelFeatureVectors[i][l] = labelFeatureVector;
    }
    
    public RealVector getLabelFeatureVector(int i, int l){
        checkRange(i,l);
        return labelFeatureVectors[i][l];//.toMySparseVector();
    }
    
    public RealVector getLabelSequenceFeatureVector(int [] label){
        if (label.length != getN()+1)
            throw new DimensionMismatchException(label.length,getN()+1);
        
        RealVector sequenceFeatureVector = new MySparseVector(featureVectorDimension);
        for (int i=1;i<label.length;i++){
            int l=label[i];
            RealVector labelFeatureVector = getLabelFeatureVector(i, l);
            sequenceFeatureVector.add(labelFeatureVector);
        }
        return sequenceFeatureVector;
    }
    
    public RealVector lossVector(int [] act, int [] pred){
        if (act.length != getN()+1)
            throw new DimensionMismatchException(act.length,getN()+1);
        if (pred.length != getN()+1)
            throw new DimensionMismatchException(pred.length,getN()+1);
        
        int l = act.length;
        
        RealVector lossVector = new MySparseVector(featureVectorDimension);
        for (int i=1;i<act.length;i++){
            if (act[i]!=pred[i]){
                lossVector = lossVector.add(getLabelFeatureVector(i,act[i]));
                lossVector = lossVector.subtract(getLabelFeatureVector(i,pred[i]));
            }
        }
        return lossVector;
    }
    
    private void checkRange(int i, int l){
        if (i>getN() || i<=0)
            throw new OutOfRangeException(i,1, getN());
        if (l>getNumLabs() || l<0)
            throw new OutOfRangeException(l,0, getN());
    }

    /**
     * @return the n
     */
    public int getN() {
        return n;
    }

    /**
     * @return the numLabs
     */
    public int getNumLabs() {
        return numLabs;
    }
    
}
