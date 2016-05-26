package com.osoco.microservices.coupons.dao.impl

import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Promise

@Slf4j
class CouponServiceImpl implements CouponService {

    Map<String, Coupon> coupons = new HashMap<String, Coupon>()

    @Override
    Promise<Coupon> add(Coupon coupon) throws AlreadyExistsException {
        log.info "Storing coupon $coupon.code"
        Blocking.get {
            // TODO jbr - coupon validation
            Coupon existing = coupons.get(coupon.code)
            if (existing) {
                throw new AlreadyExistsException()
            } else {
                log.info "Coupon $coupon.code added!"
                coupons.put(coupon.code, coupon)
            }
        }
    }

    @Override
    Promise<Coupon> get(String code) throws NotFoundException {
        log.info "Getting coupon with code: $code"
        Blocking.get {
            Coupon existing = coupons.get(code)
            if (existing) {
                existing
            } else {
                throw new NotFoundException()
            }
        }
    }

    @Override
    Promise<List<Coupon>> get() {
        Blocking.get {
            coupons.values().asList()
        }
    }

    @Override
    Promise<Coupon> update(Coupon coupon) throws NotFoundException {
        log.info "Updating coupon $coupon.code"
        Blocking.get {
            // TODO jbr - coupon validation
            Coupon existing = coupons.get(coupon.code)
            if (existing) {
                log.info "Coupon $coupon.code updated!"
                coupons.put(coupon.code, coupon)
            } else {
                throw new NotFoundException()
            }
        }
    }

    @Override
    Promise<Coupon> delete(String code) throws NotFoundException {
        log.info "Deleting coupon $code"
        Blocking.get {
            Coupon existing = coupons.get(code)
            if (existing) {
                log.info "Coupon $code removed!"
                coupons.remove(code)
            } else {
                throw new NotFoundException()
            }
        }
    }
}
