package com.revolut.sinap.api.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.UUID;

public class JsonTest {
    private static ObjectMapper mapper;

    @BeforeClass
    public static void beforeClass() {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Test
    public void testParsePaymentRequest() throws JsonProcessingException {
        String s = mapper.writeValueAsString(new PaymentRequest()
                .setTransactionId(UUID.randomUUID().toString())
                .setSource(new PaymentRequest.Account()
                        .setId(1234123412341234L)
                        .setCurrency("USD")
                        .setAmount("12.34"))
                .setTarget(new PaymentRequest.Account()
                        .setId(4321432143214321L)
                        .setCurrency("RUR")
                        .setAmount("432.10"))
                .setComment("test transfer")
        );
        System.out.println(s);
    }
}
