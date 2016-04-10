package com.revolut.sinap.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.revolut.sinap.api.ResponseCode;
import com.revolut.sinap.api.json.PaymentRequest;
import com.revolut.sinap.api.json.PaymentResponse;
import com.revolut.sinap.controller.PaymentController;
import com.revolut.sinap.controller.PaymentStatusController;
import com.revolut.sinap.netty.DispatchHttpHandlerBuilder;
import com.revolut.sinap.netty.HttpServerBuilder;
import com.revolut.sinap.netty.NettyServer;
import com.revolut.sinap.payment.Currency;
import com.revolut.sinap.payment.DummyAccountStorage;
import com.revolut.sinap.payment.DummyPaymentService;
import com.revolut.sinap.payment.PaymentService;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.testng.Assert.assertEquals;

public class ClientServerTest {
    private static final Logger logger = LoggerFactory.getLogger(ClientServerTest.class);

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    private static final int PORT = 8081;

    private long balance;
    private Map<Long, DummyAccountStorage.Account> accounts;
    private NettyServer server;
    private HttpClient httpClient;

    @BeforeMethod
    public void startServer() {
        this.balance = TRANSACTIONS / ACCOUNTS;

        this.accounts = Collections.synchronizedMap(LongStream.range(1, ACCOUNTS * 2 + 1)
                .mapToObj(accountId -> new DummyAccountStorage.Account(accountId, Currency.RUB, 0,
                        accountId <= ACCOUNTS ? balance : 0))
                .collect(Collectors.toMap(DummyAccountStorage.Account::accountId, e -> e)));

        PaymentService paymentService = new DummyPaymentService(new DummyAccountStorage(accounts));

        this.server = new HttpServerBuilder("sinap")
                .handler(new DispatchHttpHandlerBuilder()
                        .bind("/rest/json/payment", new PaymentController(paymentService))
                        .bind("/rest/json/status", new PaymentStatusController(paymentService))
                        .build())
                .group(2, 16)
                .localAddress("0.0.0.0", PORT)
                .build();

        this.httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(24)
                .setMaxConnPerRoute(24)
                .build();

        this.server.startSync();
    }

    @AfterMethod
    public void shutdownServer() {
        this.server.shutdownSync();
    }

    //    private static final int ACCOUNTS = 100;
//    private static final int TRANSACTIONS = 1000000;
    private static final int ACCOUNTS = 100;
    private static final int TRANSACTIONS = 1000;

    @Test
    public void testMultiThreadClient() {
        List<PaymentRequest> payments = new ArrayList<>(TRANSACTIONS);
        for (int i = 0; i < TRANSACTIONS; i++) {
            long sourceAccountId = i % ACCOUNTS + 1;
            long targetAccountId = ACCOUNTS + i % ACCOUNTS + 1;
            PaymentRequest payment = newPaymentRequest(sourceAccountId, targetAccountId, "0.01", Currency.RUB, "test");
            payments.add(payment);
        }

        // to increase contention
        Collections.shuffle(payments);

        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Future<Integer>> futures = payments.stream()
                    .map(payment -> executor.submit(() -> processPayment(payment)))
                    .collect(Collectors.toList());
            for (Future<Integer> future : futures) {
                try {
                    assertEquals(future.get().intValue(), ResponseCode.SUCCESS.code());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            executor.shutdown();
        }

        for (int i = 0; i < ACCOUNTS; i++) {
            assertEquals(accounts.get(Long.valueOf(i + 1)).getBalance(), 0L);
            assertEquals(accounts.get(Long.valueOf(ACCOUNTS + i + 1)).getBalance(), balance);
        }
    }

    private int processPayment(PaymentRequest payment) throws IOException {
        String uri = "http://127.0.0.1:" + PORT + "/rest/json/payment";
        PaymentResponse resp = doPostJson(uri, payment, PaymentResponse.class);
        assertEquals(resp.getTransactionId(), payment.getTransactionId());
        return resp.getResponseCode();
    }

    private static PaymentRequest newPaymentRequest(long sourceAccountId, long targetAccountId, String amount,
                                                    Currency currency, String comment) {
        return new PaymentRequest()
                .setTransactionId(UUID.randomUUID().toString())
                .setSource(new PaymentRequest.Account()
                        .setId(sourceAccountId)
                        .setAmount(amount)
                        .setCurrency(currency.code()))
                .setTarget(new PaymentRequest.Account()
                        .setId(targetAccountId)
                        .setAmount(amount)
                        .setCurrency(currency.code()))
                .setComment(comment);
    }

    private String doPost(String uri, HttpEntity body, String contentType) throws IOException {
        HttpPost request = new HttpPost(uri);
        request.addHeader("content-type", contentType);

        request.setEntity(body);

        String requestBody = EntityUtils.toString(body, Charsets.UTF_8);
        logger.debug("Sending request POST to " + uri + " with body:\n" + requestBody);

        HttpResponse response = httpClient.execute(request);

        String responseBody = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        logger.debug("Got response code " + response.getStatusLine().getStatusCode() + " with body:\n" + responseBody);

        return responseBody;
    }

    public String doPost(String uri, String body, String contentType) throws IOException {
        StringEntity params = new StringEntity(body, Charsets.UTF_8);
        return doPost(uri, params, contentType);
    }

    public <T> T doPostJson(String uri, Object body, Class<T> resultClass) throws IOException {
        String req = objectMapper.writeValueAsString(body);
        String responseStr = doPost(uri, req, "application/json");
        return objectMapper.readValue(responseStr, resultClass);
    }
}
