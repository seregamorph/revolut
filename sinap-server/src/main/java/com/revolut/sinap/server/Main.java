package com.revolut.sinap.server;

import com.revolut.sinap.controller.PaymentController;
import com.revolut.sinap.controller.PaymentStatusController;
import com.revolut.sinap.netty.DispatchHttpHandlerBuilder;
import com.revolut.sinap.netty.HttpHandler;
import com.revolut.sinap.netty.HttpServerBuilder;
import com.revolut.sinap.netty.NettyServer;
import com.revolut.sinap.payment.DummyAccountStorage;
import com.revolut.sinap.payment.DummyPaymentService;
import com.revolut.sinap.payment.PaymentService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {
    public static void main(String[] args) {
        ConcurrentMap<Long, DummyAccountStorage.Account> accounts = createDefaultAccounts();

        DummyAccountStorage accountStorage = new DummyAccountStorage(accounts);

        PaymentService paymentService = new DummyPaymentService(accountStorage);

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

    static ConcurrentHashMap<Long, DummyAccountStorage.Account> createDefaultAccounts() {
        return new ConcurrentHashMap<>();
    }
}
