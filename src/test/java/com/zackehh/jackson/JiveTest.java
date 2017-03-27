package com.zackehh.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.zackehh.jackson.stream.JiveCollectors;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JiveTest {

    @Test
    public void testExecute() {
        ObjectMapper mapper = new ObjectMapper();

        Optional<JsonNode> res1 = Jive.execute(mapper, mapper1 -> mapper1.readTree("{}"));
        Optional<JsonNode> res2 = Jive.execute(mapper, mapper1 -> null);
        Optional<JsonNode> res3 = Jive.execute(mapper, mapper1 -> {
            throw new IOException();
        });

        Assert.assertTrue(res1.isPresent());
        Assert.assertFalse(res2.isPresent());
        Assert.assertFalse(res3.isPresent());
        Assert.assertEquals(res1.orElse(null), mapper.createObjectNode());
    }

    @Test
    public void testNewArrayNode() {
        ArrayNode arr1 = Jive.newArrayNode();
        ArrayNode arr2 = Jive.newArrayNode(
            TextNode.valueOf("1"),
            TextNode.valueOf("2"),
            TextNode.valueOf("3")
        );

        Assert.assertEquals(arr1.size(), 0);
        Assert.assertEquals(arr2.size(), 3);

        Assert.assertEquals(arr2.get(0), TextNode.valueOf("1"));
        Assert.assertEquals(arr2.get(1), TextNode.valueOf("2"));
        Assert.assertEquals(arr2.get(2), TextNode.valueOf("3"));
    }

    @Test
    public void testNewIterator() {
        Iterable<JsonNode> arr1 = Jive.newIterable(null);
        Iterable<JsonNode> arr2 = Jive.newIterable(TextNode.valueOf("test"));
        Iterable<JsonNode> arr3 = Jive.newIterable(JsonNodeFactory.instance.arrayNode().add(BooleanNode.getTrue()));

        Assert.assertEquals(arr1, JsonNodeFactory.instance.arrayNode());
        Assert.assertEquals(arr2, JsonNodeFactory.instance.arrayNode().add(TextNode.valueOf("test")));
        Assert.assertEquals(arr3, JsonNodeFactory.instance.arrayNode().add(BooleanNode.getTrue()));
    }

    @Test
    public void testNewJsonNode() {
        BigDecimal val1 = BigDecimal.valueOf(50);
        BigInteger val2 = BigInteger.valueOf(50);
        Boolean val3 = true;
        Boolean val4 = false;
        byte[] val5 = "hello".getBytes(StandardCharsets.UTF_8);
        Double val6 = 1.1;
        Float val7 = 1.1f;
        Integer val8 = 1;
        Long val9 = 1L;
        HashMap val10 = new HashMap();
        Short val11 = Short.valueOf("1");
        String val12 = "hello";

        JsonNode node1  = Jive.newJsonNode(val1);
        JsonNode node2  = Jive.newJsonNode(val2);
        JsonNode node3  = Jive.newJsonNode(val3);
        JsonNode node4  = Jive.newJsonNode(val4);
        JsonNode node5  = Jive.newJsonNode(val5);
        JsonNode node6  = Jive.newJsonNode(val6);
        JsonNode node7  = Jive.newJsonNode(val7);
        JsonNode node8  = Jive.newJsonNode(val8);
        JsonNode node9  = Jive.newJsonNode(val9);
        JsonNode node10 = Jive.newJsonNode(val10);
        JsonNode node11 = Jive.newJsonNode(val11);
        JsonNode node12 = Jive.newJsonNode(val12);

        Assert.assertEquals(node1,  DecimalNode.valueOf(val1));
        Assert.assertEquals(node2,  BigIntegerNode.valueOf(val2));
        Assert.assertEquals(node3,  BooleanNode.getTrue());
        Assert.assertEquals(node4,  BooleanNode.getFalse());
        Assert.assertEquals(node5,  BinaryNode.valueOf(val5));
        Assert.assertEquals(node6,  DoubleNode.valueOf(val6));
        Assert.assertEquals(node7,  FloatNode.valueOf(val7));
        Assert.assertEquals(node8,  IntNode.valueOf(val8));
        Assert.assertEquals(node9,  LongNode.valueOf(val9));
        Assert.assertEquals(node10, new POJONode(node10));
        Assert.assertEquals(node11, ShortNode.valueOf(val11));
        Assert.assertEquals(node12, TextNode.valueOf(val12));
    }

    @Test
    public void testNewJsonEntry() {
        BigDecimal val1 = BigDecimal.valueOf(50);
        BigInteger val2 = BigInteger.valueOf(50);
        Boolean val3 = true;
        Boolean val4 = false;
        byte[] val5 = "hello".getBytes(StandardCharsets.UTF_8);
        Double val6 = 1.1;
        Float val7 = 1.1f;
        Integer val8 = 1;
        Long val9 = 1L;
        HashMap val10 = new HashMap();
        Short val11 = Short.valueOf("1");
        String val12 = "hello";

        Map.Entry<String, JsonNode> node1  = Jive.newJsonEntry("key", val1);
        Map.Entry<String, JsonNode> node2  = Jive.newJsonEntry("key", val2);
        Map.Entry<String, JsonNode> node3  = Jive.newJsonEntry("key", val3);
        Map.Entry<String, JsonNode> node4  = Jive.newJsonEntry("key", val4);
        Map.Entry<String, JsonNode> node5  = Jive.newJsonEntry("key", val5);
        Map.Entry<String, JsonNode> node6  = Jive.newJsonEntry("key", val6);
        Map.Entry<String, JsonNode> node7  = Jive.newJsonEntry("key", val7);
        Map.Entry<String, JsonNode> node8  = Jive.newJsonEntry("key", val8);
        Map.Entry<String, JsonNode> node9  = Jive.newJsonEntry("key", val9);
        Map.Entry<String, JsonNode> node10 = Jive.newJsonEntry("key", val10);
        Map.Entry<String, JsonNode> node11 = Jive.newJsonEntry("key", val11);
        Map.Entry<String, JsonNode> node12 = Jive.newJsonEntry("key", val12);

        Assert.assertEquals(node1,  new AbstractMap.SimpleEntry<>("key", DecimalNode.valueOf(val1)));
        Assert.assertEquals(node2,  new AbstractMap.SimpleEntry<>("key", BigIntegerNode.valueOf(val2)));
        Assert.assertEquals(node3,  new AbstractMap.SimpleEntry<>("key", BooleanNode.getTrue()));
        Assert.assertEquals(node4,  new AbstractMap.SimpleEntry<>("key", BooleanNode.getFalse()));
        Assert.assertEquals(node5,  new AbstractMap.SimpleEntry<>("key", BinaryNode.valueOf(val5)));
        Assert.assertEquals(node6,  new AbstractMap.SimpleEntry<>("key", DoubleNode.valueOf(val6)));
        Assert.assertEquals(node7,  new AbstractMap.SimpleEntry<>("key", FloatNode.valueOf(val7)));
        Assert.assertEquals(node8,  new AbstractMap.SimpleEntry<>("key", IntNode.valueOf(val8)));
        Assert.assertEquals(node9,  new AbstractMap.SimpleEntry<>("key", LongNode.valueOf(val9)));
        Assert.assertEquals(node10, new AbstractMap.SimpleEntry<>("key", new POJONode(val10)));
        Assert.assertEquals(node11, new AbstractMap.SimpleEntry<>("key", ShortNode.valueOf(val11)));
        Assert.assertEquals(node12, new AbstractMap.SimpleEntry<>("key", TextNode.valueOf(val12)));
    }

    @Test
    public void testNewObjectNode() {
        ObjectNode obj1 = Jive.newObjectNode();
        ObjectNode obj2 = Jive.newObjectNode(
            new AbstractMap.SimpleEntry<>("key1", TextNode.valueOf("1")),
            new AbstractMap.SimpleEntry<>("key2", TextNode.valueOf("2")),
            new AbstractMap.SimpleEntry<>("key3", TextNode.valueOf("3"))
        );

        Assert.assertEquals(obj1.size(), 0);
        Assert.assertEquals(obj2.size(), 3);

        Assert.assertEquals(obj2.get("key1"), TextNode.valueOf("1"));
        Assert.assertEquals(obj2.get("key2"), TextNode.valueOf("2"));
        Assert.assertEquals(obj2.get("key3"), TextNode.valueOf("3"));
    }

    @Test
    public void testStreamArrayNode() {
        ArrayNode arr1 = JsonNodeFactory.instance.arrayNode()
            .add(1)
            .add(2)
            .add(3);

        ArrayNode arr2 = Jive
            .stream(arr1)
            .limit(1)
            .collect(JiveCollectors.toArrayNode());

        Assert.assertEquals(arr2, JsonNodeFactory.instance.arrayNode().add(1));
    }

    @Test
    public void testStreamObjectNode() {
        ObjectNode obj1 = JsonNodeFactory.instance.objectNode()
            .put("key1", 1)
            .put("key2", 2)
            .put("key3", 3);

        ObjectNode obj2 = Jive
            .stream(obj1)
            .limit(1)
            .collect(JiveCollectors.toObjectNode());

        Assert.assertEquals(obj2, JsonNodeFactory.instance.objectNode().put("key1", 1));
    }

}
