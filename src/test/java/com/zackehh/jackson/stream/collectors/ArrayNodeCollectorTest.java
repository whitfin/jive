package com.zackehh.jackson.stream.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import com.zackehh.jackson.stream.JiveCollectors;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public class ArrayNodeCollectorTest {

    private final ArrayNodeCollector arrayNodeCollector = JiveCollectors.toArrayNode();

    @Test
    public void testSupplier() {
        ArrayNode actual = arrayNodeCollector.supplier().get();
        ArrayNode expected = JsonNodeFactory.instance.arrayNode();

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testAccumulator() {
        BiConsumer<ArrayNode, JsonNode> consumer = arrayNodeCollector.accumulator();

        ArrayNode base = JsonNodeFactory.instance.arrayNode();
        JsonNode value = TextNode.valueOf("test");

        consumer.accept(base, value);

        Assert.assertEquals(base.size(), 1);
        Assert.assertEquals(base.path(0), value);
    }

    @Test
    public void testCombiner() {
        BinaryOperator<ArrayNode> combiner = arrayNodeCollector.combiner();

        ArrayNode left = JsonNodeFactory.instance.arrayNode()
                .add(1).add(2);
        ArrayNode right = JsonNodeFactory.instance.arrayNode()
                .add(3).add(4);

        combiner.apply(left, right);

        Assert.assertEquals(left.size(), 4);
        Assert.assertEquals(left.path(0), IntNode.valueOf(1));
        Assert.assertEquals(left.path(1), IntNode.valueOf(2));
        Assert.assertEquals(left.path(2), IntNode.valueOf(3));
        Assert.assertEquals(left.path(3), IntNode.valueOf(4));
    }

    @Test
    public void testFinisher() {
        Function<ArrayNode, ArrayNode> finisher = arrayNodeCollector.finisher();

        ArrayNode base = JsonNodeFactory.instance.arrayNode()
                .add(1).add(2).add(3);

        ArrayNode kept = finisher.apply(base);

        Assert.assertEquals(kept, base);
    }

    @Test
    public void testCharacteristics() {
        Set<Collector.Characteristics> characteristics = arrayNodeCollector.characteristics();

        Assert.assertEquals(characteristics.size(), 1);
        Assert.assertTrue(characteristics.contains(Collector.Characteristics.IDENTITY_FINISH));
    }

}
