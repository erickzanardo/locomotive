package org.eck;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MixedAttribute;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eck.path.RouteParser;

public class LocomotiveRequestWrapper {

    private HttpRequest request;
    private Map<String, Param> params = new HashMap<String, Param>();
    private Map<String, Object> resources = new HashMap<String, Object>();
    private String body;
    private String uri;
    private String pattern;
    private boolean processed;

    public LocomotiveRequestWrapper(HttpRequest request, String uri,
            String pattern) {
        this.request = request;
        this.uri = uri;
        this.pattern = pattern;
        decodeQueryString();
        decodePattern();
        if (request.getMethod().equals(HttpMethod.POST)) {
            decodePost();
        }
    }

    private void decodePattern() {
        if (pattern != null) {
            Map<String, String> params = RouteParser.parse(pattern, uri);
            Set<Entry<String, String>> entrySet = params.entrySet();
            for (Entry<String, String> entry : entrySet) {
                this.params.put(entry.getKey(),
                        new Param(Arrays.asList(entry.getValue())));
            }
        }
    }

    private void decodePost() {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        // Form Data
        List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
        try {
            for (InterfaceHttpData data : bodyHttpDatas) {
                if (data.getHttpDataType().equals(HttpDataType.Attribute)) {
                    Attribute attr = (MixedAttribute) data;
                    params.put(data.getName(),
                            new Param(Arrays.asList(attr.getValue())));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        decoder.destroy();
    }

    public String uri() {
        return this.uri;
    }

    public String pattern() {
        return this.pattern;
    }

    public void processed() {
        this.processed = true;
    }

    public boolean isProcessed() {
        return processed;
    }

    public String method() {
        return this.request.getMethod().name().toUpperCase();
    }

    public Param param(String key) {
        return params.get(key);
    }

    private void decodeQueryString() {
        QueryStringDecoder decoder = new QueryStringDecoder(request.getUri());
        Set<Entry<String, List<String>>> entrySet = decoder.parameters()
                .entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
            params.put(entry.getKey(), new Param(entry.getValue()));
        }
    }

    public class Param {
        private List<String> value;

        public Param(List<String> value) {
            super();
            this.value = value;
        }

        private String first() {
            if (value != null && value.size() > 0) {
                return value.get(0);
            }
            return null;
        }

        public Integer asInteger() {
            if (value != null) {
                return Integer.parseInt(first());
            }
            return null;
        }

        public String asString() {
            if (value != null) {
                return first();
            }
            return null;
        }

        public Long asLong() {
            if (value != null) {
                return Long.parseLong(first());
            }
            return null;
        }

        public Boolean asBoolean() {
            if (value != null) {
                return Boolean.parseBoolean(first());
            }
            return null;
        }

        public Float asFloat() {
            if (value != null) {
                return Float.parseFloat(first());
            }
            return null;
        }

        public Double asDouble() {
            if (value != null) {
                return Double.parseDouble(first());
            }
            return null;
        }
    }

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
    }

    public String header(String headerName) {
        HttpHeaders headers = this.request.headers();
        return headers.get(headerName);
    }

    public void resource(String key, Object value) {
        this.resources.put(key, value);
    }

    public Object resource(String key) {
        return resources.get(key);
    }
}
