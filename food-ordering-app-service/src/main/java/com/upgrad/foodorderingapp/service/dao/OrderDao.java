package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    public CouponEntity getCouponByCouponName(String couponName) {
        try {
            List<CouponEntity> couponEntityList = entityManager.createNamedQuery("couponByCouponName", CouponEntity.class).setParameter("couponName", couponName).getResultList();
            if(couponEntityList != null && !couponEntityList.isEmpty()) {
                return couponEntityList.get(0);
            }
            else {
                return null;
            }
        }
        catch(NoResultException nre) {
            return null;
        }
    }
}
