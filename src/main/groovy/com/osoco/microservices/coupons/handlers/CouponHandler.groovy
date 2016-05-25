package com.osoco.microservices.coupons.handlers

import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Promise
import ratpack.handling.Context
import ratpack.handling.InjectionHandler
import ratpack.http.Status
import ratpack.jackson.Jackson

@Slf4j
class CouponHandler extends InjectionHandler {

    public void handle(Context context, CouponRepository couponRepository) throws Exception {
        if (context.pathTokens) {
            handleRequestWithPathTokens(context, couponRepository)
        } else {
            handleRequestWithoutPathTokens(context, couponRepository)
        }
    }

    private handleRequestWithoutPathTokens(Context context, couponRepository) {
        context.byMethod { method ->
            method.get {
                Promise<List<Coupon>> couponsToRetrieve = couponRepository.get()
                couponsToRetrieve.then { coupons ->
                    context.render(Jackson.json(coupons))
                }
            }
            method.post {
                Promise<Coupon> couponToStore = context.parse(Jackson.fromJson(Coupon.class))
                couponToStore.then { coupon ->
                    try {
                        couponRepository.add(coupon)
                        context.response.status(Status.OK).send()
                    } catch (AlreadyExistsException except) {
                        context.response.status(Status.of(409)).send()
                    }
                }
            }
            method.put {
                Promise<Coupon> couponToUpdate = context.parse(Jackson.fromJson(Coupon.class))
                couponToUpdate.then { coupon ->
                    try {
                        couponRepository.update(coupon)
                        context.response.status(Status.OK).send()
                    } catch (NotFoundException except) {
                        context.response.status(Status.of(404)).send()
                    }
                }
            }
        }
    }

    private handleRequestWithPathTokens(Context context, CouponRepository couponRepository) {
        context.byMethod { method ->
            method.get {
                Promise<Coupon> couponToRetrieve = couponRepository.get(context.pathTokens.code)
                couponToRetrieve.onError { notFoundExcept ->
                    context.response.status(Status.of(404)).send()
                }.then { coupon ->
                    context.render(Jackson.json(coupon))
                }
            }
            method.delete {
                context.render "DELETE with coupon $context.pathTokens.code"
            }
        }
    }

}
