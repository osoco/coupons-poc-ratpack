package com.osoco.microservices.coupons.exception

class ApplicationException extends Exception {

    Integer code

    ApplicationException(Integer code) {
        this.code = code
    }
}
