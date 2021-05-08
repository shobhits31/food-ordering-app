package com.upgrad.foodorderingapp.service.common;

public enum ItemType {
    VEG(""),
    NON_VEG("");
    private final String item;

    ItemType(String item) {
        this.item = item;
    }
}
