/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nizami
 */
public class Arborescence<V, L> {
    
    /**
     * In an arborescence, each node (other than the root) has exactly one parent. This is the map
     * from each node to its parent.
     */
    public final ImmutableMap<V, V> parents;
    /**
     * In an arborescence, each node (other than the root) has exactly one edge label. This is the map
     * from each node to its edge label.
     */
    public final ImmutableMap<V, L> labels;

    private Arborescence(ImmutableMap<V, V> parents, ImmutableMap<V, L> labels) {
        this.parents = parents;
        this.labels = labels;
    }

    public static <T, U> Arborescence<T, U> of(ImmutableMap<T, T> parents, ImmutableMap<T, U> labels) {
        return new Arborescence<T, U>(parents, labels);
    }

    public static <T, U> Arborescence<T, U> empty() {
        return Arborescence.of(ImmutableMap.<T, T>of(), ImmutableMap.<T, U>of());
    }

    public boolean contains(Edge<V, L> e) {
        final V dest = e.destination;
        final L lab = e.label;
        return parents.containsKey(dest) && parents.get(dest).equals(e.source) && labels.containsKey(dest) && labels.get(dest).equals(e.label);
    }
    
    public Arborescence<V,L> replaceLabel(V child, L newLabel){
        ImmutableMap<V,V> newparents = this.parents;
        ImmutableMap.Builder<V, L> builder = ImmutableMap.builder();
        for (ImmutableMap.Entry<V,L> e : this.labels.entrySet()){
            if (e.getKey()!=child)
                builder.put(e);
        }
        builder.put(child,newLabel);
        ImmutableMap<V,L> newlabels = builder.build();
        return Arborescence.of(newparents, newlabels);
    }

    @Override
    public String toString() {
        final List<String> lines = Lists.newArrayList();
        for (Map.Entry<V, V> entry : parents.entrySet()) {
            lines.add(entry.getValue() + " --" + labels.get(entry.getKey()) + "-> " + entry.getKey());
        }
        return Objects.toStringHelper(this).addValue(Joiner.on(", ").join(lines)).toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final Arborescence that = (Arborescence) other;
        Set<Map.Entry<V, V>> myEntries = parents.entrySet();
        Set thatEntries = that.parents.entrySet();
        Set<Map.Entry<V, L>> myLabels = labels.entrySet();
        Set thatLabels = that.labels.entrySet();
        return myEntries.size() == thatEntries.size() && myEntries.containsAll(thatEntries) && myLabels.size() == thatLabels.size() && myLabels.containsAll(thatLabels);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(parents);
    }
    
}
