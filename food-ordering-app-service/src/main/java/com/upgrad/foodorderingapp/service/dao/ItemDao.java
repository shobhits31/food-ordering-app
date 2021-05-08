package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {
    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);
    @PersistenceContext
    private EntityManager entityManager;

    public List<RestaurantItemEntity> getItemForRestaurantUUID(final String restaurantId){
        log.info("start getting all restaurant items from the database");
        List<RestaurantItemEntity> restaurantItemEntityList = entityManager.createNamedQuery("getAllRestaurantItemsByRestaurantId",RestaurantItemEntity.class)
                .setParameter("uuid",restaurantId).getResultList();
        log.info("end getting all restaurant items from the database");
        return restaurantItemEntityList;
    }

}
