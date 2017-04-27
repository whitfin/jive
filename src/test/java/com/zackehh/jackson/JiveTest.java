package com.zackehh.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JiveTest {

    @Test
    public void testCreate() throws Exception {
        Constructor c = Jive.class.getDeclaredConstructor();
        c.setAccessible(true);
        c.newInstance();
    }

    @Test
    public void testConcat() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        ArrayNode arr2 = arrayNode()
                .add(4).add(5).add(6);

        ArrayNode arr3 = Jive.concat(arr1);
        ArrayNode arr4 = Jive.concat(arr1, arr2);
        ArrayNode arr5 = Jive.concat(arr2, arr1);

        Assert.assertEquals(arr3, arr1);
        Assert.assertEquals(arr4, arrayNode().addAll(arr1).addAll(arr2));
        Assert.assertEquals(arr5, arrayNode().addAll(arr2).addAll(arr1));
    }

    @Test
    public void testContainsArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        Boolean result1 = Jive.contains(arr1, IntNode.valueOf(2));
        Boolean result2 = Jive.contains(arr1, TextNode.valueOf("2"));

        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void testContainsObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        Boolean result1 = Jive.contains(obj1, IntNode.valueOf(2));
        Boolean result2 = Jive.contains(obj1, TextNode.valueOf("2"));

        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void testDrop() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        ArrayNode arr2 = arrayNode();

        ArrayNode arr3 = Jive.drop(arr1, 0);
        ArrayNode arr4 = Jive.drop(arr1, 2);
        ArrayNode arr5 = Jive.drop(arr2, 0);
        ArrayNode arr6 = Jive.drop(arr2, 2);

        Assert.assertEquals(arr3, arr1);
        Assert.assertEquals(arr4, arrayNode().add(3));
        Assert.assertEquals(arr5, arr2);
        Assert.assertEquals(arr6, arr2);
    }

    @Test
    public void testEveryArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        Boolean result1 = Jive.every(arr1, JsonNode::isNumber);
        Boolean result2 = Jive.every(arr1, jsonNode -> !jsonNode.isMissingNode());

        Assert.assertFalse(result1);
        Assert.assertTrue(result2);
    }

    @Test
    public void testEveryObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        Boolean result1 = Jive.every(obj1, stringJsonNodeEntry -> stringJsonNodeEntry.getValue().isNumber());
        Boolean result2 = Jive.every(obj1, stringJsonNodeEntry -> !stringJsonNodeEntry.getValue().isMissingNode());

        Assert.assertFalse(result1);
        Assert.assertTrue(result2);
    }

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
    public void testFilterArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        ArrayNode arr2 = Jive.filter(arr1, JsonNode::isNumber);

        ArrayNode arr3 = arrayNode()
                .add(1).add(2).add(3);

        Assert.assertEquals(arr2, arr3);
    }

    @Test
    public void testFilterObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        ObjectNode obj2 = Jive.filter(obj1, jsonNodeEntry -> jsonNodeEntry.getValue().isNumber());

        ObjectNode obj3 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        Assert.assertEquals(obj2, obj3);
    }

    @Test
    public void testFindArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        Optional<JsonNode> node1 = Jive.find(arr1, JsonNode::isTextual);
        Optional<JsonNode> node2 = Jive.find(arr1, JsonNode::isArray);

        Assert.assertTrue(node1.isPresent());
        Assert.assertFalse(node2.isPresent());

        Assert.assertEquals(node1.orElse(null), TextNode.valueOf("4"));
    }

    @Test
    public void testFindObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        Optional<Map.Entry<String, JsonNode>> node1 = Jive.find(obj1, jsonNodeEntry -> jsonNodeEntry.getValue().isTextual());
        Optional<Map.Entry<String, JsonNode>> node2 = Jive.find(obj1, jsonNodeEntry -> jsonNodeEntry.getValue().isArray());

        Assert.assertTrue(node1.isPresent());
        Assert.assertFalse(node2.isPresent());

        Assert.assertEquals(node1.orElse(null), new AbstractMap.SimpleEntry<String, JsonNode>("key4", TextNode.valueOf("4")));
    }

    @Test
    public void testKeys() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        Set<String> keys = Jive.keys(obj1);

        Assert.assertEquals(keys.size(), 5);
        Assert.assertTrue(keys.contains("key1"));
        Assert.assertTrue(keys.contains("key2"));
        Assert.assertTrue(keys.contains("key3"));
        Assert.assertTrue(keys.contains("key4"));
        Assert.assertTrue(keys.contains("key5"));
    }

    @Test
    public void testLast() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        ArrayNode arr2 = arrayNode();

        JsonNode node1 = Jive.last(arr1);
        JsonNode node2 = Jive.last(arr2);

        Assert.assertEquals(node1, IntNode.valueOf(3));
        Assert.assertEquals(node2, MissingNode.getInstance());
    }

    @Test
    public void testMapArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        ArrayNode arr2 = Jive.map(arr1, jsonNode -> TextNode.valueOf(jsonNode.asText()));

        ArrayNode arr3 = arrayNode()
                .add("1").add("2").add("3");

        Assert.assertEquals(arr2, arr3);
    }

    @Test
    public void testMapObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        ObjectNode obj2 = Jive.map(obj1, jsonNodeEntry -> {
            TextNode value = TextNode.valueOf(jsonNodeEntry.getValue().asText());
            return new AbstractMap.SimpleEntry<>(jsonNodeEntry.getKey(), value);
        });

        ObjectNode obj3 = objectNode()
                .put("key1", "1")
                .put("key2", "2")
                .put("key3", "3");

        Assert.assertEquals(obj2, obj3);
    }

    @Test
    public void testMerge() {
        ObjectNode obj1 = objectNode()
                .put("key1", "1")
                .put("key2", "2")
                .put("key3", "3");

        ObjectNode obj2 = objectNode()
                .put("key3", 1)
                .put("key4", 2)
                .put("key5", 3);

        ObjectNode obj3 = objectNode()
                .put("key6", "!")
                .put("key7", "@")
                .put("key1", "#");

        ObjectNode obj4 = Jive.merge(obj1);
        ObjectNode obj5 = Jive.merge(obj1, obj2);
        ObjectNode obj6 = Jive.merge(obj1, obj2, obj3);

        Assert.assertEquals(obj4, obj1);

        Assert.assertEquals(obj5.size(), 5);
        Assert.assertEquals(obj5.path("key1"), TextNode.valueOf("1"));
        Assert.assertEquals(obj5.path("key2"), TextNode.valueOf("2"));
        Assert.assertEquals(obj5.path("key3"), IntNode.valueOf(1));
        Assert.assertEquals(obj5.path("key4"), IntNode.valueOf(2));
        Assert.assertEquals(obj5.path("key5"), IntNode.valueOf(3));

        Assert.assertEquals(obj6.size(), 7);
        Assert.assertEquals(obj6.path("key1"), TextNode.valueOf("#"));
        Assert.assertEquals(obj6.path("key2"), TextNode.valueOf("2"));
        Assert.assertEquals(obj6.path("key3"), IntNode.valueOf(1));
        Assert.assertEquals(obj6.path("key4"), IntNode.valueOf(2));
        Assert.assertEquals(obj6.path("key5"), IntNode.valueOf(3));
        Assert.assertEquals(obj6.path("key6"), TextNode.valueOf("!"));
        Assert.assertEquals(obj6.path("key6"), TextNode.valueOf("@"));
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
        Iterable<JsonNode> arr3 = Jive.newIterable(arrayNode().add(BooleanNode.getTrue()));

        Assert.assertEquals(arr1, arrayNode());
        Assert.assertEquals(arr2, arrayNode().add(TextNode.valueOf("test")));
        Assert.assertEquals(arr3, arrayNode().add(BooleanNode.getTrue()));
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
        Short val10 = Short.valueOf("1");
        String val11 = "hello";

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

        Assert.assertEquals(node1,  DecimalNode.valueOf(val1));
        Assert.assertEquals(node2,  BigIntegerNode.valueOf(val2));
        Assert.assertEquals(node3,  BooleanNode.getTrue());
        Assert.assertEquals(node4,  BooleanNode.getFalse());
        Assert.assertEquals(node5,  BinaryNode.valueOf(val5));
        Assert.assertEquals(node6,  DoubleNode.valueOf(val6));
        Assert.assertEquals(node7,  FloatNode.valueOf(val7));
        Assert.assertEquals(node8,  IntNode.valueOf(val8));
        Assert.assertEquals(node9,  LongNode.valueOf(val9));
        Assert.assertEquals(node10, ShortNode.valueOf(val10));
        Assert.assertEquals(node11, TextNode.valueOf(val11));
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
        Short val10 = Short.valueOf("1");
        String val11 = "hello";

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

        Assert.assertEquals(node1,  new AbstractMap.SimpleEntry<>("key", DecimalNode.valueOf(val1)));
        Assert.assertEquals(node2,  new AbstractMap.SimpleEntry<>("key", BigIntegerNode.valueOf(val2)));
        Assert.assertEquals(node3,  new AbstractMap.SimpleEntry<>("key", BooleanNode.getTrue()));
        Assert.assertEquals(node4,  new AbstractMap.SimpleEntry<>("key", BooleanNode.getFalse()));
        Assert.assertEquals(node5,  new AbstractMap.SimpleEntry<>("key", BinaryNode.valueOf(val5)));
        Assert.assertEquals(node6,  new AbstractMap.SimpleEntry<>("key", DoubleNode.valueOf(val6)));
        Assert.assertEquals(node7,  new AbstractMap.SimpleEntry<>("key", FloatNode.valueOf(val7)));
        Assert.assertEquals(node8,  new AbstractMap.SimpleEntry<>("key", IntNode.valueOf(val8)));
        Assert.assertEquals(node9,  new AbstractMap.SimpleEntry<>("key", LongNode.valueOf(val9)));
        Assert.assertEquals(node10, new AbstractMap.SimpleEntry<>("key", ShortNode.valueOf(val10)));
        Assert.assertEquals(node11, new AbstractMap.SimpleEntry<>("key", TextNode.valueOf(val11)));
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
    public void testNoneArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        Boolean result1 = Jive.none(arr1, JsonNode::isNumber);
        Boolean result2 = Jive.none(arr1, JsonNode::isMissingNode);

        Assert.assertFalse(result1);
        Assert.assertTrue(result2);
    }

    @Test
    public void testNoneObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        Boolean result1 = Jive.none(obj1, stringJsonNodeEntry -> stringJsonNodeEntry.getValue().isNumber());
        Boolean result2 = Jive.none(obj1, stringJsonNodeEntry -> stringJsonNodeEntry.getValue().isMissingNode());

        Assert.assertFalse(result1);
        Assert.assertTrue(result2);
    }

    @Test
    public void testOmit() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        ObjectNode obj2 = Jive.omit(obj1, "key1", "key2", "key3");
        ObjectNode obj3 = Jive.omit(obj1, new HashSet<>(Arrays.asList("key2", "key3")));

        Assert.assertEquals(obj2.size(), 0);
        Assert.assertEquals(obj3.size(), 1);
        Assert.assertEquals(obj3.path("key1"), IntNode.valueOf(1));
    }

    @Test
    public void testPick() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        ObjectNode obj2 = Jive.pick(obj1, "key1", "key2", "key3");
        ObjectNode obj3 = Jive.pick(obj1, new HashSet<>(Arrays.asList("key2", "key3")));

        Assert.assertEquals(obj2.size(), 3);
        Assert.assertEquals(obj3.size(), 2);
        Assert.assertEquals(obj2.path("key1"), IntNode.valueOf(1));
        Assert.assertEquals(obj2.path("key2"), IntNode.valueOf(2));
        Assert.assertEquals(obj2.path("key3"), IntNode.valueOf(3));
        Assert.assertEquals(obj3.path("key2"), IntNode.valueOf(2));
        Assert.assertEquals(obj3.path("key3"), IntNode.valueOf(3));
    }

    @Test
    public void testReduceArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        int reduced = Jive.reduce(arr1, 0, (acc, node) -> acc + node.asInt(0));

        Assert.assertEquals(reduced, 6);
    }

    @Test
    public void testReduceObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3);

        int reduced = Jive.reduce(obj1, 0, (acc, node) -> acc + node.getValue().asInt(0));

        Assert.assertEquals(reduced, 6);
    }

    @Test
    public void testRejectArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        ArrayNode arr2 = Jive.reject(arr1, JsonNode::isNumber);

        ArrayNode arr3 = arrayNode()
                .add("4").add("5");

        Assert.assertEquals(arr2, arr3);
    }

    @Test
    public void testRejectObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        ObjectNode obj2 = Jive.reject(obj1, jsonNodeEntry -> jsonNodeEntry.getValue().isNumber());

        ObjectNode obj3 = objectNode()
                .put("key4", "4")
                .put("key5", "5");

        Assert.assertEquals(obj2, obj3);
    }

    @Test
    public void testSomeArrayNode() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        Boolean result1 = Jive.some(arr1, JsonNode::isNumber);
        Boolean result2 = Jive.some(arr1, JsonNode::isMissingNode);

        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void testSomeObjectNode() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        Boolean result1 = Jive.some(obj1, stringJsonNodeEntry -> stringJsonNodeEntry.getValue().isNumber());
        Boolean result2 = Jive.some(obj1, stringJsonNodeEntry -> stringJsonNodeEntry.getValue().isMissingNode());

        Assert.assertTrue(result1);
        Assert.assertFalse(result2);
    }

    @Test
    public void testTake() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3);

        ArrayNode arr2 = arrayNode();

        ArrayNode arr3 = Jive.take(arr1, 0);
        ArrayNode arr4 = Jive.take(arr1, 2);
        ArrayNode arr5 = Jive.take(arr2, 0);
        ArrayNode arr6 = Jive.take(arr2, 2);

        Assert.assertEquals(arr3, arrayNode());
        Assert.assertEquals(arr4, arrayNode().add(1).add(2));
        Assert.assertEquals(arr5, arrayNode());
        Assert.assertEquals(arr6, arrayNode());
    }

    @Test
    public void testUniq() {
        ArrayNode arr1 = arrayNode()
                .add(1).add(2).add(3).add(2);

        ArrayNode arr2 = arrayNode();

        ArrayNode arr3 = Jive.uniq(arr1);
        ArrayNode arr4 = Jive.uniq(arr2);

        Assert.assertEquals(arr3, arrayNode().add(1).add(2).add(3));
        Assert.assertEquals(arr4, arrayNode());
    }

    @Test
    public void testValues() {
        ObjectNode obj1 = objectNode()
                .put("key1", 1)
                .put("key2", 2)
                .put("key3", 3)
                .put("key4", "4")
                .put("key5", "5");

        ArrayNode values = arrayNode()
                .add(1).add(2).add(3).add("4").add("5");

        ArrayNode result = Jive.values(obj1);

        Assert.assertEquals(result, values);
    }

    private ArrayNode arrayNode() {
        return JsonNodeFactory.instance.arrayNode();
    }

    private ObjectNode objectNode() {
        return JsonNodeFactory.instance.objectNode();
    }

}
