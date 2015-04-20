package org.eck.path;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class RouteParserTest {
    @Test
    public void testMatchesSimplePattern() {
        String pattern = "/users/:userId";
        Assert.assertTrue(RouteParser.macthes(pattern, "/users/1"));
        Assert.assertFalse(RouteParser.macthes(pattern, "/users"));
        Assert.assertFalse(RouteParser.macthes(pattern, "/users/3/card/3"));
        Assert.assertFalse(RouteParser.macthes(pattern, "/users/3/foo"));
    }

    @Test
    public void testMatchesComplexPattern() {
        String pattern = "/users/:userId/card/:cardId";
        Assert.assertTrue(RouteParser.macthes(pattern, "/users/1/card/3"));
        Assert.assertFalse(RouteParser.macthes(pattern, "/users/card/3"));
    }

    @Test
    public void testParse() {
        String pattern = "/users/:userId/card/:cardId";
        Map<String, String> params = RouteParser.parse(pattern, "/users/1/card/3");
        Assert.assertEquals("1", params.get("userId"));
        Assert.assertEquals("3", params.get("cardId"));
    }
}
