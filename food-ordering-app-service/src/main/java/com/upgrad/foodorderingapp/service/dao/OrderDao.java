package com.upgrad.foodorderingapp.service.dao;

import com.upgrad.foodorderingapp.service.entity.CouponEntity;
import com.upgrad.foodorderingapp.service.entity.OrderEntity;
import com.upgrad.foodorderingapp.service.entity.OrderItemEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch coupon detail based on coupon name
     *
     * @param couponName coupon name received in path variable
     * @return coupon details
     */
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

    /**
     * Retrieves past order details ordered by date in descending order
     *
     * @param customerUUID customer UUID of logged in user
     * @return list of past orders
     */
    public List<OrderEntity> getPastOrders(String customerUUID) {
        return entityManager.createNamedQuery("getPastOrders", OrderEntity.class).setParameter("customerUUID", customerUUID).getResultList();
    }

    /**
     * Retrieves all items related to a particular order
     *
     * @param orderId order id for which items to be fetched
     * @return List of order items
     */
    public List<OrderItemEntity> getOrderItemByOrderId(Integer orderId) {
        return entityManager.createNamedQuery("itemsByOrderId", OrderItemEntity.class).setParameter("id", orderId).getResultList();
    }

    /**
     * Store order information in database
     *
     * @param order order information added by customer
     * @return return order details
     */
    public OrderEntity saveOrderDetails(OrderEntity order) {
        entityManager.persist(order);
        return order;
    }

    /**
     * Retrieves coupon based on coupon UUID
     *
     * @param couponUUID coupon UUID to be retrieved
     * @return coupon details
     */
    public CouponEntity getCouponById(String couponUUID) {
        try {
            return entityManager.createNamedQuery("getCouponByUUID", CouponEntity.class).setParameter("couponUUID", couponUUID).getSingleResult();
        }
        catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Stores order item information in database
     *
     * @param orderItemEntity order item details to be saved
     * @return order item details saved
     */
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }
}
