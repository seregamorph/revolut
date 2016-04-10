package com.revolut.sinap.server;

import com.revolut.sinap.controller.PaymentController;
import com.revolut.sinap.controller.PaymentStatusController;
import com.revolut.sinap.netty.DispatchHttpHandlerBuilder;
import com.revolut.sinap.netty.HttpHandler;
import com.revolut.sinap.netty.HttpServerBuilder;
import com.revolut.sinap.netty.NettyServer;
import com.revolut.sinap.payment.Currency;
import com.revolut.sinap.payment.DummyAccountStorage;
import com.revolut.sinap.payment.DummyPaymentService;
import com.revolut.sinap.payment.PaymentService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<Long, DummyAccountStorage.Account> accounts = createDefaultAccounts();

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

    static Map<Long, DummyAccountStorage.Account> createDefaultAccounts() {
        Map<Long, DummyAccountStorage.Account> accounts = Collections.synchronizedMap(new HashMap<>());
        for (int i = 0; i < 100; i++) {
            long accountId = i + 1;
            DummyAccountStorage.Account account = new DummyAccountStorage.Account(accountId, Currency.RUB, 0, 10000);
            accounts.put(accountId, account);
        }
        return accounts;
    }
}
