package clemiraparser.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nizami
 */
public class ViterbiProblem {
    private double[] initialScores; //scores of left-most nodes
    private List<double[][]> transitionScores;
    private int numNodesPerLayer;
    private int numLayers;
    
    public ViterbiProblem(int numNodesPerLayer){
        if (numNodesPerLayer<=0)
            throw new IllegalArgumentException("numNodesPerLayer should be >0. value: " + numNodesPerLayer);
        this.numNodesPerLayer = numNodesPerLayer;
        initialScores = new double[numNodesPerLayer];
        numLayers=1;
        transitionScores = new ArrayList<>();
    }
    
    /**
     * sets the initial scores, that is, the scores of the leftmost nodes
     * @param initialScores 
     */
    public void setInitialScores(double [] initialScores){
        if (initialScores.length != getNumNodesPerLayer())
            throw new DimensionMismatchException(initialScores.length, getNumNodesPerLayer());
        
        this.initialScores = initialScores;
    }
    
    /**
     * grows the problem by one layer
     * @param transitionScores the scores of transitions. transitionScores[i][j] means score of transition from node i to new layer's node j. the transitionScores must be of width and length numNodesPerLayer
     */
    public void grow(double [][] transitionScores){
        checkTransitionScoresDimensions(transitionScores);
        numLayers++;
        this.getTransitionScores().add(transitionScores);
    }
    
    
    /**
     * grows the problem by transitionScores.size() layers
     * @param transitionScores the scores of transitions, in order of the layers from left to right. transitionScores[i][j] means score of transition from node i to new layer's node j. the transitionScores must be of width and length numNodesPerLayer
     */
    public void grow(List<double [][]> transitionScores){
        for (double[][] d : transitionScores)
            checkTransitionScoresDimensions(d);
        this.getTransitionScores().addAll(transitionScores);
    }
    
    protected void checkTransitionScoresDimensions(double [][] transitionScores){
        if (transitionScores.length!=getNumNodesPerLayer())
            throw new DimensionMismatchException(transitionScores.length, getNumNodesPerLayer());
        for (double [] i : transitionScores){
            if (i.length!=getNumNodesPerLayer())
            throw new DimensionMismatchException(i.length, getNumNodesPerLayer());
        }
        
    }
    
    /**
     * the viterbi algorithm
     * @return array of integers representing the nodes
     */
    public int[] bestPath(){
        double [][] partialSumScores = new double[getNumLayers()][getNumNodesPerLayer()];
        partialSumScores[0] = getInitialScores().clone();
        
        int [][] prevs = new int[getNumLayers()][getNumNodesPerLayer()];
        
        for (int i=1;i<getNumLayers();i++){
            prevs[i]=new int[getNumNodesPerLayer()];
            for (int j=0;j<getNumNodesPerLayer();j++){
                int prev=-1;
                double maxSum = Double.NEGATIVE_INFINITY;
                
                //searching for max
                for (int k=0;k<getNumNodesPerLayer();k++){
                    double thisSum=partialSumScores[i-1][k]+getTransitionScores().get(i-1)[k][j];
                    if (thisSum>maxSum){
                        prev=k;
                        maxSum=thisSum;
                    }
                }
                
                prevs[i][j]=prev;
                partialSumScores[i][j]=maxSum;
            }
        }
        
        //getting highes end score
        int endNode=-1;
        double maxSum = Double.NEGATIVE_INFINITY;
        for (int i=0;i<getNumNodesPerLayer();i++){
            if (partialSumScores[getNumLayers()-1][i]>maxSum){
                maxSum=partialSumScores[getNumLayers()-1][i];
                endNode=i;
            }
        }
        
        //tracing back the path
        int [] solution = new int[getNumLayers()];
        solution[getNumLayers()-1]=endNode;
        for (int i=getNumLayers()-1;i>0;i--){
            solution[i-1]=prevs[i][solution[i]];
        }
        return solution;
    }
    
