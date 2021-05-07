package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CustomerAuthEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {
    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);
    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantEntity> getAllRestaurants(){
            return entityManager.createNamedQuery("getAllRestaurants", RestaurantEntity.class).getResultList();
    }

    public List<RestaurantEntity> getRestaurantByName(final String restaurantName){
          return  entityManager.createNamedQuery("getRestaurantByName",RestaurantEntity.class).setParameter("rName",restaurantName)
                    .getResultList();
    }

    public RestaurantEntity getRestaurantById(final String restaurantId){
        try{
            RestaurantEntity restaurantEntity=entityManager.createNamedQuery("getRestaurantById",RestaurantEntity.class).setParameter("uuid",restaurantId).getSingleResult();
            return restaurantEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
