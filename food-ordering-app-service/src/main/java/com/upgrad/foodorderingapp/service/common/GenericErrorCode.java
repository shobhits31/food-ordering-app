package com.upgrad.foodorderingapp.service.common;

import java.util.HashMap;
import java.util.Map;

public enum GenericErrorCode implements ErrorCode {

    /**
     * Error message: <b>An unexpected error occurred. Please contact System Administrator</b><br>
     * <b>Cause:</b> This error could have occurred due to undetermined runtime errors.<br>
     * <b>Action: None</b><br>
     */
    GEN_001("GEN-001", "An unexpected error occurred. Please contact System Administrator"),
    SGUR_001("SGR-001", "This contact number is already registered! Try other contact number."),
    SGUR_002("SGR-002", "Invalid email-id format!"),
    SGUR_003("SGR-003", "Invalid contact number!"),
    SGUR_004("SGR-004", "Weak password!"),
    SGUR_005("SGR-005", "Except last name all fields should be filled"),
    ATH_001("ATH-001", "This contact number has not been registered!"),
    ATH_002("ATH-002", "Invalid Credentials"),
    ATH_003("ATH-003", "Incorrect format of decoded customer name and password"),
    ATHR_001("ATHR-001", "Customer is not Logged in."),
    ATHR_002("ATHR-002", "Customer is logged out. Log in again to access this endpoint."),
    ATHR_003("ATHR-003", "Your session is expired. Log in again to access this endpoint."),
    UCR_001("UCR-001","Weak password!"),
    UCR_002("UCR-002","First name field should not be empty"),
    UCR_003("UCR-003","No field should be empty"),
    UCR_004("UCR-004","Incorrect old password!"),
    CNF_001("CNF-001","Category id field should not be empty"),
    CNF_002("CNF-002","No category by this id"),
    CPF_001("CPF-001", "No coupon by this name"),
    CPF_002("CPF-002", "Coupon name field should not be empty"),
    PNF_002("PNF-002", "No payment method found by this id"),
    ANF_003("ANF-003", "No address by this id"),
    RNF_001("RNF-001", "No restaurant by this id");

    private static final Map<String, GenericErrorCode> LOOKUP = new HashMap<String, GenericErrorCode>();

    static {
        for (final GenericErrorCode enumeration : GenericErrorCode.values()) {
            LOOKUP.put(enumeration.getCode(), enumeration);
        }
    }

    private final String code;

    private final String defaultMessage;

    private GenericErrorCode(final String code, final String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

}
