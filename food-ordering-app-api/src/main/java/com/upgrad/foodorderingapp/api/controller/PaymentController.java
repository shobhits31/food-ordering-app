package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.PaymentListResponse;
import com.upgrad.foodorderingapp.api.model.PaymentResponse;
import com.upgrad.foodorderingapp.service.businness.PaymentService;
import com.upgrad.foodorderingapp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * List all available payment methods
     *
     * @return payment methods
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<PaymentListResponse> paymentDetails() {
        List<PaymentEntity> paymentEntityList = paymentService.getAllPaymentMethods();

        PaymentListResponse response = new PaymentListResponse();
        for(PaymentEntity paymentEntity: paymentEntityList) {
            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setId(UUID.fromString(paymentEntity.getUuid()));
            paymentResponse.setPaymentName(paymentEntity.getPaymentName());
            response.addPaymentMethodsItem(paymentResponse);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
