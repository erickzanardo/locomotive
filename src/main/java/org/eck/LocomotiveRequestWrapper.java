package org.eck;

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

public class LocomotiveRequestWrapper {

    private HttpRequest request;
    private Map<String, Param> params = new HashMap<String, Param>();
    private String body;

    public LocomotiveRequestWrapper(HttpRequest request) {
        this.request = request;
        decodeQueryString();
        if (request.getMethod().equals(HttpMethod.POST)) {
            decodePost();
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

    class Param {
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
}
