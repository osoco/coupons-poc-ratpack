package com.osoco.microservices.coupons.dao.impl

import com.google.inject.Inject
import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Operation
import ratpack.exec.Promise

@Slf4j
class CouponServiceImpl implements CouponService {

    private Sql sql

    @Inject
    CouponServiceImpl(Sql sql) {
        this.sql = sql
    }

    @Override
    Operation add(Coupon coupon) throws AlreadyExistsException {
        log.info "Storing coupon $coupon.code"
        // TODO jbr - coupon validation
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
    Operation update(Coupon coupon) throws NotFoundException {
        log.info "Updating coupon $coupon.code"
        // TODO jbr - coupon validation
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
}
