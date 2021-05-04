package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CustomerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);
    @PersistenceContext
    private EntityManager entityManager;

    public CustomerEntity createUser(CustomerEntity customerEntity) {
        log.info("create a new customer in the database");
        entityManager.persist(customerEntity);
        log.info("customer successfully created");
        return customerEntity;
    }

    public CustomerEntity getUserByEmail(String emailAddress) {
        return new CustomerEntity();
    }

    public CustomerEntity getCustomerByContact(String contactNumber) {
        return new CustomerEntity();
    }
}
