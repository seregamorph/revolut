package com.revolut.sinap.controller;

import com.revolut.sinap.api.ResponseCode;
import com.revolut.sinap.api.json.PaymentStatusRequest;
import com.revolut.sinap.api.json.PaymentStatusResponse;
import com.revolut.sinap.payment.PaymentService;
import com.revolut.sinap.payment.domain.PaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentStatusController extends AbstractJsonController<PaymentStatusRequest, PaymentStatusResponse> {
    private static final Logger logger = LoggerFactory.getLogger(PaymentStatusController.class);

    private final PaymentService paymentService;

    public PaymentStatusController(PaymentService paymentService) {
        super(PaymentStatusRequest.class);
        this.paymentService = paymentService;
    }

    @Override
    protected PaymentStatusResponse process(PaymentStatusRequest req) {
        try {
            return doProcess(req);
        } catch (PaymentException e) {
            logger.error("Error while processing payment request " + req, e);
            return new PaymentStatusResponse()
                    .setTransactionId(req.getTransactionId())
                    .setResponseCode(e.responseCode());
        }
    }

    private PaymentStatusResponse doProcess(PaymentStatusRequest req) throws PaymentException {
        ResponseCode responseCode = paymentService.processPaymentStatus(req.getTransactionId());

        return new PaymentStatusResponse()
                .setTransactionId(req.getTransactionId())
                .setResponseCode(responseCode);
    }
}
