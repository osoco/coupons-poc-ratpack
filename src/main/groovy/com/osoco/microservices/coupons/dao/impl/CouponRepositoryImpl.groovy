package com.osoco.microservices.coupons.dao.impl

import com.google.inject.Inject
import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.exception.ValidationException
import com.osoco.microservices.coupons.model.Coupon
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Operation
import ratpack.exec.Promise

import javax.validation.ConstraintViolation
import javax.validation.Validator

@Slf4j
class CouponRepositoryImpl implements CouponRepository {

    private Sql sql
    private Validator validator

    @Inject
    CouponRepositoryImpl(Sql sql, Validator validator) {
        this.sql = sql
        this.validator = validator
    }

    @Override
    Operation add(Coupon coupon) throws AlreadyExistsException, ValidationException {
        log.info "Storing coupon $coupon.code"

        validate(coupon)

        Blocking.get {
            Coupon existingCoupon
            try {
                existingCoupon = internalGet(coupon.code)
            } catch (NotFoundException nfe) {
            }

            if (!existingCoupon) {
                sql.execute "INSERT INTO coupon (code,name,description,maxusage,expiration,discount) " +
                        "VALUES('$coupon.code', '$coupon.name', '$coupon.description', $coupon.numMaxUsage, '$coupon.expirationDate', $coupon.discount)"
                log.info "Coupon $coupon.code added!"
            } else {
                throw new AlreadyExistsException()
            }
        }.operation()
    }

    @Override
    Promise<Coupon> get(String code) throws NotFoundException {
        log.info "Getting coupon with code: $code"
        Blocking.get {
            internalGet(code)
        }
    }

    private Coupon internalGet(String code) {
        def existing = sql.firstRow("select * from coupon where code=$code")
        if (existing) {
            new Coupon(code: existing.code, name: existing.name, description: existing.description, numMaxUsage: existing.maxusage, expirationDate: existing.expiration, discount: existing.discount)
        } else {
            throw new NotFoundException()
        }
    }

    @Override
    Promise<List<Coupon>> get() {
        Blocking.get {
            sql.rows("select * from coupon").collect {
                new Coupon(code: it.code, name: it.name, description: it.description, numMaxUsage: it.maxusage, expirationDate: it.expiration, discount: it.discount)
            }
        }
    }

    @Override
    Operation update(Coupon coupon) throws NotFoundException, ValidationException {
        log.info "Updating coupon $coupon.code"

        validate(coupon)

        Blocking.get {
            Coupon existing = internalGet(coupon.code)
            sql.execute "update coupon set name='$coupon.name', description='$coupon.description', maxusage=$coupon.numMaxUsage, expiration='$coupon.expirationDate', discount=$coupon.discount where code='$existing.code'"
            log.info "Coupon $coupon.code updated!"
        }.operation()
    }

    @Override
    Operation delete(String code) throws NotFoundException {
        log.info "Deleting coupon $code"
        Blocking.get {
            Coupon existing = internalGet(code)
            sql.execute "delete from coupon where code=$existing.code"
            log.info "Coupon $code deleted!"
        }.operation()
    }

    private void validate(Coupon coupon) throws ValidationException {
        final Set<ConstraintViolation<Coupon>> constraintViolations = validator.validate(coupon)
        if (constraintViolations.size() > 0) {
            throw new ValidationException()
        }
    }

}
