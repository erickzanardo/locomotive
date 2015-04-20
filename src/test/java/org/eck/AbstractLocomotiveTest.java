package org.eck;

import org.junit.After;
import org.junit.Before;

public abstract class AbstractLocomotiveTest {
    protected Locomotive locomotive;

    @Before
    public void tearUp() {
        locomotive = new Locomotive(8080);
        locomotive.boot();
    }

    @After
    public void tearDown() {
        locomotive.shutdown();
    }

    protected String url(String url) {
        return "http://localhost:8080" + url;
    }
}
