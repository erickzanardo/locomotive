package org.eck.path;

import java.util.HashMap;
import java.util.Map;

public class RouteParser {
    public static boolean macthes(String pattern, String path) {
        if (pattern != null && path != null) {
            String[] patternTokens = pattern.split("/");
            String[] tokens = path.split("/");

            if (patternTokens.length != tokens.length) {
                return false;
            }

            for (int i = 0; i < patternTokens.length; i++) {
                if (patternTokens[i].startsWith(":")) {
                    continue;
                } else if (!patternTokens[i].equals(tokens[i])) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public static Map<String, String> parse(String pattern, String path) {
        if (pattern != null && path != null) {
            String[] patternTokens = pattern.split("/");
            String[] tokens = path.split("/");

            Map<String, String> params = new HashMap<String, String>();
            for (int i = 0; i < patternTokens.length; i++) {
                String token = patternTokens[i];
                if (token.startsWith(":")) {
                    params.put(patternTokens[i].replace(":", ""), tokens[i]);
                }
            }
            return params;
        }
        return null;
    }
}
