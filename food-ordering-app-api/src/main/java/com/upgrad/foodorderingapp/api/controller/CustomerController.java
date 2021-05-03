package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.SignupCustomerRequest;
import com.upgrad.foodorderingapp.api.model.SignupCustomerResponse;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.exception.SignUpRestrictedException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<SignupCustomerResponse> userSignup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        final CustomerEntity customerEntity = convertToUserEntity(signupCustomerRequest);
        final CustomerEntity createdUserEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdUserEntity.getUuid()).status("");
        return new ResponseEntity<SignupCustomerResponse>(customerResponse, HttpStatus.CREATED);
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
