package org.eck;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class LocomotiveResponseTest {
    private Locomotive locomotive;

    @Before
    public void tearUp() {
        locomotive = new Locomotive(8080);
        locomotive.boot();
    }
    
    @After
    public void tearDown() {
        locomotive.shutdown();
    }

    @Test
    public void testResponseWrite() throws UnirestException {
        locomotive.get("/hello", (req, resp) -> resp.append("World"));
        HttpResponse<String> httpResponse = Unirest.get(url("/hello")).asString();
        Assert.assertEquals("World", httpResponse.getBody());
    }

    @Test
    public void testResponseStatus() throws UnirestException {
        locomotive.get("/hello", (req, resp) -> {
            resp.status(403);
            resp.append("You shall not pass");
        });
        HttpResponse<String> httpResponse = Unirest.get(url("/hello")).asString();
        Assert.assertEquals(403, httpResponse.getStatus());
        Assert.assertEquals("You shall not pass", httpResponse.getBody());
    }

    public String url(String url) {
        return "http://localhost:8080" + url;
    }
}
