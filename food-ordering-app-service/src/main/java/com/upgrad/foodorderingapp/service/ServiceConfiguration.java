package com.upgrad.foodorderingapp.service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Enabling the component scan and entity scan of classes in the below mentioned "com.upgrad.FoodOrderingApp.service" and "com.upgrad.FoodOrderingApp.service.entity" packages respectively.
 */
@Configuration
@ComponentScan("com.upgrad.foodorderingapp.service")
@EntityScan("com.upgrad.foodorderingapp.service.entity")
public class ServiceConfiguration {
}
