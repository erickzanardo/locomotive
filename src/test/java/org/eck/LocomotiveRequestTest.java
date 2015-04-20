package org.eck;

import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class LocomotiveRequestTest extends AbstractLocomotiveTest {

    @Test
    public void testRequestQueryString() throws UnirestException {
        locomotive.get("/calc", (req, resp) -> {
            int sum = req.param("number1").asInteger()
                    + req.param("number2").asInteger();
            resp.append(String.valueOf(sum));
        });

        HttpResponse<String> response = Unirest.get(url("/calc"))
                .queryString("number1", 1).queryString("number2", 2).asString();
        Assert.assertEquals("3", response.getBody());
    }

    @Test
    public void testRequestFormData() throws UnirestException {
        locomotive.post("/calc", (req, resp) -> {
            int sum = req.param("number1").asInteger()
                    + req.param("number2").asInteger();
            resp.append(String.valueOf(sum));
        });

        HttpResponse<String> response = Unirest.post(url("/calc"))
                .field("number1", 1).field("number2", 2).asString();
        Assert.assertEquals("3", response.getBody());
    }

    @Test
    public void testRequestBody() throws UnirestException {
        locomotive.post(
                "/calc",
                (req, resp) -> {
                    String[] values = req.body().split(",");
                    int sum = Integer.parseInt(values[0])
                            + Integer.parseInt(values[1]);
                    resp.append(String.valueOf(sum));
                });

        HttpResponse<String> response = Unirest.post(url("/calc")).body("1,2")
                .asString();
        Assert.assertEquals("3", response.getBody());
    }

    @Test
    public void testRequestRouteParams() throws UnirestException {
        locomotive.post("/calc/sum/:number1/and/:number2", (req, resp) -> {
            int sum = req.param("number1").asInteger()
                    + req.param("number2").asInteger();
            resp.append(String.valueOf(sum));
        });

        HttpResponse<String> response = Unirest.post(url("/calc/sum/{number1}/and/{number2}"))
                .routeParam("number1", "1").routeParam("number2", "2").asString();
        Assert.assertEquals("3", response.getBody());
    }
}
