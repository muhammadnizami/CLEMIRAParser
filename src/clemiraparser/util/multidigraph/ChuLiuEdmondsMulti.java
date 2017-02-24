/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import edu.cmu.cs.ark.cle.graph.WeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author nizami
 */
public class ChuLiuEdmondsMulti {
    
    /**
     * Find an optimal arborescence of the given graph `graph`, rooted in the given node `root`.
     */
    public static <V, L> Weighted<Arborescence<V, L>> getMaxArborescence(WeightedMultigraph<V, L> graph, V root) {
        // remove all edges incoming to `root`. resulting arborescence is then forced to be rooted at `root`.
        return getMaxArborescence(graph.filterEdges(Predicates.not(Edge.hasDestination(root))));
    }

    static <V, L> Weighted<Arborescence<V, L>> getMaxArborescence(WeightedMultigraph<V, L> graph, Set<Edge<V, L>> required, Set<Edge<V, L>> banned) {
        return getMaxArborescence(graph.filterEdges(Predicates.and(Predicates.not(Edge.competesWith(required)), Predicates.not(Edge.isIn(banned)))));
    }

    /**
     * Find an optimal arborescence of the given graph.
     */
    public static <V, L> Weighted<Arborescence<V, L>> getMaxArborescence(WeightedMultigraph<V, L> graph) {
        WeightedMultigraph<V, L> maxMultigraph = graph.toMaxWeightGraphWithLabels();
        WeightedGraph<V> maxGraph = maxMultigraph.toMaxWeightGraph();
        Weighted<edu.cmu.cs.ark.cle.Arborescence<V>> maxArborescence = ChuLiuEdmonds.getMaxArborescence(maxGraph);
        
        return labeledArborescence(maxArborescence, maxMultigraph);
    }
    
    public static <V, L> Weighted<Arborescence<V,L>> labeledArborescence(Weighted<edu.cmu.cs.ark.cle.Arborescence<V>> arb, WeightedMultigraph multigraph){
        ImmutableMap parents = arb.val.parents;
        ImmutableMap.Builder<V, L> labelMapBuilder = ImmutableMap.builder();
        for (Object o : parents.entrySet()) {
            ImmutableMap.Entry<V, V> e = (ImmutableMap.Entry<V, V>) o;
            Collection<Weighted<Edge<V, L>>> edgeColl = multigraph.getConnectingEdges(e.getValue(), e.getKey());
            assert (edgeColl.size() == 1);
            L label = edgeColl.iterator().next().val.label;
            labelMapBuilder.put(e.getKey(), label);
        }
        ImmutableMap labels = labelMapBuilder.build();
        Arborescence<V, L> arbMulti = Arborescence.of(parents, labels);
        return new Weighted<>(arbMulti, arb.weight);
    }
    
}
