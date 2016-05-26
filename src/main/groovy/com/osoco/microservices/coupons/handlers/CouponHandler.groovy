package com.osoco.microservices.coupons.handlers

import com.google.inject.Inject
import com.osoco.microservices.coupons.dao.CouponService
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
    CouponService couponRepository

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
                handleDelete(context)
            }
        }
    }

    private handleRequestWithoutPathTokens(Context context) {
        context.byMethod { method ->
            method.get {
                getAll(context)
            }
            method.post {
                handleAdd(context)
            }
            method.put {
                handleUpdate(context)
            }
        }
    }

    private void getAll(Context context) {
        Promise<List<Coupon>> couponsToRetrieve = couponRepository.get()
        couponsToRetrieve.then { coupons ->
            context.render(Jackson.json(coupons))
        }
    }

    private void handleAdd(Context context) {
        Promise<Coupon> couponToStore = context.parse(Jackson.fromJson(Coupon.class))
        couponToStore.then { coupon ->
            store(context, coupon)
        }
    }

    private void store(Context context, Coupon coupon) {
        Promise<Coupon> couponAdded = couponRepository.add(coupon)
        couponAdded.then {
            context.response.status(Status.OK).send()
        }
    }

    private void handleUpdate(Context context) {
        Promise<Coupon> couponToUpdate = context.parse(Jackson.fromJson(Coupon.class))
        couponToUpdate.then { coupon ->
            update(context, coupon)
        }
    }

    private void update(Context context, Coupon coupon) {
        Promise<Coupon> couponUpdated = couponRepository.update(coupon)
        couponUpdated.then {
            context.response.status(Status.OK).send()
        }
    }

    private void get(Context context) {
        Promise<Coupon> couponToRetrieve = couponRepository.get(context.pathTokens.code)
        couponToRetrieve.then { coupon ->
            context.render(Jackson.json(coupon))
        }
    }

    private void handleDelete(Context context) {
        Promise<Coupon> couponDeleted = couponRepository.delete(context.pathTokens.code)
        couponDeleted.then {
            context.response.status(Status.OK).send()
        }
    }

}
