package com.upgrad.foodorderingapp.api.exception;

import com.upgrad.foodorderingapp.api.model.ErrorResponse;
import com.upgrad.foodorderingapp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

        /**
         * @param exp     - SignUpRestrictedException
         * @param request - WebRequest
         * @return
         */
        @ExceptionHandler(SignUpRestrictedException.class)
        public ResponseEntity<ErrorResponse> signUpRestrictionException(
                final SignUpRestrictedException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }

        /**
         * @param exp     - AuthenticationFailedException
         * @param request - WebRequest
         * @return
         */
        @ExceptionHandler(AuthenticationFailedException.class)
        public ResponseEntity<ErrorResponse> authenticationFailedException(
                final AuthenticationFailedException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.UNAUTHORIZED);
        }

        /**
         *
         * @param exp
         * @param request
         * @return
         */
        @ExceptionHandler(AuthorizationFailedException.class)
        public ResponseEntity<ErrorResponse> authorizationFailedException(
                final AuthorizationFailedException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.FORBIDDEN);
        }

        /**
         *
         * @param exp     - UpdateCustomerException
         * @param request - WebRequest
         * @return
         */
        @ExceptionHandler(UpdateCustomerException.class)
        public ResponseEntity<ErrorResponse> updateCustomerException(
                final UpdateCustomerException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }

        /**
         * @param exp     - SaveAddressException
         * @param request - WebRequest
         * @return
         */
        @ExceptionHandler(SaveAddressException.class)
        public ResponseEntity<ErrorResponse> saveAddressException(
                final SaveAddressException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.BAD_REQUEST);
        }

        /**
         * @param exp     - AddressNotFoundException
         * @param request - WebRequest
         * @return
         */
        @ExceptionHandler(AddressNotFoundException.class)
        public ResponseEntity<ErrorResponse> addressNotFoundException(
                final AddressNotFoundException exp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exp.getCode())
                    .message(exp.getErrorMessage()), HttpStatus.NOT_FOUND);
        }

        /**
         * @param excp      - CouponNotFoundException
         * @param request   - WebRequest
         * @return
         */
        @ExceptionHandler(CouponNotFoundException.class)
        public ResponseEntity<ErrorResponse> couponNotFoundException(
                final CouponNotFoundException excp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                    .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
        }
        /**
         * @param excp      - CategoryNotFoundException
         * @param request   - WebRequest
         * @return
         */
        @ExceptionHandler(CategoryNotFoundException.class)
        public ResponseEntity<ErrorResponse> categoryNotFoundException(
                final CategoryNotFoundException excp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                    .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
        }

        /**
         * @param excp      - RestaurantNotFoundException
         * @param request   - WebRequest
         * @return
         */
        @ExceptionHandler(RestaurantNotFoundException.class)
        public ResponseEntity<ErrorResponse> categoryNotFoundException(
                final RestaurantNotFoundException excp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                    .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
        }

        /**
         * @param excp      - PaymentMethodNotFoundException
         * @param request   - WebRequest
         * @return
         */
        @ExceptionHandler(PaymentMethodNotFoundException.class)
        public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(
                final PaymentMethodNotFoundException excp, final WebRequest request) {
            return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(excp.getCode())
                    .message(excp.getErrorMessage()), HttpStatus.NOT_FOUND);
        }
}
