package com.revolut.sinap.controller;

import com.revolut.sinap.api.json.PaymentRequest;
import com.revolut.sinap.api.json.PaymentResponse;
import com.revolut.sinap.payment.PaymentService;
import com.revolut.sinap.payment.domain.Payment;

public class PaymentController<RQ, RS> extends AbstractJsonController<PaymentRequest, PaymentResponse> {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        super(PaymentRequest.class);
        this.paymentService = paymentService;
    }

    @Override
    protected PaymentResponse process(PaymentRequest req) {
        Payment payment = new Payment(req.getTransactionId())
                .setSourceAccountId(req.getSource().getId())

        return null;
    }
}
