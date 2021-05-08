package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.dao.PaymentDao;
import com.upgrad.foodorderingapp.service.entity.PaymentEntity;
import com.upgrad.foodorderingapp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@Service
public class PaymentService {

    @Autowired
    private PaymentDao paymentDao;

    /**
     * Retrieve payment methods
     *
     * @return payment methods
     */
    public List<PaymentEntity> getAllPaymentMethods() {
        return paymentDao.getAllPaymentMethods();
    }

    /**
     * Retrieves the Payment details
     *
     * @param paymentUUID The uuid of the Payment to lookup
     * @return payment details
     * @throws PaymentMethodNotFoundException No payment method found by this id
     */
    public PaymentEntity getPaymentByUUID(String paymentUUID) throws PaymentMethodNotFoundException {
        PaymentEntity payment = paymentDao.getPaymentByUUID(paymentUUID);
        if (payment == null) {
            throw new PaymentMethodNotFoundException(PNF_002.getCode(), PNF_002.getDefaultMessage());
        }
        return payment;
    }
}
