/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import edu.cmu.cs.ark.cle.graph.WeightedGraph;
import edu.cmu.cs.ark.cle.util.Weighted;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author nizami
 */
public abstract class WeightedMultigraph<V, L> {
    
    public WeightedMultigraph() {
    }

    public abstract Collection<V> getNodes();

    public abstract double getWeightOf(V source, L label, V dest);

    public abstract Collection<Weighted<Edge<V, L>>> getIncomingEdges(V destinationNode);

    public abstract Collection<Weighted<Edge<V, L>>> getConnectingEdges(V sourceNode, V destinationNode);

    public WeightedMultigraph<V, L> filterEdges(Predicate<Edge<V, L>> predicate) {
        final List<Weighted<Edge<V, L>>> allEdges = Lists.newArrayList();
        for (V node : getNodes()) {
            final Collection<Weighted<Edge<V, L>>> incomingEdges = getIncomingEdges(node);
            for (Weighted<Edge<V, L>> edge : incomingEdges) {
                if (predicate.apply(edge.val)) {
                    allEdges.add(edge);
                }
            }
        }
        return SparseWeightedMultigraph.from(getNodes(), allEdges);
    }

    public abstract WeightedGraph<V> toMaxWeightGraph();

    public abstract WeightedMultigraph<V, L> toMaxWeightGraphWithLabels();
    
}
