package org.eck;

import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class LocomotiveRequestResourceTest extends AbstractLocomotiveTest {

    @Override
    protected void beforeBoot(Locomotive locomotive) {
        locomotive.addMiddleware((req, resp) -> {
            req.resource("new-number", req.param("number").asInteger());
        });
    }

    @Test
    public void testRequestResources() throws UnirestException {
        locomotive.post("/calc/:number", (req, resp) -> {
            resp.send(req.resource("new-number").toString());
        });

        HttpResponse<String> response = Unirest.post(url("/calc/2")).asString();
        Assert.assertEquals("2", response.getBody());
    }
}
