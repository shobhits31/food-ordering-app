package com.upgrad.foodorderingapp.api.controller;


import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.AddressService;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.common.Constants;
import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.entity.AddressEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.entity.StateEntity;
import com.upgrad.foodorderingapp.service.exception.AddressNotFoundException;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.SaveAddressException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.ANF_005;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private ModelMapper modelMapper;

    /**
     * To Save the Address details
     *
     * @param authorization
     * @param saveAddressRequest
     * @return
     * @throws AuthorizationFailedException
     * @throws SaveAddressException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/address",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
                                                           @RequestBody(required = false) final SaveAddressRequest saveAddressRequest)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {

        String accessToken = FoodAppUtil.getAccessToken(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        AddressEntity addressEntity = convertToAddressEntity(saveAddressRequest);
        StateEntity state = addressService.getStateByUUID(saveAddressRequest.getStateUuid());
        addressEntity.setState(state);

        AddressEntity savedAddressEntity = addressService.saveAddress(addressEntity, customerEntity);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(savedAddressEntity.getUuid())
                .status(Constants.ADDRESS_REGISTRATION_MESSAGE);

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    /**
     * Get all the saved addresses of a customer
     *
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddress(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        String accessToken = FoodAppUtil.getAccessToken(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<AddressEntity> addressEntityList = addressService.getAllAddress(customerEntity);
        List<AddressList> addressList = new ArrayList<>();
        if (!addressEntityList.isEmpty()) {
            addressEntityList.forEach(
                    address -> addressList.add(convertToAddressList(address)));
        }

        AddressListResponse addressListResponse = new AddressListResponse()
                .addresses(addressList);

        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);

    }


    /**
     * Delete the address of a customer
     *
     * @param authorization
     * @param addressId
     * @return
     * @throws AuthorizationFailedException
     * @throws AddressNotFoundException
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(@RequestHeader("authorization") final String authorization,
                                                                    @PathVariable("address_id") final String addressId)
            throws AuthorizationFailedException, AddressNotFoundException {

        String accessToken = FoodAppUtil.getAccessToken(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        // Throws exception if the address UUID is not present
        if (FoodAppUtil.isEmptyField(addressId)) {
            throw new AddressNotFoundException(ANF_005.getCode(), ANF_005.getDefaultMessage());
        }

        AddressEntity addressEntity = addressService.getAddressByUUID(addressId, customerEntity);
        AddressEntity deletedAddressEntity= addressService.deleteAddress(addressEntity);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse()
                .id(UUID.fromString(deletedAddressEntity.getUuid()))
                .status(Constants.DELETE_ADDRESS_MESSAGE);

        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);

    }

    /**
     * Method to get AllStates
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<StatesListResponse> getAllStates() {

        List<StateEntity> stateEntities = addressService.getAllStates();
        if (!stateEntities.isEmpty()) {
            List<StatesList> stateList = new ArrayList<>();
            stateEntities.forEach(stateEntity -> {
                StatesList state = new StatesList()
                        .id(UUID.fromString(stateEntity.getUuid()))
                        .stateName(stateEntity.getStateName());
                stateList.add(state);
            });

            StatesListResponse statesListResponse = new StatesListResponse().states(stateList);
            return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
        } else
            //Return empty set if stateEntities is empty.
            return new ResponseEntity<StatesListResponse>(new StatesListResponse(), HttpStatus.OK);
    }

    /**
     * map the request object to address entity
     *
     * @param saveAddressRequest
     * @return
     */
    private AddressEntity convertToAddressEntity(final SaveAddressRequest saveAddressRequest) {
        AddressEntity addressEntity = modelMapper.map(saveAddressRequest, AddressEntity.class);
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);
        return addressEntity;

    }

    /**
     * map the request object to address entity
     *
     * @param addressEntity
     * @return
     */
    private AddressList convertToAddressList(final AddressEntity addressEntity) {
        AddressList addressList = modelMapper.map(addressEntity, AddressList.class);
        addressList.setId(UUID.fromString(addressEntity.getUuid()));
        addressList.getState().setId(UUID.fromString(addressEntity.getState().getUuid()));
        return addressList;

    }
}