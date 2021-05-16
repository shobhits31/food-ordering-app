package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.common.Constants;
import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.dao.AddressDao;
import com.upgrad.foodorderingapp.service.dao.StateDao;
import com.upgrad.foodorderingapp.service.entity.AddressEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerAddressEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.entity.StateEntity;
import com.upgrad.foodorderingapp.service.exception.AddressNotFoundException;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private StateDao stateDao;

    /**
     * Method to get a State by uuid from db
     *
     * @param stateUuid
     * @return
     */
    public StateEntity getStateByUUID(final String stateUuid) throws AddressNotFoundException {
        StateEntity stateEntity = stateDao.getStateByUUID(stateUuid);
        // Throws exception if no state exists in db with the uuid
        if (stateEntity == null) {
            throw new AddressNotFoundException(ANF_002.getCode(), ANF_002.getDefaultMessage());
        }
        return stateEntity;
    }

    /**
     * Method to save AddressEntity into the database
     *
     * @param addressEntity
     * @param customerEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(final AddressEntity addressEntity, final CustomerEntity customerEntity) throws SaveAddressException {

        // Throw exception if any of the required field is Empty
        if (FoodAppUtil.isEmptyField((addressEntity.getFlatBuildingName()))
                || FoodAppUtil.isEmptyField(addressEntity.getLocality())
                || FoodAppUtil.isEmptyField(addressEntity.getCity())
                || FoodAppUtil.isEmptyField(addressEntity.getPincode())
                || FoodAppUtil.isEmptyField(addressEntity.getUuid())) {
            throw new SaveAddressException(SAR_001.getCode(), SAR_001.getDefaultMessage());
        }
        // Throw exception if the pincode is invalid
        if (!FoodAppUtil.isValidPattern(Constants.PINCODE_PATTERN, addressEntity.getPincode())){
            throw new SaveAddressException(SAR_002.getCode(),SAR_002.getDefaultMessage());
        }

        List<CustomerEntity> customerEntities = new ArrayList<>();
        customerEntities.add(customerEntity);
        addressEntity.setCustomers(customerEntities);
        AddressEntity savedAddressEntity = addressDao.saveAddress(addressEntity);

        return savedAddressEntity;
    }

    /**
     * Method to retrieve all address for a customer
     *
     * @param customerEntity
     * @return
     */
    public List<AddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        List<AddressEntity> addressList = new ArrayList<>();
        List<CustomerAddressEntity> customerAddressEntities = addressDao.getAllAddress(customerEntity);
        if (!customerAddressEntities.isEmpty()) {
            customerAddressEntities.forEach(
                    customerAddressEntity -> addressList.add(customerAddressEntity.getAddress()));
        }
        return addressList;
    }
    /**
     * Method to get address by address UUID for a customer
     *
     * @param addressUuid
     * @param customerEntity
     * @return
     * @throws AddressNotFoundException
     * @throws AuthorizationFailedException
     */
    public AddressEntity getAddressByUUID(final String addressUuid, final CustomerEntity customerEntity)
            throws AddressNotFoundException, AuthorizationFailedException {

        AddressEntity addressEntity = addressDao.getAddressByUUID(addressUuid);
        // Throw exception if no AddressEntity is found by UUID
        if (addressEntity == null) {
            throw new AddressNotFoundException(ANF_003.getCode(), ANF_003.getDefaultMessage());
        }

        CustomerAddressEntity customerAddressEntity = addressDao.getCustomerAddress(addressEntity, customerEntity);
        //Throw exception if the address doesn't belong to the customer
        if (customerAddressEntity == null) {
            throw new AuthorizationFailedException (ATHR_004.getCode(), ATHR_004.getDefaultMessage());
        }
        return addressEntity;
    }

    /**
     * Method to delete address from the database
     * @param addressEntity
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        addressDao.deleteAddress(addressEntity);
        return addressEntity;
    }
    /**
     * Method to retrieve all the states
     * @return
     */
    public List<StateEntity> getAllStates(){
        List<StateEntity> stateEntities = stateDao.getAllStates();
        return stateEntities;
    }
}


