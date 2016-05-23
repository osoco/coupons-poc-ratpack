package com.osoco.microservices.coupons.handlers

import ratpack.handling.Context
import ratpack.handling.Handler

class CouponsHandler implements Handler {

    @Override
    void handle(Context context) throws Exception {

    }

    void post(Context context) {
        context.render "Hello POST coupon: "
    }

    void get(Context context) {
        context.render "Hello GET coupons Handler"
    }

}
