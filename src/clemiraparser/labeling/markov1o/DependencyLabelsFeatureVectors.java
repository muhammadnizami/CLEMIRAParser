/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.labeling.markov1o;

import clemiraparser.util.MySparseVector;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author nizami
 */
public class DependencyLabelsFeatureVectors{
    private RealVector[] initialFeatureVectors; //scores of left-most nodes
    private List<RealVector[][]> transitionFeatureVectors;
    private int numNodesPerLayer;
    private int numLayers;
    private int featureVectorDimensions;
    
    public DependencyLabelsFeatureVectors(int numNodesPerLayer, int featureVectorDimensions){
        if (numNodesPerLayer<=0)
            throw new IllegalArgumentException("numNodesPerLayer should be >0. value: " + numNodesPerLayer);
        this.numNodesPerLayer = numNodesPerLayer;
        initialFeatureVectors = new RealVector[numNodesPerLayer];
        numLayers=1;
        transitionFeatureVectors = new ArrayList<>();
    }
    
    /**
     * sets the initial scores, that is, the scores of the leftmost nodes
     * @param initialFeatureVectors
     */
    public void setInitialFeatureVectors(RealVector [] initialFeatureVectors){
        if (initialFeatureVectors.length != getNumNodesPerLayer())
            throw new DimensionMismatchException(initialFeatureVectors.length, getNumNodesPerLayer());
        
        this.initialFeatureVectors = initialFeatureVectors;
    }
    
    /**
     * grows the problem by one layer
     * @param transitionFeatureVectors the feature vectors of transitions. transitionFeatureVectors[i][j] means feature vector of transition from node i to new layer's node j. the transitionFeatureVectors must be of width and length numNodesPerLayer
     */
    public void grow(RealVector [][] transitionFeatureVectors){
        checkTransitionScoresDimensions(transitionFeatureVectors);
        numLayers++;
        this.getTransitionFeatureVectors().add(transitionFeatureVectors);
    }
    
    
    /**
     * grows the problem by transitionScores.size() layers
     * @param transitionFeatureVectors the feature vectors of transitions, in order of the layers from left to right. transitionFeatureVectors[i][j] means feature vector of transition from node i to new layer's node j. the transitionFeatureVectors must be of width and length numNodesPerLayer
     */
    public void grow(List<RealVector [][]> transitionFeatureVectors){
        for (RealVector[][] d : transitionFeatureVectors)
            checkTransitionScoresDimensions(d);
        this.getTransitionFeatureVectors().addAll(transitionFeatureVectors);
    }
    
    protected void checkTransitionScoresDimensions(RealVector[][] transitionScores){
        if (transitionScores.length!=getNumNodesPerLayer())
            throw new DimensionMismatchException(transitionScores.length, getNumNodesPerLayer());
        for (RealVector [] i : transitionScores){
            if (i.length!=getNumNodesPerLayer())
            throw new DimensionMismatchException(i.length, getNumNodesPerLayer());
        }
        
    }

    /**
     * @return the initialFeatureVectors
     */
    public RealVector[] getInitialFeatureVectors() {
        return initialFeatureVectors;
    }

    /**
     * @return the transitionFeatureVectors
     */
    public List<RealVector[][]> getTransitionFeatureVectors() {
        return transitionFeatureVectors;
    }

    /**
     * @return the numNodesPerLayer
     */
    public int getNumNodesPerLayer() {
        return numNodesPerLayer;
    }

    /**
     * @return the numLayers
     */
    public int getNumLayers() {
        return numLayers;
    }
    
    public RealVector getFeatureVector(int i, int leftLabel, int curLabel){
        if (i<0 || i>=getNumLayers())
            throw new OutOfRangeException(i,0,getNumLayers()-1);
        if (leftLabel<-1 || leftLabel>=getNumNodesPerLayer())
            throw new OutOfRangeException(leftLabel,-1,getNumNodesPerLayer()-1);
        if (curLabel<0 || curLabel>=getNumNodesPerLayer())
            throw new OutOfRangeException(curLabel,0,getNumNodesPerLayer()-1);
        if (i==0)
            return initialFeatureVectors[curLabel];
        else
            return transitionFeatureVectors.get(i-1)[leftLabel][curLabel];
    }

    public RealVector lossVector(int[] act, int[] pred) {
        if (act.length != getNumLayers())
            throw new DimensionMismatchException(act.length,getNumLayers());
        if (pred.length != getNumLayers())
            throw new DimensionMismatchException(pred.length,getNumLayers());
        
        RealVector retval = new MySparseVector(featureVectorDimensions);
        for (int i=0;i<getNumLayers();i++){
            if (act[i]!=pred[i])
                retval = retval.add(getFeatureVector(i,i>0?act[i-1]:-1,act[i]));
        }
        return retval;
    }
}
