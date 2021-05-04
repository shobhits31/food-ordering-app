package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.dao.CustomerDao;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.exception.SignUpRestrictedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@Service
public class CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * Business logic to create an user based on sign-up request details
     *
     * @param customerEntity
     * @return
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(final CustomerEntity customerEntity) throws SignUpRestrictedException {
        log.debug("****** Starting signup ******");
        validateCustomerDetails(customerEntity);
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        CustomerEntity customer = customerDao.createUser(customerEntity);
        log.debug("****** Ending signup ******");
        return customer;

    }


    /**
     * method to validate customer data for sign-up request
     *
     * @param customer
     * @throws SignUpRestrictedException
     */
    private void validateCustomerDetails(final CustomerEntity customer) throws
            SignUpRestrictedException {
        /*CustomerEntity customerEntity = customerDao.getCustomerByContact(customer.getContactNumber());
        if (customerEntity != null) {
            log.info("This contact number {} is already registered", customer.getContactNumber());
            throw new SignUpRestrictedException(SGUR_001.getCode(), SGUR_001.getDefaultMessage());
        }

         customerEntity = customerDao.getUserByEmail(customer.getEmailAddress());
        if (customerEntity != null) {
            log.info("This email {} is already registered", customer.getEmailAddress());
            throw new SignUpRestrictedException(SGUR_003.getCode(), SGUR_003.getDefaultMessage());
        }*/
        if (customer.getContactNumber() == null || customer.getEmailAddress() == null
                || customer.getFirstName()==null || customer.getPassword()==null) {
            throw new SignUpRestrictedException(SGUR_005.getCode(), SGUR_005.getDefaultMessage());
        }

    }
}
