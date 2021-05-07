package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CategoryEntity;
import com.upgrad.foodorderingapp.service.entity.CategoryItemEntity;
import com.upgrad.foodorderingapp.service.entity.CustomerAuthEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDao {
    private final Logger log = LoggerFactory.getLogger(CustomerDao.class);
    @PersistenceContext
    private EntityManager entityManager;


    public List<CategoryEntity> getAllCategories(){
        log.info("getting all categories from the database");
        List<CategoryEntity> categoryEntityList = entityManager.createNamedQuery("getAllCategory").getResultList();
        return categoryEntityList;
    }

    public CategoryEntity getCategoryById(final String categoryId){
        log.info("getting all categories from the database");
        try {
            CategoryEntity categoryEntity = entityManager.createNamedQuery("getCategoryById",CategoryEntity.class).setParameter("uuid",categoryId)
                    .getSingleResult();
            return categoryEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    public List<CategoryItemEntity> getAllCategoryItems(final String categoryId){
        List<CategoryItemEntity> categoryItemEntity = entityManager.createNamedQuery("getAllCategoryItemsByUuid",CategoryItemEntity.class).setParameter("uuid",categoryId)
                    .getResultList();
        return categoryItemEntity;
    }
}
