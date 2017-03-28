package com.zackehh.jackson.stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;

public class JiveCollectorsTest {

    @Test
    public void testCreate() throws Exception {
        Constructor c = JiveCollectors.class.getDeclaredConstructor();
        c.setAccessible(true);
        c.newInstance();
    }

    @Test
    public void testToArrayNode() {
        Assert.assertNotNull(JiveCollectors.toArrayNode());
    }

    @Test
    public void testToObjectNode() {
        Assert.assertNotNull(JiveCollectors.toObjectNode());
    }

}
