/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.unlabeled;

import clemiraparser.util.MySparseVector;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class DependencyInstanceFeatureVectors {
    RealVector [][] edgeFeatureVectors;
    private final int n;
    final int featureVectorDimension;
    
    public DependencyInstanceFeatureVectors(int n, int featureVectorDimension){
        this.n = n;
        edgeFeatureVectors = new RealVector[n+1][n+1];
        this.featureVectorDimension = featureVectorDimension;
    }
    
    public void setEdgeFeatureVector(int i, int j, RealVector edgeFeatureVector){
        if (edgeFeatureVector.getDimension()!=featureVectorDimension)
            throw new DimensionMismatchException(edgeFeatureVector.getDimension(),featureVectorDimension);
        
        checkRange(i,j);
        edgeFeatureVectors[i][j] = edgeFeatureVector;
    }
    
    public RealVector getEdgeFeatureVector(int i, int j){
        checkRange(i,j);
        return edgeFeatureVectors[i][j];
    }
    
    public RealVector getTreeFeatureVector(int [] dep){
        if (dep.length != getN()+1)
            throw new DimensionMismatchException(dep.length,getN()+1);
        
        RealVector treeFeatureVector = new MySparseVector(featureVectorDimension);
        for (int j=1;j<dep.length;j++){
            int i=dep[j];
            RealVector edgeFeatureVector = getEdgeFeatureVector(i, j);
            treeFeatureVector.add(edgeFeatureVector);
        }
        return treeFeatureVector;
    }
    
    public RealVector lossVector(int [] act, int [] pred){
        if (act.length != getN()+1)
            throw new DimensionMismatchException(act.length,getN()+1);
        if (pred.length != getN()+1)
            throw new DimensionMismatchException(pred.length,getN()+1);
        
        int l = act.length;
        
        RealVector lossVector = new MySparseVector(featureVectorDimension);
        for (int j=1;j<act.length;j++){
            if (act[j]!=pred[j]){
                lossVector = lossVector.add(getEdgeFeatureVector(act[j],j));
                lossVector = lossVector.subtract(getEdgeFeatureVector(pred[j],j));
            }
        }
        return lossVector;
    }
    
    private void checkRange(int i, int j){
        if (i>getN() || i<0)
            throw new OutOfRangeException(i,0, getN());
        if (j>getN() || j<=0)
            throw new OutOfRangeException(j,1, getN());
    }

    /**
     * @return the n
     */
    public int getN() {
        return n;
    }
}
