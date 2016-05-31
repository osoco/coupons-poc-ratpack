package com.osoco.microservices.coupons.exception

class NotFoundException extends ApplicationException {

    NotFoundException() {
        super(404)
    }
}
