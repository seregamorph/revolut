package com.revolut.sinap.server;

import com.revolut.sinap.controller.PaymentController;
import com.revolut.sinap.controller.PaymentStatusController;
import com.revolut.sinap.netty.DispatchHttpHandlerBuilder;
import com.revolut.sinap.netty.HttpHandler;
import com.revolut.sinap.netty.HttpServerBuilder;
import com.revolut.sinap.netty.NettyServer;
import com.revolut.sinap.payment.DummyPaymentService;
import com.revolut.sinap.payment.PaymentService;

public class Main {
    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");

        PaymentService paymentService = new DummyPaymentService();

        HttpHandler dispatcher = new DispatchHttpHandlerBuilder()
                .bind("/rest/json/payment", new PaymentController(paymentService))
                .bind("/rest/json/status", new PaymentStatusController(paymentService))
                .build();

        NettyServer server = new HttpServerBuilder("sinap")
                .handler(dispatcher)
                .group(2, 16)
                .localAddress("0.0.0.0", 8080)
                .build();

        server.startSync().channel().closeFuture().syncUninterruptibly();
    }
}
