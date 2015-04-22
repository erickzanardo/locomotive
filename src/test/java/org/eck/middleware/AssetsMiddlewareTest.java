package org.eck.middleware;


import org.eck.AbstractLocomotiveTest;
import org.eck.Locomotive;
import org.eck.middlewares.LocomotiveAssetsMiddleware;
import org.junit.Assert;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class AssetsMiddlewareTest extends AbstractLocomotiveTest {

    @Override
    protected void beforeBoot(Locomotive locomotive) {
        locomotive.addMiddleware(new LocomotiveAssetsMiddleware("public"));
    }

    @Test
    public void testGetAssets() throws UnirestException {
        HttpResponse<String> response = Unirest.get(url("/bla.html")).asString();
        Assert.assertEquals("<div>bla</div>", response.getBody());
    }
}
