package com.revolut.sinap.controller;

import com.revolut.sinap.api.ResponseCode;
import com.revolut.sinap.api.json.PaymentStatusRequest;
import com.revolut.sinap.api.json.PaymentStatusResponse;
import com.revolut.sinap.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PaymentStatusController extends AbstractJsonController<PaymentStatusRequest, PaymentStatusResponse> {
    private static final Logger logger = LoggerFactory.getLogger(PaymentStatusController.class);

    private final PaymentService paymentService;

    public PaymentStatusController(PaymentService paymentService) {
        super(PaymentStatusRequest.class);
        this.paymentService = paymentService;
    }

    @Override
    protected PaymentStatusResponse process(PaymentStatusRequest req) {
        return doProcess(req);
    }

    private PaymentStatusResponse doProcess(PaymentStatusRequest req) {
        UUID transactionId = UUID.fromString(req.getTransactionId());
        ResponseCode responseCode = paymentService.processPaymentStatus(transactionId);

        return new PaymentStatusResponse()
                .setTransactionId(req.getTransactionId())
                .setResponseCode(responseCode);
    }
}
