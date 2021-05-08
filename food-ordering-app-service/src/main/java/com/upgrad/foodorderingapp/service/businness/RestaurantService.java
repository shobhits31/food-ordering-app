package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.dao.CategoryDao;
import com.upgrad.foodorderingapp.service.dao.RestaurantDao;
import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantCategoryEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantItemEntity;
import com.upgrad.foodorderingapp.service.exception.CategoryNotFoundException;
import com.upgrad.foodorderingapp.service.exception.RestaurantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    public List<RestaurantEntity>  restaurantsByRating(){
        return restaurantDao.getAllRestaurants();
    }



    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException{
        if(restaurantName==null || restaurantName==""){
            throw new RestaurantNotFoundException(RNF_003.getCode(), RNF_003.getDefaultMessage());
        }
        List<RestaurantEntity> restaurantEntity = restaurantDao.getRestaurantByName(restaurantName);
        return restaurantEntity;
    }

    public RestaurantEntity restaurantByUUID(final String restaurantId) throws RestaurantNotFoundException{
        if(restaurantId==null || restaurantId=="") {
            throw new RestaurantNotFoundException(RNF_003.getCode(), RNF_003.getDefaultMessage());
        }
        RestaurantEntity restaurantEntity=restaurantDao.getRestaurantById(restaurantId);
        if(restaurantEntity==null)
            throw new RestaurantNotFoundException(RNF_001.getCode(), RNF_001.getDefaultMessage());
        return restaurantEntity;
    }

    /**
     * Method to get all restaurants using category uuid
     *
     * @param categoryUuid - String represents category UUID
     * @return - list of Restaurant Entities using  category uuid
     * @throws CategoryNotFoundException - if category uuid is empty, or no category exists for the give category uuid
     */
    public List<RestaurantEntity> restaurantByCategory(final String categoryUuid) throws CategoryNotFoundException {
        // Throw exception if the category uuid is empty
        if (categoryUuid.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);
        // Throw exception if no category exists for the give category uuid
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        List<RestaurantEntity> restaurantEntities = new ArrayList<>();

        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantDao
                .restaurantsByCategoryId(categoryEntity.getUuid());
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            restaurantEntities.add(restaurantCategoryEntity.getRestaurantEntity());
        });
        return restaurantEntities;
    }

}
