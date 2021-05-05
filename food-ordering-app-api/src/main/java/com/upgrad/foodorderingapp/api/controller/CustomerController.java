package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.common.Constants;
import com.upgrad.foodorderingapp.service.entity.CustomerAuthEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.exception.AuthenticationFailedException;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.SignUpRestrictedException;
import com.upgrad.foodorderingapp.service.exception.UpdateCustomerException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomerService customerService;

    /** To create an customer based on sign-up request details
     * @param signupCustomerRequest
     * @return
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> customerSignup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        final CustomerEntity customerEntity = convertToUserEntity(signupCustomerRequest);
        final CustomerEntity createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status(Constants.CUSTOMER_REGISTRATION_MESSAGE);
        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
    }


    /** To sign-in a customer based on authentication
     * @param authorization
     * @return
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        String contactNumber="";
        String password="";
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split(Constants.HEADER_STRING)[1]);
            final String decodedText = new String(decode);
            final String[] decodedArray = decodedText.split(":");
            contactNumber = decodedArray[0];
            password = decodedArray[1];
        } catch (Exception e) {
            throw new AuthenticationFailedException(ATH_003.getCode(), ATH_003.getDefaultMessage());

        }

        CustomerAuthEntity authEntity = customerService.authenticate(contactNumber,password);
        CustomerEntity customer = authEntity.getCustomer();
        LoginResponse loginResponse = new LoginResponse().id(authEntity.getUuid()).message(Constants.LOGIN_MESSAGE)
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .emailAddress(customer.getEmailAddress())
                .contactNumber(customer.getContactNumber());

        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", authEntity.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);

    }

    /** To logout a already signed in customer
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> signout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final CustomerAuthEntity customerAuthEntity = customerService.logout(authorization);
        final CustomerEntity customerEntity = customerAuthEntity.getCustomer();

        LogoutResponse logoutResponse = new LogoutResponse().id(customerEntity.getUuid()).message(Constants.LOGOUT_MESSAGE);
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }


    /**
     * Update customer details if valid authorization and all mandatory fields are provided
     *
     * @param authorization
     * @param updateCustomerRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(@RequestHeader("authorization") final String authorization,
                                                                        @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest)
            throws AuthorizationFailedException, UpdateCustomerException {

        // Throw exception if first name is not present
        if (CustomerService.isEmptyField(updateCustomerRequest.getFirstName())) {
            throw new UpdateCustomerException(UCR_002.getCode(), UCR_002.getDefaultMessage());
        }

        CustomerEntity customerEntity = customerService.getCustomer(authorization);
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());
        CustomerEntity updatedCustomerEntity = customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .id(updatedCustomerEntity.getUuid())
                .status(Constants.UPDATE_CUSTOMER_MESSAGE)
                .firstName(updatedCustomerEntity.getFirstName())
                .lastName(updatedCustomerEntity.getLastName());

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    /** map the request object to customer entity
     * @param signupUserRequest
     * @return
     */
    private CustomerEntity convertToUserEntity(final SignupCustomerRequest signupUserRequest) {
        CustomerEntity customerEntity = modelMapper.map(signupUserRequest, CustomerEntity.class);
        customerEntity.setUuid(UUID.randomUUID().toString());
        return customerEntity;

    }

    /**
     * Update password if valid authorization token and all mandatory fields are provided
     *
     * @param authorization
     * @param updatePasswordRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws UpdateCustomerException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/password",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updateCustomerPassword(@RequestHeader("authorization") final String authorization,
                                                                         @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest)
            throws AuthorizationFailedException, UpdateCustomerException {

        final String oldPass = updatePasswordRequest.getOldPassword();
        final String newPass = updatePasswordRequest.getNewPassword();

        // Throw exception if the old or new password is not provided
        if (customerService.isEmptyField(oldPass)
                || customerService.isEmptyField(newPass)) {
            throw new UpdateCustomerException(UCR_003.getCode(), UCR_003.getDefaultMessage());
        }

        CustomerEntity customerEntity = customerService.getCustomer(authorization);
        CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(
                oldPass, newPass, customerEntity);

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
                .id(updatedCustomerEntity.getUuid())
                .status(Constants.UPDATE_PASSWORD_MESSAGE);

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}
