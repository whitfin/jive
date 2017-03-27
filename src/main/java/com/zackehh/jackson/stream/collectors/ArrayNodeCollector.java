package com.zackehh.jackson.stream.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Collector to collect a Stream of JsonNode instance into a new ArrayNode instance.
 *
 * Even though this class could operate on arbitrary types by coercing values into
 * JsonNode instance, it's better to enforce explicitness in pipelines as it raises
 * error handling into the userland layer.
 */
public class ArrayNodeCollector implements Collector<JsonNode, ArrayNode, ArrayNode> {

    /**
     * Simply returns a Supplier which creates a new empty ArrayNode instance
     * in order to collect JsonNode values into.
     *
     * @inheritDoc
     */
    @Override
    public Supplier<ArrayNode> supplier() {
        return JsonNodeFactory.instance::arrayNode;
    }

    /**
     * Returns a BiConsumer which adds a pre-created JsonNode into the ArrayNode
     * instance provided by the Supplier.
     *
     * @inheritDoc
     */
    @Override
    public BiConsumer<ArrayNode, JsonNode> accumulator() {
        return ArrayNode::add;
    }

    /**
     * Returns an operator which combines two ArrayNode instances by adding all
     * JsonNode values from the right instance into the left instance.
     *
     * @inheritDoc
     */
    @Override
    public BinaryOperator<ArrayNode> combiner() {
        return ArrayNode::addAll;
    }

    /**
     * Returns a Function to identify the input.
     *
     * This is moot and not called due to the provided Characteristics.
     *
     * @inheritDoc
     */
    @Override
    public Function<ArrayNode, ArrayNode> finisher() {
        return Function.identity();
    }

    /**
     * Returns a Set of Characteristics to apply to this Collector. The only one
     * provided is that there's an identity finisher, in order to remove the call.
     *
     * @inheritDoc
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }

}

