package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.CustomerService;
import com.upgrad.foodorderingapp.service.businness.OrderService;
import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.entity.*;
import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;
import com.upgrad.foodorderingapp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Validate customer session and retrieve past orders of a customer
     * Exception handling for logged out or not logged in or session expired
     *
     * @param authorization Authorization string passed in request header
     * @return List of order items
     * @throws AuthorizationFailedException Customer not logged in or customer logged out or session expired
     */
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> pastOrders(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        String accessToken = FoodAppUtil.getAccessToken(authorization);
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        List<OrderEntity> pastOrders = orderService.getPastOrders(customerEntity.getUuid());
        CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
        if(pastOrders != null && !pastOrders.isEmpty()) {
            pastOrders.forEach(pastOrder -> {
                OrderList orderList = new OrderList();
                orderList.id(UUID.fromString(pastOrder.getUuid())).bill(BigDecimal.valueOf(pastOrder.getBill())).discount(BigDecimal.valueOf(pastOrder.getDiscount())).date(pastOrder.getDate().toString());

                CouponEntity pastOrderCoupon = pastOrder.getCouponId();
                if(pastOrderCoupon != null) {
                    OrderListCoupon coupon = new OrderListCoupon();
                    coupon.id(UUID.fromString(pastOrderCoupon.getUuid())).couponName(pastOrderCoupon.getCouponName()).percent(pastOrderCoupon.getPercent());
                    orderList.coupon(coupon);
                }

                OrderListPayment payment = new OrderListPayment();
                payment.id(UUID.fromString(pastOrder.getPaymentId().getUuid())).paymentName(pastOrder.getPaymentId().getPaymentName());
                orderList.payment(payment);

                OrderListCustomer customer = new OrderListCustomer();
                customer.id(UUID.fromString(pastOrder.getCustomerId().getUuid())).firstName(pastOrder.getCustomerId().getFirstName()).lastName(pastOrder.getCustomerId().getLastName()).emailAddress(pastOrder.getCustomerId().getEmailAddress()).contactNumber(pastOrder.getCustomerId().getContactNumber());
                orderList.customer(customer);

                OrderListAddress address = new OrderListAddress();
                address.id(UUID.fromString(pastOrder.getAddressId().toString())).flatBuildingName(pastOrder.getAddressId().toString()).locality(pastOrder.getAddressId().toString()).pincode(pastOrder.getAddressId().toString());

                OrderListAddressState addressState = new OrderListAddressState();
//                StateEntity stateEntity =
                addressState.id(UUID.fromString(pastOrder.getAddressId().toString())).stateName(pastOrder.getAddressId().toString());
                address.state(addressState);
                orderList.address(address);

                List<OrderItemEntity> orderItems = orderService.getOrderItemByOrderId(pastOrder.getId());
                if (orderItems != null) {
                    orderItems.stream().forEach(orderItem -> {
                        // Populate each item detail in the response
                        ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                        itemQuantityResponse.quantity(orderItem.getQuantity()).price(orderItem.getPrice());
                        ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
                        itemQuantityResponseItem.id(UUID.fromString(orderItem.getItemId().getUuid()))
                                .itemName(orderItem.getItemId().getItem_name()).itemPrice(orderItem.getPrice())
                                .type(ItemQuantityResponseItem.TypeEnum
                                        .fromValue(orderItem.getItemId().getType()));
                        itemQuantityResponse.item(itemQuantityResponseItem);
                        orderList.addItemQuantitiesItem(itemQuantityResponse);
                    });
                }
                if(orderList.getItemQuantities() == null) {
                    orderList.setItemQuantities(new ArrayList<ItemQuantityResponse>());
                }
                customerOrderResponse.addOrdersItem(orderList);
            });
        }
        if(customerOrderResponse.getOrders() == null) {
            customerOrderResponse.setOrders(new ArrayList<OrderList>());
        }
        return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }
}
