package com.osoco.microservices.coupons.dao.impl

import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Promise

@Slf4j
class CouponRepositoryImpl implements CouponRepository {

    Map<String, Coupon> coupons = new HashMap<String, Coupon>()

    @Override
    void add(Coupon coupon) throws AlreadyExistsException {
        log.info "Trying to store coupon ${coupon.code}"
        // TODO jbr - coupon validation
        Coupon existing = coupons.get(coupon.code)
        log.info "Exists? " + (existing)
        if (existing) {
            log.info "Existing coupon: ${existing.code}"
            throw new AlreadyExistsException()
        } else {
            // TODO jbr - asynchronous
            log.info "Persisting"
            coupons.put(coupon.code, coupon)
            log.info "Coupon ${coupon.code} added!"
        }
    }

    @Override
    Promise<Coupon> get(String code) throws NotFoundException {
        Blocking.get {
            Coupon existing = coupons.get(code)
            log.info "Exists? " + (existing)
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
    void update(Coupon coupon) throws NotFoundException {
        log.info "Trying to update coupon ${coupon.code}"
        // TODO jbr - coupon validation
        Coupon existing = coupons.get(coupon.code)
        log.info "Exists? " + (existing)
        if (existing) {
            // TODO jbr - asynchronous
            log.info "Persisting"
            coupons.put(coupon.code, coupon)
            log.info "Coupon ${coupon.code} updated!"
        } else {
            log.info "NOT Existing coupon: ${coupon.code}"
            throw new NotFoundException()
        }
    }

    @Override
    void delete(String code) throws NotFoundException {
        log.info "Trying to delete coupon ${code}"
        Coupon existing = coupons.get(code)
        log.info "Exists? " + (existing)
        if (existing) {
            // TODO jbr - asynchronous
            log.info "Deleting"
            coupons.remove(code)
            log.info "Coupon ${code} removed!"
        } else {
            log.info "NOT Existing coupon: ${code}"
            throw new NotFoundException()
        }
    }
}
