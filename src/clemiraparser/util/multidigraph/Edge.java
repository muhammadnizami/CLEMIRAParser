/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clemiraparser.util.multidigraph;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nizami
 */
public class Edge<V, L> {
    
    public final V source;
    public final L label;
    public final V destination;

    public Edge(V source, L label, V destination) {
        this.source = source;
        this.label = label;
        this.destination = destination;
    }

    public static class EdgeBuilder<V, L> {

        public final V source;
        public final L label;

        private EdgeBuilder(V source) {
            this.source = source;
            label = null;
        }

        private EdgeBuilder(V source, L label) {
            this.source = source;
            this.label = label;
        }

        public EdgeBuilder<V, L> label(L label) {
            return new EdgeBuilder<>(source, label);
        }

        public Edge<V, L> to(V destination) {
            return new Edge<>(source, label, destination);
        }
    }

    public static <T, U> EdgeBuilder<T, U> from(T source) {
        return new EdgeBuilder<>(source);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source, label, destination);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("source", source).add("label", label).add("destination", destination).toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        return this.source == other.source && this.label.equals(other.label) && this.destination == other.destination;
    }

    //// Edge Predicates
    public static <T, U> Predicate<Edge<T, U>> hasDestination(final T node) {
        return new Predicate<Edge<T, U>>() {
            @Override
            public boolean apply(Edge<T, U> input) {
                return input.destination.equals(node);
            }
        };
    }

    public static <T, U> Predicate<Edge<T, U>> competesWith(final Set<Edge<T, U>> required) {
        final ImmutableMap.Builder<T, T> requiredSourceByDestinationBuilder = ImmutableMap.builder();
        for (Edge<T, U> edge : required) {
            requiredSourceByDestinationBuilder.put(edge.destination, edge.source);
        }
        final Map<T, T> requiredSourceByDest = requiredSourceByDestinationBuilder.build();
        return new Predicate<Edge<T, U>>() {
            @Override
            public boolean apply(Edge<T, U> input) {
                return requiredSourceByDest.containsKey(input.destination) && !input.source.equals(requiredSourceByDest.get(input.destination));
            }
        };
    }

    public static <T, U> Predicate<Edge<T, U>> isAutoCycle() {
        return new Predicate<Edge<T, U>>() {
            @Override
            public boolean apply(Edge<T, U> input) {
                return input.source.equals(input.destination);
            }
        };
    }

    public static <T, U> Predicate<Edge<T, U>> isIn(final Set<Edge<T, U>> banned) {
        return new Predicate<Edge<T, U>>() {
            @Override
            public boolean apply(Edge<T, U> input) {
                return banned.contains(input);
            }
        };
    }
    
}
