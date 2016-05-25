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

    private handleRequestWithPathTokens(Context context, CouponRepository couponRepository) {
        context.byMethod { method ->
            method.get {
                get(context, couponRepository)
            }
            method.delete {
                delete(context, couponRepository)
            }
        }
    }

    private handleRequestWithoutPathTokens(Context context, CouponRepository couponRepository) {
        context.byMethod { method ->
            method.get {
                getAll(context, couponRepository)
            }
            method.post {
                add(context, couponRepository)
            }
            method.put {
                update(context, couponRepository)
            }
        }
    }

    private void getAll(Context context, CouponRepository couponRepository) {
        Promise<List<Coupon>> couponsToRetrieve = couponRepository.get()
        couponsToRetrieve.then { coupons ->
            context.render(Jackson.json(coupons))
        }
    }

    private void add(Context context, CouponRepository couponRepository) {
        Promise<Coupon> couponToStore = context.parse(Jackson.fromJson(Coupon.class))
        couponToStore.then { coupon ->
            try {
                couponRepository.add(coupon)
                context.response.status(Status.OK).send()
            } catch (AlreadyExistsException ex) {
                context.response.status(Status.of(409)).send()
            }
        }
    }

    private void update(Context context, CouponRepository couponRepository) {
        Promise<Coupon> couponToUpdate = context.parse(Jackson.fromJson(Coupon.class))
        couponToUpdate.then { coupon ->
            try {
                couponRepository.update(coupon)
                context.response.status(Status.OK).send()
            } catch (NotFoundException ex) {
                context.response.status(Status.of(404)).send()
            }
        }
    }

    private void get(Context context, CouponRepository couponRepository) {
        Promise<Coupon> couponToRetrieve = couponRepository.get(context.pathTokens.code)
        couponToRetrieve.onError { notFoundException ->
            context.response.status(Status.of(404)).send()
        }.then { coupon ->
            context.render(Jackson.json(coupon))
        }
    }

    private void delete(Context context, CouponRepository couponRepository) {
        try {
            couponRepository.delete(context.pathTokens.code)
            context.response.status(Status.OK).send()
        } catch (NotFoundException ex) {
            context.response.status(Status.of(404)).send()
        }
    }

}
