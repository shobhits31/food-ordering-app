package com.upgrad.foodorderingapp.service.common;

import com.upgrad.foodorderingapp.service.exception.AuthorizationFailedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.upgrad.foodorderingapp.service.common.GenericErrorCode.ATHR_001;

public class FoodAppUtil {

    /**
     * generate access-token from authorization header
     *
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    public static String getAccessToken(String authorization) throws AuthorizationFailedException {
        String[] bearerToken;
        try {
            bearerToken = authorization.split(Constants.TOKEN_PREFIX);
            if (bearerToken.length != 2) {
                throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
            }
        } catch (AuthorizationFailedException e) {
            throw new AuthorizationFailedException(ATHR_001.getCode(), ATHR_001.getDefaultMessage());
        }
        return bearerToken[1];
    }

    /**
     * Method to validate if a particular field is null or empty
     *
     * @param value
     * @return
     */
    public static boolean isEmptyField(final String value) {
        return value == null || value.isEmpty();
    }

    /**
     * Method to match a field with a requested pattern
     *
     * @param pattern
     * @param field
     * @return
     */
    public static boolean isValidPattern(final String pattern, final String field) {
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(field);
        return matcher.matches();
    }
}
