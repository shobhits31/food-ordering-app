package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.LoginResponse;
import com.upgrad.foodorderingapp.api.model.LogoutResponse;
import com.upgrad.foodorderingapp.api.model.SignupCustomerRequest;
import com.upgrad.foodorderingapp.api.model.SignupCustomerResponse;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.common.Constants;
import com.upgrad.foodorderingapp.service.entity.CustomerAuthEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.exception.AuthenticationFailedException;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.SignUpRestrictedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

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
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");

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



    /** map the request object to customer entity
     * @param signupUserRequest
     * @return
     */
    private CustomerEntity convertToUserEntity(final SignupCustomerRequest signupUserRequest) {
        CustomerEntity customerEntity = modelMapper.map(signupUserRequest, CustomerEntity.class);
        customerEntity.setUuid(UUID.randomUUID().toString());
        return customerEntity;

    }
}
