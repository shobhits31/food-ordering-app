package com.upgrad.foodorderingapp.api.controller;

import com.upgrad.foodorderingapp.api.model.*;
import com.upgrad.foodorderingapp.service.businness.*;
import com.upgrad.foodorderingapp.service.common.FoodAppUtil;
import com.upgrad.foodorderingapp.service.entity.*;
import com.upgrad.foodorderingapp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.*;

@RestController
@CrossOrigin
public class OrderController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private PaymentService paymentService;

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

        List<OrderEntity> pastOrders = orderService.getOrdersByCustomers(customerEntity.getUuid());
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
                customer.id(UUID.fromString(pastOrder.getCustomer().getUuid())).firstName(pastOrder.getCustomer().getFirstName()).lastName(pastOrder.getCustomer().getLastName()).emailAddress(pastOrder.getCustomer().getEmailAddress()).contactNumber(pastOrder.getCustomer().getContactNumber());
                orderList.customer(customer);

                OrderListAddress address = new OrderListAddress();
                address.id(UUID.fromString(pastOrder.getAddress().getUuid())).flatBuildingName(pastOrder.getAddress().toString()).locality(pastOrder.getAddress().toString()).pincode(pastOrder.getAddress().toString());

                OrderListAddressState addressState = new OrderListAddressState();
                addressState.id(UUID.fromString(pastOrder.getAddress().getState().getUuid())).stateName(pastOrder.getAddress().getState().getStateName());
                address.state(addressState);
                orderList.address(address);

                List<OrderItemEntity> orderItems = orderService.getOrderItemByOrderId(pastOrder.getId());
                if (orderItems != null) {
                    orderItems.stream().forEach(orderItem -> {
                        // Populate each item detail in the response
                        ItemQuantityResponse itemQuantityResponse = new ItemQuantityResponse();
                        itemQuantityResponse.quantity(orderItem.getQuantity()).price(orderItem.getPrice());
                        ItemQuantityResponseItem itemQuantityResponseItem = new ItemQuantityResponseItem();
                        itemQuantityResponseItem.id(UUID.fromString(orderItem.getItemId().getUuid())).itemName(orderItem.getItemId().getItemName()).itemPrice(orderItem.getPrice());
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

    /**
     * Validate customer session and store order details of a customer
     * Exception handling for logged out or not logged in or session expired, No coupon by this id, No address by this id, You are not authorized to view/update/delete any one else's address, No payment method found by this id, No restaurant by this id, No item by this id exist
     *
     * @param authorization Authorization string passed in request header
     * @param saveOrderRequest The Request holding all the details of order
     * @return The response with the created order uuid and success message
     * @throws AuthorizationFailedException logged out or not logged in or session expired
     * @throws AddressNotFoundException No address by this id, You are not authorized to view/update/delete any one else's address
     * @throws CouponNotFoundException No coupon by this id
     * @throws RestaurantNotFoundException No restaurant by this id
     * @throws PaymentMethodNotFoundException No payment method found by this id
     * @throws ItemNotFoundException No item by this id exist
     */
    @RequestMapping(method = RequestMethod.POST, path ="/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") final String authorization, @RequestBody SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, AddressNotFoundException, CouponNotFoundException, RestaurantNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException {
        String accessToken = FoodAppUtil.getAccessToken(authorization);
        CustomerEntity loggedInCustomer = customerService.getCustomer(accessToken);

        CouponEntity coupon = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
        PaymentEntity payment = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
        AddressEntity address = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), loggedInCustomer);
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(saveOrderRequest.getRestaurantId().toString());

        OrderEntity order = new OrderEntity();
        order.setUuid(UUID.randomUUID().toString());
        order.setCustomer(loggedInCustomer);
        order.setCouponId(coupon);
        order.setAddress(address);
        order.setPaymentId(payment);
        order.setRestaurant(restaurant);
        order.setBill(saveOrderRequest.getBill() != null ? saveOrderRequest.getBill().doubleValue() : null);
        order.setDiscount(saveOrderRequest.getDiscount() != null ? saveOrderRequest.getDiscount().doubleValue() : null);
        order.setDate(new Date());
        final OrderEntity saveOrder = orderService.saveOrder(order);

        List<ItemQuantity> itemQuantities = saveOrderRequest.getItemQuantities();
        for(ItemQuantity itemQuantity: itemQuantities) {
            ItemEntity item = itemService.getItemByUUID(itemQuantity.getItemId().toString());
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setItemId(item);
            orderItem.setOrderId(saveOrder);
            orderItem.setQuantity(itemQuantity.getQuantity());
            orderItem.setPrice(itemQuantity.getPrice());
            orderService.saveOrderItem(orderItem);
        }

        SaveOrderResponse response = new SaveOrderResponse();
        response.id(saveOrder.getUuid()).status("ORDER SUCCESSFULLY PLACED");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