    public List<int[]> lBestPathsNaive(int l) throws Exception{
        
        
        class SubNode implements Comparable{
            
            public SubNode(int label, double score, SubNode prev){
                this.label = label;
                this.score = score;
                this.prev = prev;
            }
            
            int label;
            double score;
            SubNode prev;
            
            public int compareTo(SubNode p){
                int a = Double.compare(score, p.score);
                if (a!=0)
                    return a;
                else
                    return Integer.compare(label,p.label);
            }

            @Override
            public int compareTo(Object t) {
                SubNode p = (SubNode) t;
                return compareTo(p);
            }
            
            @Override
            public String toString(){
                return "("+label+","+score+","+prev+")";
            }
        }
        
        class LBestSubNodes{
            List<SubNode> subNodes;
            int l;
            public LBestSubNodes(int l){
                subNodes = new ArrayList<>(l);
                this.l = l;
            }
            public void add(SubNode p){
                subNodes.add(p);
                Collections.sort(subNodes);
                if (subNodes.size()>l){
                    subNodes.remove(0);
                }
            }
            public int size(){
                return subNodes.size();
            }
        }
        
        LBestSubNodes[][] lBestSubNodes= new LBestSubNodes[getNumLayers()][getNumNodesPerLayer()];
        
        for (int i=0;i<getNumNodesPerLayer();i++){
            lBestSubNodes[0][i] = new LBestSubNodes(l);
            lBestSubNodes[0][i].add(new SubNode(i,getInitialScores()[i],null));
        }
        
        for (int i=1;i<getNumLayers();i++){
            for (int j=0;j<getNumNodesPerLayer();j++){
                lBestSubNodes[i][j] = new LBestSubNodes(l);
                
                //searching for max
                for (int k=0;k<getNumNodesPerLayer();k++){
                    for (SubNode prevSubNode : lBestSubNodes[i-1][k].subNodes){
                        if (prevSubNode.label!=k)
                            throw new Exception("Algorithm error: " + k + " != " + prevSubNode.label);
                        double newScore = prevSubNode.score + getTransitionScores().get(i-1)[k][j];
                        lBestSubNodes[i][j].add(new SubNode(j,newScore,prevSubNode));
                    }
                }
            }
        }
        
        //getting l highest end score
        LBestSubNodes endLBest = new LBestSubNodes(l);
        for (int k=0;k<getNumNodesPerLayer();k++){
            for (SubNode subNode : lBestSubNodes[getNumLayers()-1][k].subNodes){
                endLBest.add(subNode);
            }
        }
        
        //tracing back the path
        List<int[]> solutions = new ArrayList<>(l);
        for (SubNode subNode: endLBest.subNodes){
            int[] solution = new int[getNumLayers()];
            for (int i=getNumLayers()-1;i>=0;i--){
                solution[i] = subNode.label;
                subNode = subNode.prev;
            }
            solutions.add(solution);
        }
        return solutions;
    }
    
    public static double [] negate(double [] m){
        double [] r = new double[m.length];
        for (int i=0;i<m.length;i++){
            r[i]=-m[i];
        }
        return r;
    }
    
    public static double [][] negate(double[][] m){
        double [][] r = new double[m.length][];
        for (int i=0;i<m.length;i++){
            r[i]=negate(m[i]);
        }
        
        return r;        
    }
    
    public ViterbiProblem negate(){
        ViterbiProblem viterbiProblem = new ViterbiProblem(getNumNodesPerLayer());
        viterbiProblem.setInitialScores(negate(getInitialScores()));
        
        for (int i=0;i<getNumLayers()-1;i++){
            viterbiProblem.grow(negate(getTransitionScores().get(i)));
        }
        
        return viterbiProblem;
    }

    /**
     * @return the initialScores
     */
    public double[] getInitialScores() {
        return initialScores;
    }

    /**
     * @return the transitionScores
     */
    public List<double[][]> getTransitionScores() {
        return transitionScores;
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
    
    public double score(int [] path){
        if (path.length!=getNumLayers())
            throw new DimensionMismatchException(path.length,getNumLayers());
        
        double score = getInitialScores()[path[0]];
        for (int i=0;i<path.length-1;i++){
            score+=getTransitionScores().get(i)[path[i]][path[i+1]];
        }
        return score;
    }
}
