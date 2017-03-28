package com.zackehh.jackson.stream.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.*;
import com.zackehh.jackson.stream.JiveCollectors;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

public class ObjectNodeCollectorTest {

    private final ObjectNodeCollector objectNodeCollector = JiveCollectors.toObjectNode();

    @Test
    public void testSupplier() {
        ObjectNode actual = objectNodeCollector.supplier().get();
        ObjectNode expected = JsonNodeFactory.instance.objectNode();

        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testAccumulator() {
        BiConsumer<ObjectNode, Map.Entry<String, JsonNode>> consumer = objectNodeCollector.accumulator();

        ObjectNode base = JsonNodeFactory.instance.objectNode();
        Map.Entry<String, JsonNode> value = new AbstractMap.SimpleEntry<>("key", TextNode.valueOf("test"));

        consumer.accept(base, value);

        Assert.assertEquals(base.size(), 1);
        Assert.assertEquals(base.path("key"), TextNode.valueOf("test"));
    }

    @Test
    public void testCombiner() {
        BinaryOperator<ObjectNode> combiner = objectNodeCollector.combiner();

        ObjectNode left = JsonNodeFactory.instance.objectNode()
                .put("key1", 1).put("key2", 2);
        ObjectNode right = JsonNodeFactory.instance.objectNode()
                .put("key3", 3).put("key4", 4);

        combiner.apply(left, right);

        Assert.assertEquals(left.size(), 4);
        Assert.assertEquals(left.path("key1"), IntNode.valueOf(1));
        Assert.assertEquals(left.path("key2"), IntNode.valueOf(2));
        Assert.assertEquals(left.path("key3"), IntNode.valueOf(3));
        Assert.assertEquals(left.path("key4"), IntNode.valueOf(4));
    }

    @Test
    public void testFinisher() {
        Function<ObjectNode, ObjectNode> finisher = objectNodeCollector.finisher();

        ObjectNode base = JsonNodeFactory.instance.objectNode()
                .put("key1", 1).put("key2", 2).put("key3", 3);

        ObjectNode kept = finisher.apply(base);

        Assert.assertEquals(kept, base);
    }

    @Test
    public void testCharacteristics() {
        Set<Collector.Characteristics> characteristics = objectNodeCollector.characteristics();

        Assert.assertEquals(characteristics.size(), 2);
        Assert.assertTrue(characteristics.contains(Collector.Characteristics.IDENTITY_FINISH));
        Assert.assertTrue(characteristics.contains(Collector.Characteristics.UNORDERED));
    }

}
