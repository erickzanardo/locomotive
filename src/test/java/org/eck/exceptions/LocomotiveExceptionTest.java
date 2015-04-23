package org.eck.exceptions;

import org.eck.AbstractLocomotiveTest;
import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class LocomotiveExceptionTest extends AbstractLocomotiveTest {

    @Test
    public void testExceptionThrowByWagon() throws UnirestException {
        locomotive.post("/user", (req, resp) -> {
            throw new RuntimeException("Not implemented");
        });
        HttpResponse<String> response = Unirest.post(url("/user")).asString();
        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("Not implemented", response.getBody());
    }

    @Test
    public void testExceptionThrowByWagonLocomotiveException()
            throws UnirestException {
        class UnauthorizeException extends LocomotiveException {
            private static final long serialVersionUID = 1821368040400403033L;

            public UnauthorizeException(String message) {
                super(message);
            }

            @Override
            public int code() {
                return 401;
            }

        }

        locomotive.post("/user", (req, resp) -> {
            throw new UnauthorizeException("You shall not pass");
        });
        HttpResponse<String> response = Unirest.post(url("/user")).asString();
        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals("You shall not pass", response.getBody());
    }

    @Test
    public void testExceptionThrowByWagonGenericLocomotiveException()
            throws UnirestException {
        locomotive.post("/user", (req, resp) -> {
            throw new GenericLocomotiveException(401, "You shall not pass");
        });
        HttpResponse<String> response = Unirest.post(url("/user")).asString();
        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals("You shall not pass", response.getBody());
    }
}
