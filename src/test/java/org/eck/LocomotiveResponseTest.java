package org.eck;

import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class LocomotiveResponseTest extends AbstractLocomotiveTest {

    @Test
    public void testResponseWrite() throws UnirestException {
        locomotive.get("/hello", (req, resp) -> resp.send("World"));
        HttpResponse<String> httpResponse = Unirest.get(url("/hello")).asString();
        Assert.assertEquals("World", httpResponse.getBody());
    }

    @Test
    public void testResponseStatus() throws UnirestException {
        locomotive.get("/hello", (req, resp) -> {
            resp.status(403);
            resp.send("You shall not pass");
        });
        HttpResponse<String> httpResponse = Unirest.get(url("/hello")).asString();
        Assert.assertEquals(403, httpResponse.getStatus());
        Assert.assertEquals("You shall not pass", httpResponse.getBody());
    }

    @Test
    public void testResponseContentType() throws UnirestException {
        locomotive.get("/hello", (req, resp) -> {
            resp.contentType("application/json");
            resp.send("{ \"name\": \"James\" }");
        });
        HttpResponse<JsonNode> httpResponse = Unirest.get(url("/hello")).asJson();
        Assert.assertEquals("application/json", httpResponse.getHeaders().get("content-type").get(0));
        Assert.assertEquals("James", httpResponse.getBody().getObject().getString("name"));
    }
}
