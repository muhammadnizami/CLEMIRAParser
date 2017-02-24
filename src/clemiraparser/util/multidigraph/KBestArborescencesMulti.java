/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import edu.cmu.cs.ark.cle.graph.WeightedGraph;
import edu.cmu.cs.ark.cle.util.Pair;
import edu.cmu.cs.ark.cle.util.Weighted;
import edu.cmu.cs.ark.cle.KBestArborescences;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import edu.cmu.cs.ark.cle.ChuLiuEdmonds;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


/**
 *
 * @author nizami
 */
public class KBestArborescencesMulti extends KBestArborescences{
    
    /** Find the k-best arborescences of `graph`, rooted in the given node `root`.
     * Naive approach
     */
    public static <V,L> List<Weighted<Arborescence<V,L>>> getKBestArborescences(WeightedMultigraph<V,L> graph, V root, int k) {
        WeightedMultigraph<V,L> maxWeightGraphWithLabels = graph.toMaxWeightGraphWithLabels();
        WeightedGraph<V> maxWeightGraph = maxWeightGraphWithLabels.toMaxWeightGraph();
        List<Weighted<edu.cmu.cs.ark.cle.Arborescence<V>>> kBestUnlabeled = KBestArborescences.getKBestArborescences(maxWeightGraph, root, k);
        List<Weighted<Arborescence<V,L>>> ret = new ArrayList<>(k);
            
        int i=0;
        final PriorityQueue<Weighted<SearchNode<V,L>>> queue = Queues.newPriorityQueue();
        
        while (ret.size()<k && i<kBestUnlabeled.size()){
            Weighted<edu.cmu.cs.ark.cle.Arborescence<V>> bestArb = kBestUnlabeled.get(i);
            Weighted<Arborescence<V,L>> bestArbLab = ChuLiuEdmondsMulti.labeledArborescence(bestArb, maxWeightGraphWithLabels);
            SearchNode<V,L> searchNode = SearchNode.of(bestArbLab.val);
            queue.add(new Weighted<>(searchNode, bestArbLab.weight));
            
            double nextWeight=i<kBestUnlabeled.size()-1?kBestUnlabeled.get(i+1).weight:Double.NEGATIVE_INFINITY;
            while (ret.size() < k && !queue.isEmpty() && queue.peek().weight >= nextWeight){
                Weighted<SearchNode<V,L>> weightedCurSearchNode = queue.poll();
                SearchNode<V,L> curSearchNode = weightedCurSearchNode.val;
                Arborescence<V,L> curArb = curSearchNode.arb;
                ret.add(new Weighted<>(curSearchNode.arb,weightedCurSearchNode.weight));
                
                for (V child : curArb.labels.keySet()){
                    V parent = curArb.parents.get(child);
                    L oldLabel = curArb.labels.get(child);
                    for (Weighted<Edge<V,L>> connectingEdge : graph.getConnectingEdges(parent, child)){
                        L newLabel = connectingEdge.val.label;
                        if (!newLabel.equals(oldLabel))
                        if (!curSearchNode.replacementHistory.get(child).contains(newLabel)){
                            SearchNode<V,L> newSearchNode = curSearchNode.replaceLabel(child, newLabel);
                            double weight = weightedCurSearchNode.weight
                                    - graph.getWeightOf(parent, oldLabel, child)
                                    + connectingEdge.weight;
                            queue.add(new Weighted<>(newSearchNode,weight));
                        }
                    }
                }
                
            }
            i++;
        }
        return ret;
    }
    
    private static class SearchNode<V,L> {
        Arborescence<V,L> arb;
        Map<V,Set<L>> replacementHistory;
        
        public SearchNode(Arborescence<V,L> arb, Map<V, Set<L>> replacementHistory){
            this.arb = arb;
            this.replacementHistory = replacementHistory;
        }
        
        public static <V,L> SearchNode<V,L> of (Arborescence<V,L> arb){
            ImmutableMap.Builder<V,Set<L>> replacementHistoryBuilder = ImmutableMap.builder();
            for (V v : arb.labels.keySet()){
                replacementHistoryBuilder.put(v, ImmutableSet.of());
            }
            return new SearchNode(arb, replacementHistoryBuilder.build());
            
        }
        
        public SearchNode<V,L> replaceLabel(V child, L newLabel){
            Arborescence newArb = arb.replaceLabel(child, newLabel);
            ImmutableMap.Builder<V,Set<L>> replacementHistoryBuilder = ImmutableMap.builder();
            for (V v : replacementHistory.keySet()){
                if (!v.equals(child)){
                    replacementHistoryBuilder.put(v, replacementHistory.get(v));
                }else{
                    ImmutableSet.Builder<L> setBuilder = ImmutableSet.builder();
                    setBuilder.addAll(replacementHistory.get(v));
                    setBuilder.add(arb.labels.get(v));
                    replacementHistoryBuilder.put(v,setBuilder.build());
                }
            }
            return new SearchNode(newArb,replacementHistoryBuilder.build());
        }
    }
    
}
