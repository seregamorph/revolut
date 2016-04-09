package com.revolut.sinap.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revolut.sinap.netty.HttpUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.IOException;
import java.util.Objects;

public abstract class AbstractJsonController<RQ, RS> extends AbstractController<RQ, RS> {
    private final Class<RQ> requestClass;
    private final ObjectMapper mapper;

    protected AbstractJsonController(Class<RQ> requestClass) {
        this.requestClass = Objects.requireNonNull(requestClass, "requestClass");
        this.mapper = initMapper();
    }

    protected ObjectMapper initMapper() {
        return new ObjectMapper();
    }

    @Override
    protected RQ parseRequest(FullHttpRequest request) {
        // todo check "Content-Type" header
        byte[] requestBody = HttpUtils.getBody(request);
        if (requestBody == null || requestBody.length == 0) {
            // error 400
            throw new IllegalArgumentException("Empty body not allowed");
        }
        try {
            return mapper.readValue(requestBody, requestClass);
        } catch (IOException e) {
            throw new IllegalArgumentException("Bad request", e);
        }
    }

    @Override
    protected FullHttpResponse formatResponse(RS resp) {
        byte[] responseBody;
        try {
            responseBody = mapper.writeValueAsBytes(resp);
        } catch (JsonProcessingException e) {
            // error 500
            throw new RuntimeException("Error while processing json", e);
        }
        return HttpUtils.createJsonHttpResponse(HttpResponseStatus.OK, responseBody);
    }
}
