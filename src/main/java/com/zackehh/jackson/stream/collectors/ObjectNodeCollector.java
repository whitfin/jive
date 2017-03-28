package com.zackehh.jackson.stream.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector to collect a Stream of JsonNode instance into a new ObjectNode instance.
 *
 * Even though this class could operate on arbitrary types by coercing values into
 * JsonNode instance, it's better to enforce explicitness in pipelines as it raises
 * error handling into the userland layer.
 */
public class ObjectNodeCollector implements Collector<Map.Entry<String, JsonNode>, ObjectNode, ObjectNode> {

    /**
     * Simply returns a Supplier which creates a new empty ObjectNode
     * instance in order to collect JsonNode values into.
     */
    @Override
    public Supplier<ObjectNode> supplier() {
        return JsonNodeFactory.instance::objectNode;
    }

    /**
     * Returns a BiConsumer which adds a pre-created JsonNode into the ObjectNode
     * instance provided by the Supplier.
     */
    @Override
    public BiConsumer<ObjectNode, Map.Entry<String, JsonNode>> accumulator() {
        return (acc, entry) -> acc.set(entry.getKey(), entry.getValue());
    }

    /**
     * Returns an operator which combines two ObjectNode instances by merging all
     * entries from the right instance into the left instance.
     *
     * This will overwrite any entries in the left with the same key as an entry
     * in the right instance.
     */
    @Override
    public BinaryOperator<ObjectNode> combiner() {
        return (l, r) -> {
            Iterator<Map.Entry<String, JsonNode>> it = r.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                l.set(entry.getKey(), entry.getValue());
            }
            return l;
        };
    }

    /**
     * Returns a Function to identify the input.
     *
     * This is moot and not called due to the provided Characteristics.
     */
    @Override
    public Function<ObjectNode, ObjectNode> finisher() {
        return Function.identity();
    }

    /**
     * Returns a Set of Characteristics to apply to this Collector. The only ones
     * provided are that there's an identity finisher and that there is not specific
     * order of the incoming pairs.
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }

}

