package com.osoco.microservices.coupons.exception

class AlreadyExistsException extends ApplicationException {

    AlreadyExistsException() {
        super(409)
    }
}
