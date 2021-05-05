package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.common.Constants;
import com.upgrad.foodorderingapp.service.dao.CustomerDao;
import com.upgrad.foodorderingapp.service.entity.CustomerAuthEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.exception.AuthenticationFailedException;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.SignUpRestrictedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        setEncryptedPassword(customerEntity);
        CustomerEntity customer = customerDao.createUser(customerEntity);
        log.debug("****** Ending signup ******");
        return customer;

    }


    /**
     * Method to validate if a particular field is null or empty
     *
     * @param value
     * @return
     */
    public boolean isEmptyField(final String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Method to authenticate customer and generate auth-token
     *
     * @param contactNumber
     * @param password
     * @return - CustomerAuthEntity
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(String contactNumber,String password) throws AuthenticationFailedException {
        log.debug("****** Starting authenticate ******");

        CustomerEntity customerEntity = customerDao.getCustomerByContactNo(contactNumber);
        if (customerEntity == null) {
            log.info("This contact number {} is not registered",contactNumber);
            throw new AuthenticationFailedException(ATH_001.getCode(), ATH_001.getDefaultMessage());
        }
        CustomerAuthEntity customerAuthEntity=null;
        final String encryptedPassword = cryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (!encryptedPassword.equals(customerEntity.getPassword())) {
            log.info("Invalid password for contact number: {}",contactNumber);
            throw new AuthenticationFailedException(ATH_002.getCode(), ATH_002.getDefaultMessage());
        }
        log.info("Password validation successful for contactNumber: {}",contactNumber);
        customerAuthEntity = createUserAuthToken(customerEntity, encryptedPassword);
        log.debug("****** Ends authenticate method******");

        return customerAuthEntity;
    }
    /**
     * Business logic to logout an already signed in customer
     *
     * @param accessToken
     * @return
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        String[] bearerToken;
        try {
            bearerToken = accessToken.split(Constants.TOKEN_PREFIX);
            if (bearerToken.length != 2) {
                log.info("Invalid authorization token");
                throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
            }
        } catch (AuthorizationFailedException e) {
            log.error("Exception parsing token");
            throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
        }
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthEntity(bearerToken[1]);
        validateCustomerAuthToken(customerAuthEntity);
        customerAuthEntity.setLogoutAt(ZonedDateTime.now());
        customerDao.updateCustomerAuth(customerAuthEntity);

        return customerAuthEntity;
    }

    /**
     * method to validate customer data for sign-up request
     *
     * @param customerEntity
     * @throws SignUpRestrictedException
     */
    private void validateCustomerDetails(final CustomerEntity customerEntity) throws
            SignUpRestrictedException {

        // Throw exception if the contact number is not valid
        if (customerEntity.getContactNumber().length() > 10
                || customerEntity.getContactNumber().length() < 10
                || !StringUtils.isNumeric(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException(SGUR_003.getCode(), SGUR_003.getDefaultMessage());
        }

        // Throw exception if customer contact number is already present in the database
        CustomerEntity custEntityByPhnNum = customerDao.getCustomerByContactNo(customerEntity.getContactNumber());
        if (custEntityByPhnNum != null) {
            throw new SignUpRestrictedException(SGUR_001.getCode(), SGUR_001.getDefaultMessage());
        }

        // Throw exception if email Id pattern is not valid
        boolean isValidEmail = isValidPattern(Constants.EMAIL_PATTERN, customerEntity.getEmailAddress());
        if (!isValidEmail) {
            throw new SignUpRestrictedException(SGUR_002.getCode(), SGUR_002.getDefaultMessage());
        }

        // Throw exception if password is weak
        if (!isValidPattern(Constants.PASSWORD_PATTERN, customerEntity.getPassword())) {
            throw new SignUpRestrictedException(SGUR_004.getCode(), SGUR_004.getDefaultMessage());
        }

        if (isEmptyField(customerEntity.getContactNumber())|| isEmptyField(customerEntity.getEmailAddress())
                || isEmptyField(customerEntity.getFirstName()) || isEmptyField(customerEntity.getPassword())) {
            throw new SignUpRestrictedException(SGUR_005.getCode(), SGUR_005.getDefaultMessage());
        }

    }


    /**
     * Method to set encrypted password
     *
     * @param customerEntity
     */
    private void setEncryptedPassword(final CustomerEntity customerEntity) {
        String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
    }

    /**
     * Method to match a field with a requested pattern
     *
     * @param pattern
     * @param field
     * @return
     */
    private boolean isValidPattern(final String pattern, final String field) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(field);
        return matcher.matches();
    }

    /**
     * to create an customer auth-token
     *
     * @param customerEntity
     * @param secret
     * @return
     */
    private CustomerAuthEntity createUserAuthToken(CustomerEntity customerEntity, String secret) {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(secret);
        CustomerAuthEntity customerAuth = new CustomerAuthEntity();
        customerAuth.setCustomer(customerEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(Constants.EXPIRATION_TIME);
        customerAuth.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
        customerAuth.setLoginAt(now);
        customerAuth.setExpiresAt(expiresAt);
        customerAuth.setUuid(customerEntity.getUuid());
        customerDao.createAuthToken(customerAuth);
        log.debug("auth-token successfully created for contact number {}", customerEntity.getContactNumber());
        return customerAuth;
    }

    /**
     * To fetch user auth-token details
     *
     * @param customerAuthEntity
     * @return
     * @throws AuthorizationFailedException
     */
    private CustomerAuthEntity validateCustomerAuthToken(final CustomerAuthEntity customerAuthEntity) throws
            AuthorizationFailedException {
        // Throw exception if the customer is not logged in
        if (customerAuthEntity == null) {
            log.info("authorization token not found in db");
            throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
        }
        //Throw exception if the customer is already logged out
        if (customerAuthEntity.getLogoutAt() != null) {
            log.info("Customer is already signed out at: {}", customerAuthEntity.getLogoutAt());
            throw new AuthorizationFailedException(ATHR_002.getCode(), ATHR_002.getCode());
        }
        // Throw exception is the customer session has already expired
        if (customerAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException(ATHR_003.getCode(),ATHR_003.getDefaultMessage());
        }
        return customerAuthEntity;
    }

}
