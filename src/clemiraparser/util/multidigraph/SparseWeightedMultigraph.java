/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import edu.cmu.cs.ark.cle.graph.SparseWeightedGraph;
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
class SparseWeightedMultigraph<V, L> extends WeightedMultigraph<V, L> {
    
    private final Set<V> nodes;
    private final Map<V, Map<V, Map<L, Weighted<Edge<V, L>>>>> incomingEdges;

    private SparseWeightedMultigraph(Set<V> nodes, Map<V, Map<V, Map<L, Weighted<Edge<V, L>>>>> incomingEdges) {
        this.nodes = nodes;
        this.incomingEdges = incomingEdges;
    }

    public static <T, U> SparseWeightedMultigraph<T, U> from(Iterable<T> nodes, Iterable<Weighted<Edge<T, U>>> edges) {
        final Map<T, Map<T, Map<U, Weighted<Edge<T, U>>>>> incomingEdges = Maps.newHashMap();
        for (Weighted<Edge<T, U>> edge : edges) {
            if (!incomingEdges.containsKey(edge.val.destination)) {
                incomingEdges.put(edge.val.destination, Maps.<T, Map<U, Weighted<Edge<T, U>>>>newHashMap());
            }
            if (!incomingEdges.get(edge.val.destination).containsKey(edge.val.source)) {
                incomingEdges.get(edge.val.destination).put(edge.val.source, Maps.<U, Weighted<Edge<T, U>>>newHashMap());
            }
            incomingEdges.get(edge.val.destination).get(edge.val.source).put(edge.val.label, edge);
        }
        return new SparseWeightedMultigraph<>(ImmutableSet.copyOf(nodes), incomingEdges);
    }

    public static <T, U> SparseWeightedMultigraph<T, U> from(Iterable<Weighted<Edge<T, U>>> edges) {
        final Set<T> nodes = Sets.newHashSet();
        for (Weighted<Edge<T, U>> edge : edges) {
            nodes.add(edge.val.source);
            nodes.add(edge.val.destination);
        }
        return SparseWeightedMultigraph.from(nodes, edges);
    }

    @Override
    public Collection<V> getNodes() {
        return nodes;
    }

    @Override
    public double getWeightOf(V source, L label, V dest) {
        if (!incomingEdges.containsKey(dest)) {
            return Double.NEGATIVE_INFINITY;
        }
        final Map<V, Map<L, Weighted<Edge<V, L>>>> inEdges = incomingEdges.get(dest);
        if (!inEdges.containsKey(source)) {
            return Double.NEGATIVE_INFINITY;
        }
        final Map<L, Weighted<Edge<V, L>>> edges = inEdges.get(source);
        if (!edges.containsKey(label)) {
            return Double.NEGATIVE_INFINITY;
        }
        return edges.get(label).weight;
    }

    @Override
    public Collection<Weighted<Edge<V, L>>> getIncomingEdges(V destinationNode) {
        if (!incomingEdges.containsKey(destinationNode)) {
            return ImmutableSet.of();
        }
        Collection<Weighted<Edge<V, L>>> ret = Collections.emptySet();
        for (Map<L, Weighted<Edge<V, L>>> m : incomingEdges.get(destinationNode).values()) {
            ret.addAll(m.values());
        }
        return ret;
    }

    @Override
    public WeightedGraph<V> toMaxWeightGraph() {
        List<Weighted<edu.cmu.cs.ark.cle.graph.Edge<V>>> newEdges = new ArrayList<>();
        for (Map<V, Map<L, Weighted<Edge<V, L>>>> a : incomingEdges.values()) {
            for (Map<L, Weighted<Edge<V, L>>> b : a.values()) {
                double maxWeight = Double.NEGATIVE_INFINITY;
                Weighted<Edge<V, L>> maxEdge = null;
                for (Weighted<Edge<V, L>> e : b.values()) {
                    if (e.weight > maxWeight) {
                        maxWeight = e.weight;
                        maxEdge = e;
                    }
                }
                if (maxEdge != null) {
                    newEdges.add(new Weighted<edu.cmu.cs.ark.cle.graph.Edge<V>>(new edu.cmu.cs.ark.cle.graph.Edge<V>(maxEdge.val.source, maxEdge.val.destination), maxWeight));
                }
            }
        }
        return SparseWeightedGraph.from(nodes, newEdges);
    }

    @Override
    public WeightedMultigraph<V, L> toMaxWeightGraphWithLabels() {
        List<Weighted<Edge<V, L>>> newEdges = new ArrayList<>();
        for (Map<V, Map<L, Weighted<Edge<V, L>>>> a : incomingEdges.values()) {
            for (Map<L, Weighted<Edge<V, L>>> b : a.values()) {
                double maxWeight = Double.NEGATIVE_INFINITY;
                Weighted<Edge<V, L>> maxEdge = null;
                for (Weighted<Edge<V, L>> e : b.values()) {
                    if (e.weight > maxWeight) {
                        maxWeight = e.weight;
                        maxEdge = e;
                    }
                }
                if (maxEdge != null) {
                    newEdges.add(maxEdge);
                }
            }
        }
        return SparseWeightedMultigraph.from(nodes, newEdges);
    }

    @Override
    public Collection<Weighted<Edge<V, L>>> getConnectingEdges(V sourceNode, V destinationNode) {
        Map<V, Map<L, Weighted<Edge<V, L>>>> a = incomingEdges.get(destinationNode);
        Map<L, Weighted<Edge<V, L>>> b = a.get(sourceNode);
        return b.values();
    }
    
}
