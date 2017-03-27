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
import com.zackehh.jackson.scope.SafeExecution;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.zackehh.jackson.stream.JiveCollectors.toArrayNode;
import static com.zackehh.jackson.stream.JiveCollectors.toObjectNode;

/**
 * The Jive class provides various utilities for working with Jackson
 * JSON modules, specifically ArrayNode and ObjectNode values.
 *
 * The intent is to remove friction from user interaction with Jackson
 * bindings by providing just enough to make implementing new behaviour
 * easy. One example is the ${@link #stream(ArrayNode)} function which
 * allows a user to begin working with the Stream interface against their
 * JSON values.
 */
public class Jive {

    /**
     * Private constructor as this class serves only static purposes.
     */
    private Jive() { }

    /**
     * Executes a scoped function with the provided ObjectMapper.
     *
     * Any thrown IOException instances will be caught and will
     * return an empty Optional to the user. In the case of a
     * successful execution, the return value of the called block
     * will be wrapped into an Optional and returned.
     *
     * This function can be used to remove the need to manually
     * handle exceptions when calling ObjectMapper functions.
     *
     * @param mapper the ObjectMapper instance to execute with.
     * @param execution the execution implementation to call.
     * @param <T> the type of the block return value.
     * @return an Optional containing a potential result.
     */
    public static <T> Optional<T> execute(ObjectMapper mapper, SafeExecution<T> execution) {
        try {
            return Optional.ofNullable(execution.apply(mapper));
        } catch(IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Constructs a new Jackson ArrayNode instance.
     *
     * @return a new Jackson ArrayNode instance.
     */
    public static ArrayNode newArrayNode() {
        return JsonNodeFactory.instance.arrayNode();
    }

    /**
     * Constructs a new Jackson ArrayNode instance.
     *
     * The instance is populated with the provided JsonNode instances
     * and returned back to the caller.
     *
     * @param nodes the nodes to populate with.
     * @return a newly populated ArrayNode instance.
     */
    public static ArrayNode newArrayNode(JsonNode... nodes) {
        return arrayCollect(Arrays.stream(nodes));
    }

    /**
     * Constructs a new Iterable of type JsonNode using the provided
     * input.
     *
     * If the provided input is an ArrayNode, it is returned as is due
     * to it being already iterable. Any other JsonNode is wrapped in
     * a new instance of ArrayNode before being returned.
     *
     * If the provided value is null, an empty ArrayNode is returned
     * to avoid receiving a NullPointerException.
     *
     * @param node the node to use when seeding the Iterable.
     * @return a new Iterable of type JsonNode.
     */
    public static Iterable<JsonNode> newIterable(JsonNode node) {
        return node == null ? newArrayNode() : node.isArray() ? node : newArrayNode(node);
    }

    /**
     * Constructs a new JSON Map.Entry from a JsonNode value.
     *
     * @param key the key of this entry as a String.
     * @param value the JsonNode value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, JsonNode value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    /**
     * Constructs a new JSON Map.Entry from a BigDecimal value.
     *
     * @param key the key of this entry as a String.
     * @param value the BigDecimal value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, BigDecimal value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a BigInteger value.
     *
     * @param key the key of this entry as a String.
     * @param value the BigInteger value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, BigInteger value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a Boolean value.
     *
     * @param key the key of this entry as a String.
     * @param value the Boolean value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Boolean value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a byte[] value.
     *
     * @param key the key of this entry as a String.
     * @param value the byte[] value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, byte[] value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a Double value.
     *
     * @param key the key of this entry as a String.
     * @param value the Double value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Double value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a Float value.
     *
     * @param key the key of this entry as a String.
     * @param value the Float value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Float value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from an Integer value.
     *
     * @param key the key of this entry as a String.
     * @param value the Integer value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Integer value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a Long value.
     *
     * @param key the key of this entry as a String.
     * @param value the Long value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Long value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from an Object value.
     *
     * @param key the key of this entry as a String.
     * @param value the Object value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Object value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a Short value.
     *
     * @param key the key of this entry as a String.
     * @param value the Short value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, Short value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JSON Map.Entry from a String value.
     *
     * @param key the key of this entry as a String.
     * @param value the String value of this entry.
     * @return a new Map.Entry instance.
     */
    public static Map.Entry<String, JsonNode> newJsonEntry(String key, String value) {
        return newJsonEntry(key, newJsonNode(value));
    }

    /**
     * Constructs a new JsonNode from a BigDecimal value.
     *
     * @param value the BigDecimal value of this entry.
     * @return a new JsonNode instance.
     */
    public static DecimalNode newJsonNode(BigDecimal value){
        return DecimalNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a BigInteger value.
     *
     * @param value the BigInteger value of this entry.
     * @return a new JsonNode instance.
     */
    public static BigIntegerNode newJsonNode(BigInteger value){
        return BigIntegerNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a Boolean value.
     *
     * @param value the Boolean value of this entry.
     * @return a new JsonNode instance.
     */
    public static BooleanNode newJsonNode(Boolean value){
        return BooleanNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a byte[] value.
     *
     * @param value the byte[] value of this entry.
     * @return a new JsonNode instance.
     */
    public static BinaryNode newJsonNode(byte[] value){
        return BinaryNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a Double value.
     *
     * @param value the Double value of this entry.
     * @return a new JsonNode instance.
     */
    public static DoubleNode newJsonNode(Double value){
        return DoubleNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a Float value.
     *
     * @param value the Float value of this entry.
     * @return a new JsonNode instance.
     */
    public static FloatNode newJsonNode(Float value){
        return FloatNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from an Integer value.
     *
     * @param value the Integer value of this entry.
     * @return a new JsonNode instance.
     */
    public static IntNode newJsonNode(Integer value){
        return IntNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a Long value.
     *
     * @param value the Long value of this entry.
     * @return a new JsonNode instance.
     */
    public static LongNode newJsonNode(Long value){
        return LongNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from an Object value.
     *
     * @param value the Object value of this entry.
     * @return a new JsonNode instance.
     */
    public static POJONode newJsonNode(Object value) {
        return new POJONode(value);
    }

    /**
     * Constructs a new JsonNode from a Short value.
     *
     * @param value the Short value of this entry.
     * @return a new JsonNode instance.
     */
    public static ShortNode newJsonNode(Short value){
        return ShortNode.valueOf(value);
    }

    /**
     * Constructs a new JsonNode from a String value.
     *
     * @param value the String value of this entry.
     * @return a new JsonNode instance.
     */
    public static TextNode newJsonNode(String value){
        return TextNode.valueOf(value);
    }

    /**
     * Constructs a new Jackson ObjectNode instance.
     *
     * @return a new Jackson ObjectNode instance.
     */
    public static ObjectNode newObjectNode() {
        return JsonNodeFactory.instance.objectNode();
    }

    /**
     * Constructs a new Jackson ObjectNode instance.
     *
     * The instance is populated with the provided entries and
     * returned back to the caller.
     *
     * @param entries the entries to populate with.
     * @return a newly populated ObjectNode instance.
     */
    @SafeVarargs
    public static ObjectNode newObjectNode(Map.Entry<String, JsonNode>... entries) {
        return objectCollect(Arrays.stream(entries));
    }

    /**
     * Creates a new Stream from the provided ArrayNode.
     *
     * The Stream is created as a serial Stream so that the caller may
     * decide to turn parallel as needed.
     *
     * @param node the ArrayNode to stream.
     * @return a new Stream of the input elements.
     */
    public static Stream<JsonNode> stream(ArrayNode node) {
        return StreamSupport.stream(node.spliterator(), false);
    }

    /**
     * Creates a new Stream from the provided ObjectNode.
     *
     * The Stream is created as a serial Stream so that the caller may
     * decide to turn parallel as needed.
     *
     * @param node the ObjectNode to stream.
     * @return a new Stream of the input entries.
     */
    public static Stream<Map.Entry<String, JsonNode>> stream(ObjectNode node) {
        return StreamSupport.stream(((Iterable<Map.Entry<String, JsonNode>>) node::fields).spliterator(), false);
    }

    /**
     * Transforms a provided ArrayNode into a new ArrayNode instance.
     *
     * The provided Function maps the base Stream to a new Stream in
     * order to transform the ArrayNode internally. The final nodes
     * are collected back into a new ArrayNode.
     *
     * @param node the ArrayNode to transform.
     * @param transformer the Stream transformer.
     * @return a new ArrayNode instance after transformation.
     */
    public static ArrayNode transform(ArrayNode node, Function<Stream<JsonNode>, Stream<JsonNode>> transformer) {
        return arrayCollect(transformer.apply(stream(node)));
    }

    /**
     * Transforms a provided ObjectNode into a new ObjectNode instance.
     *
     * The provided Function maps the base Stream to a new Stream in
     * order to transform the ObjectNode internally. The final entry
     * pairs are collected back into a new ObjectNode.
     *
     * @param node the ObjectNode to transform.
     * @param transformer the Stream transformer.
     * @return a new ObjectNode instance after transformation.
     */
    public static ObjectNode transform(ObjectNode node, Function<Stream<Map.Entry<String, JsonNode>>, Stream<Map.Entry<String, JsonNode>>> transformer) {
        return objectCollect(transformer.apply(stream(node)));
    }

    /**
     * Collects a provided JsonNode Stream into an ArrayNode.
     *
     * @param stream the Stream instance to collect.
     * @return a new ArrayNode after collection.
     */
    private static ArrayNode arrayCollect(Stream<JsonNode> stream) {
        return stream.collect(toArrayNode());
    }

    /**
     * Collects a provided entry Stream into an ObjectNode.
     *
     * @param stream the Stream instance to collect.
     * @return a new ObjectNode after collection.
     */
    private static ObjectNode objectCollect(Stream<Map.Entry<String, JsonNode>> stream) {
        return stream.collect(toObjectNode());
    }

}
