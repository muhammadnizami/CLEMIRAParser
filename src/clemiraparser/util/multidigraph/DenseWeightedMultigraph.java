/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Preconditions;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import edu.cmu.cs.ark.cle.graph.DenseWeightedGraph;
import edu.cmu.cs.ark.cle.graph.WeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nizami
 */
public class DenseWeightedMultigraph<V, L> extends WeightedMultigraph<V, L> {
    
    private final ArrayList<V> nodes;
    private final ArrayList<L> labels;
    private final Map<V, Integer> indexOfNodes;
    private final Map<L, Integer> indexOfLabels;
    private final double[][][] weights;

    private DenseWeightedMultigraph(ArrayList<V> nodes, ArrayList<L> labels, Map<V, Integer> indexOfNodes, Map<L, Integer> indexOfLabels, double[][][] weights) {
        this.nodes = nodes;
        this.labels = labels;
        this.indexOfNodes = indexOfNodes;
        this.indexOfLabels = indexOfLabels;
        this.weights = weights;
    }

    public static <V, L> DenseWeightedMultigraph<V, L> from(Iterable<V> nodes, Iterable<L> labels, double[][][] weights) {
        final ArrayList<V> nodeList = Lists.newArrayList(nodes);
        final ArrayList<L> labelList = Lists.newArrayList(labels);
        Preconditions.checkArgument(nodeList.size() == weights.length);
        final Map<V, Integer> indexOfNodes = Maps.newHashMap();
        for (int i = 0; i < nodeList.size(); i++) {
            indexOfNodes.put(nodeList.get(i), i);
        }
        final Map<L, Integer> indexOfLabels = Maps.newHashMap();
        for (int i = 0; i < labelList.size(); i++) {
            indexOfLabels.put(labelList.get(i), i);
        }
        return new DenseWeightedMultigraph<V, L>(nodeList, labelList, indexOfNodes, indexOfLabels, weights);
    }

    public static DenseWeightedMultigraph<Integer, Integer> from(double[][][] weights) {
        final Set<Integer> nodes = ContiguousSet.create(Range.closedOpen(0, weights.length), DiscreteDomain.integers());
        final Set<Integer> labels = ContiguousSet.create(Range.closedOpen(0, weights[0][0].length), DiscreteDomain.integers());
        return DenseWeightedMultigraph.from(nodes, labels, weights);
    }

    @Override
    public Collection<V> getNodes() {
        return nodes;
    }

    @Override
    public double getWeightOf(V source, L label, V dest) {
        if (!indexOfNodes.containsKey(source) || !indexOfNodes.containsKey(dest) || !indexOfLabels.containsKey(label)) {
            return Double.NEGATIVE_INFINITY;
        }
        return weights[indexOfNodes.get(source)][indexOfNodes.get(dest)][indexOfLabels.get(label)];
    }

    @Override
    public Collection<Weighted<Edge<V, L>>> getIncomingEdges(V destinationNode) {
        if (!indexOfNodes.containsKey(destinationNode)) {
            return Collections.emptySet();
        }
        final int dest = indexOfNodes.get(destinationNode);
        List<Weighted<Edge<V, L>>> results = Lists.newArrayList();
        for (int src = 0; src < nodes.size(); src++) {
            for (int lab = 0; lab < labels.size(); lab++) {
                V source = nodes.get(src);
                L label = labels.get(lab);
                Edge<V, L> e = Edge.<V, L>from(source).label(label).to(destinationNode);
                results.add(Weighted.weighted(e, weights[src][dest][lab]));
            }
        }
        return results;
    }

    @Override
    public WeightedGraph<V> toMaxWeightGraph() {
        int numNodes = nodes.size();
        double[][] maxweights = new double[numNodes][numNodes];
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                double max = Double.NEGATIVE_INFINITY;
                for (int k = 0; k < weights[i][j].length; k++) {
                    if (weights[i][j][k] > max) {
                        max = weights[i][j][k];
                    }
                }
                maxweights[i][j] = max;
            }
        }
        return DenseWeightedGraph.from(nodes, maxweights);
    }

    @Override
    public WeightedMultigraph<V, L> toMaxWeightGraphWithLabels() {
        int numNodes = nodes.size();
        List<Weighted<Edge<V, L>>> newEdges = new ArrayList<>(numNodes * numNodes);
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                double max = Double.NEGATIVE_INFINITY;
                int maxk = -1;
                for (int k = 0; k < weights[i][j].length; k++) {
                    if (weights[i][j][k] > max) {
                        max = weights[i][j][k];
                        maxk = k;
                    }
                }
                if (maxk>=0){
                    L label = labels.get(maxk);
                    V source = nodes.get(i);
                    V destination = nodes.get(j);
                    Edge<V, L> newEdge = new Edge(source, label, destination);
                    newEdges.add(new Weighted<>(newEdge, max));
                }
            }
        }
        return SparseWeightedMultigraph.from(nodes, newEdges);
    }

    @Override
    public Collection<Weighted<Edge<V, L>>> getConnectingEdges(V sourceNode, V destinationNode) {
        ImmutableSet.Builder<Weighted<Edge<V, L>>> builder = new ImmutableSet.Builder<>();
        for (L l : labels) {
            builder.add(new Weighted<>(new Edge(sourceNode, l, destinationNode), weights[indexOfNodes.get(sourceNode)][indexOfNodes.get(destinationNode)][indexOfLabels.get(l)]));
        }
        return builder.build();
    }
    
}
