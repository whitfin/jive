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
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.zackehh.jackson.scope.SafeExecution;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.zackehh.jackson.stream.JiveCollectors.toArrayNode;
import static com.zackehh.jackson.stream.JiveCollectors.toObjectNode;
import static java.util.Objects.requireNonNull;

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
     * Concatenates multiple ArrayNode instances.
     *
     * The returned value is a new ArrayNode instance, rather than
     * modifying one of the input instances.
     *
     * @param nodes the ArrayNode instances to concatenate.
     * @return a new ArrayNode instance of all provided nodes.
     */
    public static ArrayNode concat(ArrayNode... nodes) {
        return arrayCollect(Arrays.stream(nodes).flatMap(Jive::stream));
    }

    /**
     * Determines whether a JsonNode exists in an ArrayNode.
     *
     * @param node the ArrayNode to search within.
     * @param value the JsonNode value to search for.
     * @return true if the JsonNode value can be found.
     */
    public static boolean contains(ArrayNode node, JsonNode value) {
        return some(node, e -> e.equals(value));
    }

    /**
     * Determines whether a JsonNode exists in an ObjectNode as
     * a value entry.
     *
     * @param node the ObjectNode to search within.
     * @param value the JsonNode value to search for.
     * @return true if the JsonNode value can be found.
     */
    public static boolean contains(ObjectNode node, JsonNode value) {
        return some(node, e -> e.getValue().equals(value));
    }

    /**
     * Returns a new ArrayNode with the first N items removed.
     *
     * This does not modify the original ArrayNode input, but
     * returns a new ArrayNode instance with the nodes added.
     *
     * @param node the node to drop from.
     * @param count the number of nodes to drop.
     * @return a new ArrayNode instance with dropped nodes.
     */
    public static ArrayNode drop(ArrayNode node, int count) {
        return transform(node, s -> s.skip(count));
    }

    /**
     * Executes a scoped function with caught Exceptions.
     *
     * Any thrown IOException instances will be caught and will
     * return an empty Optional to the user. In the case of a
     * successful execution, the return value of the called block
     * will be wrapped into an Optional and returned.
     *
     * This function can be used to remove the need to manually
     * handle exceptions when calling ObjectMapper functions.
     *
     * @param execution the execution implementation to call.
     * @param <T> the type of the block return value.
     * @return an Optional containing a potential result.
     */
    public static <T> Optional<T> execute(Callable<T> execution) {
        try {
            return Optional.ofNullable(execution.call());
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Executes a scoped function with the provided ObjectMapper.
     *
     * Any thrown Exception instances will be caught and will
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
        return execute(() -> execution.apply(mapper));
    }

    /**
     * Determines whether all JsonNodes within an ArrayNode fit
     * a provided Predicate condition.
     *
     * @param node the ArrayNode to iterate through.
     * @param predicate the condition to verify matches against.
     * @return true if every JsonNode value matches the Predicate.
     */
    public static boolean every(ArrayNode node, Predicate<JsonNode> predicate) {
        return stream(node).allMatch(predicate);
    }

    /**
     * Determines whether all entries within an ObjectNode fit
     * a provided Predicate condition.
     *
     * @param node the ObjectNode to iterate through.
     * @param predicate the condition to verify matches against.
     * @return true if every entry value matches the Predicate.
     */
    public static boolean every(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return stream(node).allMatch(predicate);
    }

    /**
     * Filters values in an ArrayNode into a new ArrayNode.
     *
     * This does not modify the input ArrayNode but collects
     * the filtered values into a new ArrayNode instance.
     *
     * @param node the ArrayNode to filter through.
     * @param predicate the condition used to filter values.
     * @return an ArrayNode instance containing filtered values.
     */
    public static ArrayNode filter(ArrayNode node, Predicate<JsonNode> predicate) {
        return transform(node, s -> s.filter(predicate));
    }

    /**
     * Filters values in an ObjectNode into a new ObjectNode.
     *
     * This does not modify the input ObjectNode but collects
     * the filtered values into a new ObjectNode instance.
     *
     * @param node the ObjectNode to filter through.
     * @param predicate the condition used to filter values.
     * @return an ObjectNode instance containing filtered values.
     */
    public static ObjectNode filter(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return transform(node, s -> s.filter(predicate));
    }

    /**
     * Attempts to find an ArrayNode value matching a criteria.
     *
     * @param node the ArrayNode to search.
     * @param predicate the condition to match the values against.
     * @return a potential JsonNode matching the predicate.
     */
    public static Optional<JsonNode> find(ArrayNode node, Predicate<JsonNode> predicate) {
        return stream(node).filter(predicate).findFirst();
    }

    /**
     * Attempts to find an ObjectNode entry matching a criteria.
     *
     * @param node the ObjectNode to search.
     * @param predicate the condition to match the entries against.
     * @return a potential Map.Entry matching the predicate.
     */
    public static Optional<Map.Entry<String, JsonNode>> find(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return stream(node).filter(predicate).findFirst();
    }

    /**
     * Returns an Set of Strings instance containing all keys of
     * the provided ObjectNode.
     *
     * @param node the ObjectNode to retrieve keys for.
     * @return a Set containing all keys.
     */
    public static Set<String> keys(ObjectNode node) {
        return stream(node).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Retrieves the last value in an ArrayNode.
     *
     * If the ArrayNode is empty, a MissingNode instance
     * will be returned.
     *
     * @param node a new ArrayNode instance.
     * @return a JsonNode instance.
     */
    public static JsonNode last(ArrayNode node) {
        return node.path(node.size() - 1);
    }

    /**
     * Maps values in an ArrayNode into a new ArrayNode.
     *
     * This does not modify the original ArrayNode, rather
     * it collects the mapped values into a new ArrayNode.
     *
     * @param node the ArrayNode to map.
     * @param function the mapping function.
     * @return an ArrayNode containing the mapped values.
     */
    public static ArrayNode map(ArrayNode node, Function<JsonNode, JsonNode> function) {
        return transform(node, s -> s.map(function));
    }

    /**
     * Maps entries in an ObjectNode into a new ObjectNode.
     *
     * This does not modify the original ObjectNode, rather
     * it collects the mapped entries into a new ObjectNode.
     *
     * @param node the ObjectNode to map.
     * @param function the mapping function.
     * @return an ObjectNode containing the mapped values.
     */
    public static ObjectNode map(ObjectNode node, Function<Map.Entry<String, JsonNode>, Map.Entry<String, JsonNode>> function) {
        return transform(node, s -> s.map(function));
    }

    /**
     * Performs a shallow merge of multiple ObjectNodes.
     *
     * This is not a recursive merge, it will just overwrite the value
     * on the left in case of a clash.
     *
     * @param nodes the ObjectNode instances to merge.
     * @return a new ObjectNode instance of merged keys.
     */
    public static ObjectNode merge(ObjectNode... nodes) {
        return objectCollect(Arrays.stream(nodes).flatMap(Jive::stream));
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
     * Constructs a new BigIntegerNode from a BigDecimal value.
     *
     * @param value the BigIntegerNode value of this entry.
     * @return a new BigIntegerNode instance.
     */
    public static BigIntegerNode newBigIntegerNode(@Nonnull BigInteger value) {
        return BigIntegerNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new DecimalNode from a BigDecimal value.
     *
     * @param value the BigDecimal value of this entry.
     * @return a new DecimalNode instance.
     */
    public static DecimalNode newDecimalNode(@Nonnull BigDecimal value) {
        return DecimalNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new BooleanNode from a Boolean value.
     *
     * @param value the Boolean value of this entry.
     * @return a new BooleanNode instance.
     */
    public static BooleanNode newBooleanNode(@Nonnull Boolean value) {
        return BooleanNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new BinaryNode from a byte[] value.
     *
     * @param value the byte[] value of this entry.
     * @return a new BinaryNode instance.
     */
    public static BinaryNode newBinaryNode(@Nonnull byte[] value) {
        return BinaryNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new DoubleNode from a Double value.
     *
     * @param value the Double value of this entry.
     * @return a new DoubleNode instance.
     */
    public static DoubleNode newDoubleNode(@Nonnull Double value) {
        return DoubleNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new FloatNode from a Float value.
     *
     * @param value the Float value of this entry.
     * @return a new FloatNode instance.
     */
    public static FloatNode newFloatNode(@Nonnull Float value) {
        return FloatNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new IntNode from an Integer value.
     *
     * @param value the Integer value of this entry.
     * @return a new IntNode instance.
     */
    public static IntNode newIntNode(@Nonnull Integer value) {
        return IntNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new LongNode from a Long value.
     *
     * @param value the Long value of this entry.
     * @return a new LongNode instance.
     */
    public static LongNode newLongNode(@Nonnull Long value) {
        return LongNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new POJONode from an Object value.
     *
     * @param value the Object value of this entry.
     * @return a new POJONode instance.
     */
    public static POJONode newPojoNode(@Nonnull Object value) {
        return new POJONode(requireNonNull(value));
    }

    /**
     * Constructs a new ShortNode from a Short value.
     *
     * @param value the Short value of this entry.
     * @return a new ShortNode instance.
     */
    public static ShortNode newShortNode(@Nonnull Short value) {
        return ShortNode.valueOf(requireNonNull(value));
    }

    /**
     * Constructs a new TextNode from a String value.
     *
     * @param value the String value of this entry.
     * @return a new TextNode instance.
     */
    public static TextNode newTextNode(@Nonnull String value) {
        return TextNode.valueOf(requireNonNull(value));
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
    public static JsonNode newJsonNode(BigDecimal value) {
        return safeNode(value, Jive::newDecimalNode);
    }

    /**
     * Constructs a new JsonNode from a BigInteger value.
     *
     * @param value the BigInteger value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(BigInteger value) {
        return safeNode(value, Jive::newBigIntegerNode);
    }

    /**
     * Constructs a new JsonNode from a Boolean value.
     *
     * @param value the Boolean value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Boolean value) {
        return safeNode(value, Jive::newBooleanNode);
    }

    /**
     * Constructs a new JsonNode from a byte[] value.
     *
     * @param value the byte[] value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(byte[] value) {
        return safeNode(value, Jive::newBinaryNode);
    }

    /**
     * Constructs a new JsonNode from a Double value.
     *
     * @param value the Double value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Double value) {
        return safeNode(value, Jive::newDoubleNode);
    }

    /**
     * Constructs a new JsonNode from a Float value.
     *
     * @param value the Float value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Float value) {
        return safeNode(value, Jive::newFloatNode);
    }

    /**
     * Constructs a new JsonNode from an Integer value.
     *
     * @param value the Integer value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Integer value) {
        return safeNode(value, Jive::newIntNode);
    }

    /**
     * Constructs a new JsonNode from a Long value.
     *
     * @param value the Long value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Long value) {
        return safeNode(value, Jive::newLongNode);
    }

    /**
     * Constructs a new JsonNode from an Object value.
     *
     * @param value the Object value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Object value) {
        return safeNode(value, Jive::newPojoNode);
    }

    /**
     * Constructs a new JsonNode from a Short value.
     *
     * @param value the Short value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(Short value) {
        return safeNode(value, Jive::newShortNode);
    }

    /**
     * Constructs a new JsonNode from a String value.
     *
     * @param value the String value of this entry.
     * @return a new JsonNode instance.
     */
    public static JsonNode newJsonNode(String value) {
        return safeNode(value, Jive::newTextNode);
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
     * Returns true if no values in the ArrayNode match the
     * provided predicate.
     *
     * @param node the ArrayNode to verify.
     * @param predicate the condition to match against.
     * @return true if no values match the condition.
     */
    public static boolean none(ArrayNode node, Predicate<JsonNode> predicate) {
        return !some(node, predicate);
    }

    /**
     * Returns true if no entries in the ObjectNode match the
     * provided predicate.
     *
     * @param node the ObjectNode to verify.
     * @param predicate the condition to match against.
     * @return true if no entries match the condition.
     */
    public static boolean none(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return !some(node, predicate);
    }

    /**
     * Returns an ObjectNode instance without the provided list
     * of keys using the provided ObjectNode instance.
     *
     * @param node the ObjectNode to omit from.
     * @param keys the keys to omit.
     * @return an ObjectNode without the omitted fields.
     */
    public static ObjectNode omit(ObjectNode node, String... keys) {
        return omit(node, new HashSet<>(Arrays.asList(keys)));
    }

    /**
     * Returns an ObjectNode instance without the provided list
     * of keys using the provided ObjectNode instance.
     *
     * @param node the ObjectNode to omit from.
     * @param keys the keys to omit.
     * @return an ObjectNode without the omitted fields.
     */
    public static ObjectNode omit(ObjectNode node, Collection<String> keys) {
        return reject(node, e -> keys.contains(e.getKey()));
    }

    /**
     * Returns an ObjectNode instance created from the provided list
     * of keys using the provided ObjectNode instance.
     *
     * @param node the ObjectNode to pick from.
     * @param keys the keys to pick.
     * @return an ObjectNode containing the picked fields.
     */
    public static ObjectNode pick(ObjectNode node, String... keys) {
        return pick(node, new HashSet<>(Arrays.asList(keys)));
    }

    /**
     * Returns an ObjectNode instance created from the provided Set
     * of keys using the provided ObjectNode instance.
     *
     * @param node the ObjectNode to pick from.
     * @param keys the keys to pick.
     * @return an ObjectNode containing the picked fields.
     */
    public static ObjectNode pick(ObjectNode node, Collection<String> keys) {
        return filter(node, e -> keys.contains(e.getKey()));
    }

    /**
     * Reduces an ArrayNode into a single value of type T using
     * the provided function to accumulate values.
     *
     * @param node the ArrayNode to reduce.
     * @param initial the initial accumulator state.
     * @param function the reducing function.
     * @param <T> the type of the result.
     * @return a new instance of type T.
     */
    public static <T> T reduce(ArrayNode node, T initial, BiFunction<T, JsonNode, T> function) {
        return stream(node).reduce(initial, function, new RightOperator<>());
    }

    /**
     * Reduces an ObjectNode into a single value of type T using
     * the provided function to accumulate values.
     *
     * @param node the ObjectNode to reduce.
     * @param initial the initial accumulator state.
     * @param function the reducing function.
     * @param <T> the type of the result.
     * @return a new instance of type T.
     */
    public static <T> T reduce(ObjectNode node, T initial, BiFunction<T, Map.Entry<String, JsonNode>, T> function) {
        return stream(node).reduce(initial, function, new RightOperator<>());
    }

    /**
     * Filters values in an ArrayNode into a new ArrayNode.
     *
     * This does not modify the input ArrayNode but collects
     * the filtered values into a new ArrayNode instance.
     *
     * Unlike {@link #filter(ArrayNode, Predicate)} this method
     * filters out values rather than filtering them in.
     *
     * @param node the ArrayNode to filter through.
     * @param predicate the condition used to filter values.
     * @return an ArrayNode instance containing filtered values.
     */
    public static ArrayNode reject(ArrayNode node, Predicate<JsonNode> predicate) {
        return filter(node, e -> !predicate.test(e));
    }

    /**
     * Filters values in an ObjectNode into a new ObjectNode.
     *
     * This does not modify the input ObjectNode but collects
     * the filtered values into a new ObjectNode instance.
     *
     * Unlike {@link #filter(ObjectNode, Predicate)} this method
     * filters out values rather than filtering them in.
     *
     * @param node the ObjectNode to filter through.
     * @param predicate the condition used to filter values.
     * @return an ObjectNode instance containing filtered values.
     */
    public static ObjectNode reject(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return filter(node, e -> !predicate.test(e));
    }

    /**
     * Returns true if some values in the ArrayNode match the
     * provided predicate.
     *
     * @param node the ArrayNode to verify.
     * @param predicate the condition to match against.
     * @return true if some values match the condition.
     */
    public static boolean some(ArrayNode node, Predicate<JsonNode> predicate) {
        return stream(node).anyMatch(predicate);
    }

    /**
     * Returns true if some entries in the ObjectNode match the
     * provided predicate.
     *
     * @param node the ObjectNode to verify.
     * @param predicate the condition to match against.
     * @return true if some entries match the condition.
     */
    public static boolean some(ObjectNode node, Predicate<Map.Entry<String, JsonNode>> predicate) {
        return stream(node).anyMatch(predicate);
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
     * Returns a new ArrayNode containing N taken items from a provided
     * ArrayNode instance.
     *
     * This does not modify the original ArrayNode input, but
     * returns a new ArrayNode instance with the nodes taken.
     *
     * @param node the node to take from.
     * @param count the number of nodes to take.
     * @return a new ArrayNode instance fo the taken nodes.
     */
    public static ArrayNode take(ArrayNode node, int count) {
        return transform(node, s -> s.limit(count));
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
     * Returns an ArrayNode instance with all duplicate values
     * removed.
     *
     * @param node the input ArrayNode to uniq.
     * @return a new ArrayNode of unique instance.
     */
    public static ArrayNode uniq(ArrayNode node) {
        return transform(node, Stream::distinct);
    }

    /**
     * Returns an ArrayNode instance containing all values of
     * the provided ObjectNode.
     *
     * Note that the returned values are in no guaranteed order.
     *
     * @param node the ObjectNode to retrieve values for.
     * @return an ArrayNode containing all JsonNode values.
     */
    public static ArrayNode values(ObjectNode node) {
        return arrayCollect(stream(node).map(Map.Entry::getValue));
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

    /**
     * Safely converts a value to a JsonNode instance.
     *
     * If the value is null, a NullNode will be returned.
     *
     * @param value the value to convert.
     * @param mapper the mapping Function to convert with.
     * @param <T> the type of the value to convert.
     * @return a new JsonNode instance.
     */
    private static <T> JsonNode safeNode(T value, Function<T, JsonNode> mapper) {
        return value == null ? NullNode.getInstance() : mapper.apply(value);
    }

    /**
     * Internal BinaryOperator implementation for reduction methods.
     *
     * @param <T> the type of the values.
     */
    static class RightOperator<T> implements BinaryOperator<T> {

        /**
         * Overwrites the left value with the right value.
         *
         * @param left the left value (ignored).
         * @param right the right value (returned).
         * @return the value on the right side.
         */
        @Override
        public T apply(T left, T right) {
            return right;
        }
    }

}
