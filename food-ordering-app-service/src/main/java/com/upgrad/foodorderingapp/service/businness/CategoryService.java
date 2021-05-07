package com.upgrad.foodorderingapp.service.businness;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;
import com.upgrad.foodorderingapp.service.dao.CategoryDao;
import com.upgrad.foodorderingapp.service.dao.CustomerDao;
import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.RestaurantCategoryEntity;
import com.upgrad.foodorderingapp.service.exception.CategoryNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CategoryDao categoryDao;


    public List<CategoryEntity> getAllCategories(){
        log.info("******Start get all categories*******");
        List<CategoryEntity> categoryEntityList= categoryDao.getAllCategories();
        log.info("******End get all categories*******");
        return categoryEntityList;
    }

    public List<CategoryItemEntity> getCategoryById(final String categoryId) throws CategoryNotFoundException{
        log.info("******Start categories by category Id*******");
        if(categoryId==null || categoryId.equals("")){
            throw new CategoryNotFoundException(CNF_001.getCode(), CNF_001.getDefaultMessage());
        }
        List<CategoryItemEntity> categoryEntity = categoryDao.getAllCategoryItems(categoryId);
        if(categoryEntity.size()<1){
            throw new CategoryNotFoundException(CNF_002.getCode(), CNF_002.getDefaultMessage());
        }
        log.info("******End categories by category Id*******");
        return categoryEntity;
    }

    public List<CategoryEntity> getCategoriesByRestaurant(final String restaurantId){
        List<RestaurantCategoryEntity> restaurantCategoryEntities  = categoryDao.getAllCategoriesByRestaurant(restaurantId);
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();
        for(RestaurantCategoryEntity ct:restaurantCategoryEntities){
            categories.add(ct.getCategoryEntity());
        }
        return  categories;

    }


}
