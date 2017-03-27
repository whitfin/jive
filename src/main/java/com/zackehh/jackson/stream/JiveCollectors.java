package com.zackehh.jackson.stream;

import com.zackehh.jackson.stream.collectors.ArrayNodeCollector;
import com.zackehh.jackson.stream.collectors.ObjectNodeCollector;

/**
 * Stream collector bindings to return Collector instances in the
 * same call form as the Collectors in the standard library. Every
 * call in here is just sugar for a direct instantiation of a custom
 * Jive Collector.
 */
public class JiveCollectors {

    /**
     * Private constructor as this class serves only static purposes.
     */
    private JiveCollectors() { }

    /**
     * Returns a new ArrayNode collector.
     *
     * @return a new ArrayNodeCollector instance.
     */
    public static ArrayNodeCollector toArrayNode() {
        return new ArrayNodeCollector();
    }

    /**
     * Returns a new ObjectNode collector.
     *
     * @return a new ObjectNodeCollector instance.
     */
    public static ObjectNodeCollector toObjectNode() {
        return new ObjectNodeCollector();
    }

}
