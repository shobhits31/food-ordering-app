package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.AddressEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerAddressEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import com.upgrad.foodorderingapp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class AddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to save AddressEntity in the database
     *
     * @param addressEntity
     * @return
     */
    public AddressEntity saveAddress(AddressEntity addressEntity) {
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    /**
     * Method to save Customer Address in the database
     *
     * @param customerAddressEntity
     */
    public void saveCustomerAddr(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
    }

    /**
     * Method to retrieve all addresses for a customer
     *
     * @param customerEntity
     * @return
     */
    public List<CustomerAddressEntity> getAllAddress(final CustomerEntity customerEntity) {
        List<CustomerAddressEntity> addressList = entityManager
                .createNamedQuery("customerAddressByCustomer", CustomerAddressEntity.class)
                .setParameter("customer", customerEntity)
                .getResultList();
        if (addressList == null) {
            return Collections.emptyList();
        }
        return addressList;
    }
    /**
     * Method to get AddressEntity by UUID from db
     *
     * @param addressUuid
     * @return
     */
    public AddressEntity getAddressByUUID(final String addressUuid) {
        try {
            return entityManager.createNamedQuery("addressByUuid", AddressEntity.class)
                    .setParameter("addressUuid", addressUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to retrieve CustomerAddressEntity by customer and address uuid
     *
     * @param address
     * @param customer
     * @return
     */
    public CustomerAddressEntity getCustomerAddress(AddressEntity address, CustomerEntity customer) {
        try {
            return entityManager.createNamedQuery("customerAddressByCustomerAndAddrId", CustomerAddressEntity.class)
                    .setParameter("customer", customer)
                    .setParameter("address", address)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to delete AddressEntity from the db
     *
     * @param addressEntity
     */
    public void deleteAddress(AddressEntity addressEntity) {
        entityManager.remove(addressEntity);
    }
}
