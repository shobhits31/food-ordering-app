package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.OrderListCoupon;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.businness.OrderService;
import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.entity.CouponEntity;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    /**
     * Validate customer session and retrieve coupon based on coupon name
     * Exception handling for logged out or not logged in or session expired and for coupon not available
     *
     * @param authorization Authorization string passed in request header
     * @param couponName Coupon Name passed as path variable
     * @return Coupon Name
     * @throws AuthorizationFailedException Customer not logged in or customer logged out or session expired
     * @throws CouponNotFoundException No coupon by this name or coupon not found
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<OrderListCoupon> couponByCouponName(@RequestHeader("authorization") final String authorization, @PathVariable("coupon_name") final String couponName) throws AuthorizationFailedException, CouponNotFoundException {
        String accessToken = FoodAppUtil.getAccessToken(authorization);
        customerService.getCustomer(accessToken);

        CouponEntity couponEntity = orderService.getCouponByCouponName(couponName);
        OrderListCoupon orderListCoupon = new OrderListCoupon();
        orderListCoupon.id(UUID.fromString(couponEntity.getUuid())).couponName(couponEntity.getCouponName()).percent(couponEntity.getPercent());
        return new ResponseEntity<>(orderListCoupon, HttpStatus.OK);
    }
}
