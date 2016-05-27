package com.osoco.microservices.coupons.dao.impl

import com.google.inject.Inject
import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Operation
import ratpack.exec.Promise

import javax.validation.ConstraintViolation
import javax.validation.ValidationException
import javax.validation.Validator

@Slf4j
class CouponServiceImpl implements CouponService {

    Map<String, Coupon> coupons = new HashMap<String, Coupon>()

    private Validator validator

    @Inject
    CouponServiceImpl(Validator validator) {
        this.validator = validator
    }

    @Override
    Operation add(Coupon coupon) throws AlreadyExistsException, ValidationException {
        log.info "Storing coupon $coupon.code"

        validate(coupon)

        Operation.of {
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
    Operation update(Coupon coupon) throws NotFoundException {
        log.info "Updating coupon $coupon.code"

        validate(coupon)

        Operation.of {
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
    Operation delete(String code) throws NotFoundException {
        log.info "Deleting coupon $code"
        Operation.of {
            Coupon existing = coupons.get(code)
            if (existing) {
                log.info "Coupon $code removed!"
                coupons.remove(code)
            } else {
                throw new NotFoundException()
            }
        }
    }

    private void validate(Coupon coupon) throws ValidationException {
        final Set<ConstraintViolation<Coupon>> constraintViolations = validator.validate(coupon)
        if (constraintViolations.size() > 0) {
            throw new ValidationException()
        }
    }

}
