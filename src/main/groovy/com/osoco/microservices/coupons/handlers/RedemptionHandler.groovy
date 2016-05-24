package com.osoco.microservices.coupons.handlers

import ratpack.handling.Context
import ratpack.handling.Handler

class RedemptionHandler implements Handler {

    @Override
    void handle(Context context) throws Exception {
        context.byMethod { method ->
            method.post({
                context.render "POST redemption with coupon $context.pathTokens.code"
            })
        }
    }

}
