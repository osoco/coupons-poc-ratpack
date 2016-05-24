package com.osoco.microservices.coupons.handlers

import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Promise
import ratpack.handling.Context
import ratpack.handling.InjectionHandler
import ratpack.jackson.Jackson

class CouponHandler extends InjectionHandler {

    public void handle(Context context, CouponRepository couponRepository) throws Exception {
        if (context.pathTokens) {
            context.byMethod { method ->
                method.get {
                    context.render "GET with coupon $context.pathTokens.code"
                }
                method.delete {
                    context.render "DELETE with coupon $context.pathTokens.code"
                }
            }
        } else {
            context.byMethod { method ->
                method.get {
                    context.render "GET"
                }
                method.post {
                    Promise<Coupon> couponToStore = context.parse(Jackson.fromJson(Coupon.class))
                    couponToStore.then { coupon ->
                        couponRepository.add(coupon)
                        context.response.status(200).send()
                    }
                }
                method.put {
                    context.render "PUT"
                }
            }
        }
    }

}
