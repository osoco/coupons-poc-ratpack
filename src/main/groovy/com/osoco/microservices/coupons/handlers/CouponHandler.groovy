package com.osoco.microservices.coupons.handlers

import com.google.inject.Inject
import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Promise
import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status
import ratpack.jackson.Jackson

@Slf4j
class CouponHandler implements Handler {

    @Inject
    CouponRepository couponRepository

    @Override
    public void handle(Context context) throws Exception {
        if (context.pathTokens) {
            handleRequestWithPathTokens(context)
        } else {
            handleRequestWithoutPathTokens(context)
        }
    }

    private handleRequestWithPathTokens(Context context) {
        context.byMethod { method ->
            method.get {
                get(context)
            }
            method.delete {
                delete(context)
            }
        }
    }

    private handleRequestWithoutPathTokens(Context context) {
        context.byMethod { method ->
            method.get {
                getAll(context)
            }
            method.post {
                add(context)
            }
            method.put {
                update(context)
            }
        }
    }

    private void getAll(Context context) {
        Promise<List<Coupon>> couponsToRetrieve = couponRepository.get()
        couponsToRetrieve.then { coupons ->
            context.render(Jackson.json(coupons))
        }
    }

    private void add(Context context) {
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

    private void update(Context context) {
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

    private void get(Context context) {
        Promise<Coupon> couponToRetrieve = couponRepository.get(context.pathTokens.code)
        couponToRetrieve.onError { notFoundException ->
            context.response.status(Status.of(404)).send()
        }.then { coupon ->
            context.render(Jackson.json(coupon))
        }
    }

    private void delete(Context context) {
        try {
            couponRepository.delete(context.pathTokens.code)
            context.response.status(Status.OK).send()
        } catch (NotFoundException ex) {
            context.response.status(Status.of(404)).send()
        }
    }

}
