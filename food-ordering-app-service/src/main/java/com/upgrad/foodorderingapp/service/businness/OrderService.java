package com.upgrad.foodorderingapp.service.businness;

import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.dao.OrderDao;
import com.upgrad.foodorderingapp.service.entity.CouponEntity;
import com.upgrad.foodorderingapp.service.entity.OrderEntity;
import com.upgrad.foodorderingapp.service.entity.OrderItemEntity;
import com.upgrad.foodorderingapp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@Service
public class OrderService {

    @Autowired
    private OrderDao orderDao;

    /**
     * Fetch coupon details based on coupon name
     * Exception Handling for Coupon not found by name or coupon name should not be blank
     *
     * @param couponName coupon name from path variable
     * @return CouponEntity object
     * @throws CouponNotFoundException Coupon not found by name or coupon name should not be blank
     */
    public CouponEntity getCouponByCouponName(String couponName) throws CouponNotFoundException {
        if(FoodAppUtil.isEmptyField(couponName)) {
            throw new CouponNotFoundException(CPF_002.getCode(), CPF_002.getDefaultMessage());
        }
        CouponEntity couponEntity = orderDao.getCouponByCouponName(couponName);

        if(couponEntity == null) {
            throw new CouponNotFoundException(CPF_001.getCode(), CPF_001.getDefaultMessage());
        }
        return couponEntity;
    }

    /**
     * Retrieves list of past orders of a customer
     *
     * @param customerUUID UUID of logged in customer
     * @return List of order details
     */
    public List<OrderEntity> getPastOrders(String customerUUID) {
        return orderDao.getPastOrders(customerUUID);
    }

    /**
     * Retrieves list of items for a particular order
     *
     * @param orderId order id for which items have to be fetched
     * @return list of order items
     */
    public List<OrderItemEntity> getOrderItemByOrderId(Integer orderId) {
        return orderDao.getOrderItemByOrderId(orderId);
    }

    /**
     * Store order in the database
     *
     * @param order order information to be saved in database
     * @return order details saved in database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderEntity saveOrderDetails(OrderEntity order) {
        order.setUuid(UUID.randomUUID().toString());
        order.setDate(new Date());
        orderDao.saveOrderDetails(order);
        return order;
    }

    /**
     * Retrieves coupon details based on coupon id
     *
     * @param couponUUID coupon uuid for which coupon details to be fetched
     * @return coupon details
     * @throws CouponNotFoundException No coupon by this id
     */
    public CouponEntity getCouponById(String couponUUID) throws CouponNotFoundException {
        CouponEntity coupon = orderDao.getCouponById(couponUUID);
        if (coupon == null) {
            throw new CouponNotFoundException(CPF_002.getCode(), CPF_002.getDefaultMessage());
        }
        return coupon;
    }

    /**
     * Store order item information in the database
     *
     * @param orderItemEntity order item details to be saved
     * @return order item details
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        orderDao.saveOrderItem(orderItemEntity);
        return orderItemEntity;
    }
}
