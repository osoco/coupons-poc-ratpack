package com.osoco.microservices.coupons.dao

import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Operation
import ratpack.exec.Promise

import javax.validation.ValidationException

interface CouponService {

    Operation add(Coupon coupon) throws AlreadyExistsException, ValidationException

    Promise<Coupon> get(String code) throws NotFoundException

    Promise<List<Coupon>> get()

    Operation update(Coupon coupon) throws NotFoundException, ValidationException

    Operation delete(String code) throws NotFoundException

}
