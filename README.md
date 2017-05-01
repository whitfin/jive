# Jive

[![Build Status](https://travis-ci.org/zackehh/jive.svg?branch=master)](https://travis-ci.org/zackehh/jive) [![Coverage Status](https://coveralls.io/repos/zackehh/jive/badge.svg?branch=master&service=github)](https://coveralls.io/github/zackehh/jive?branch=master)

J{ackson F}ive is a grace library for working with Jackson JSON collections in Java 8 through familiar interfaces. It's great for typical Collection-based abstractions such as filter/map & reduce, whilst also providing simple utilities for node construction and verification.

The main bulk of what's offered here is just a collection of methods which would usually be self-rolled over and over inside a codebase, but in a single place, and from a single source. Most funcitons internally are rolled re-using other internal methods, making it a small library that's easy to use and test.

Jive also offers stream utilities for `ArrayNode` and `ObjectNode` instances, allowing you to operate on your JSON objects using the powerful `Stream` interfaces which come along with Java 8. As such, Java 8 is required to use Jive (if you're not using J8, and you could do, you should). 

### Setup

`jive` is available on Maven central, via Sonatype OSS:

```
<dependency>
    <groupId>com.zackehh</groupId>
    <artifactId>jive</artifactId>
    <version>1.1.1</version>
</dependency>
```

### Usage

The API is pretty simple, and you can visit the Javadocs to see what's available (or feel free to reach out).

Here are a couple of examples bundled into a `main()` for you to play about with to get started:

```java
package com.zackehh.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class JiveExample {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        // initialize a new ArrayNode
        ArrayNode arr1 = Jive.newArrayNode(
                Jive.newJsonNode(1),
                Jive.newJsonNode(2),
                Jive.newJsonNode(3)
        );

        // [ 1, 2, 3 ]
        print(arr1);

        // filter out all odd numbers in an ArrayNode
        ArrayNode arr2 = Jive.filter(arr1, jsonNode -> jsonNode.asInt() % 2 == 0);

        // [ 2 ]
        print(arr2);

        // double all values in an ArrayNode
        ArrayNode arr3 = Jive.map(arr1, jsonNode -> Jive.newJsonNode(jsonNode.asInt() * 2));

        // [ 2, 4, 6 ]
        print(arr3);

        // reduce an ArrayNode into a single value
        Integer val1 = Jive.reduce(arr1, 0, (i, jsonNode) -> i + jsonNode.asInt());

        // 6
        print(val1);

        // initialize a new ObjectNode
        ObjectNode obj1 = Jive.newObjectNode(
                Jive.newJsonEntry("key1", 1),
                Jive.newJsonEntry("key2", 2),
                Jive.newJsonEntry("key3", 3)
        );

        // { "key1": 1, "key2": 2, "key3": 3 }
        print(obj1);

        // pick our values from an ObjectNode
        ObjectNode obj2 = Jive.pick(obj1, "key2");

        // { "key2": 2 }
        print(obj2);

        // grab the keys from an ObjectNode
        Set<String> val2 = Jive.keys(obj1);

        // [ "key1", "key2", "key3" ]
        print(val2);

        // initialize raw Stream instances
        Stream<JsonNode> stream1 = Jive.stream(arr1);
        Stream<Map.Entry<String, JsonNode>> stream2 = Jive.stream(obj1);

        // 3 & 3
        print(stream1.count());
        print(stream2.count());
    }

    private static void print(JsonNode value) {
        Optional<String> stringOptional = Jive
                .execute(mapper, mapper -> mapper.writeValueAsString(value));
        print(stringOptional.orElseThrow(IllegalArgumentException::new));
    }

    private static void print(Object o) {
        System.out.println(o);
    }

}
```
