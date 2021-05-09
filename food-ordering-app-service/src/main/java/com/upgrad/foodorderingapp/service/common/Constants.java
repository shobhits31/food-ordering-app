package com.upgrad.foodorderingapp.service.common;

public class Constants {
    public static final long EXPIRATION_TIME = 8;
    public static final String HEADER_STRING = "Basic ";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_ISSUER = "https://FoodOrderingApp.io";
    public static final String LOGIN_MESSAGE = "LOGGED IN SUCCESSFULLY";
    public static final String LOGOUT_MESSAGE = "LOGGED OUT SUCCESSFULLY";
    public static final String ORDER_PLACED_MESSAGE = "ORDER SUCCESSFULLY PLACED";
    public static final String CUSTOMER_REGISTRATION_MESSAGE = "CUSTOMER SUCCESSFULLY REGISTERED";
    public static final String DELETE_ADDRESS_MESSAGE = "ADDRESS DELETED SUCCESSFULLY";
    public static final String ADDRESS_REGISTRATION_MESSAGE = "ADDRESS SUCCESSFULLY REGISTERED";
    public static final String UPDATE_CUSTOMER_MESSAGE = "CUSTOMER DETAILS UPDATED SUCCESSFULLY";
    public static final String UPDATE_PASSWORD_MESSAGE = "CUSTOMER PASSWORD UPDATED SUCCESSFULLY";
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$";
    public static final String PINCODE_PATTERN = "^[1-9][0-9]{5}$";

}