package com.osoco.microservices.coupons.dao

import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Promise

interface CouponRepository {

    Promise<Coupon> add(Coupon coupon) throws AlreadyExistsException

    Promise<Coupon> get(String code) throws NotFoundException

    Promise<List<Coupon>> get()

    Promise<Coupon> update(Coupon coupon) throws NotFoundException

    Promise<Coupon> delete(String code) throws NotFoundException

}
