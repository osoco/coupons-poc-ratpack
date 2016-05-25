package com.osoco.microservices.coupons.dao

import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Promise

interface CouponRepository {

    void add(Coupon coupon) throws AlreadyExistsException

    Promise<Coupon> get(String code) throws NotFoundException

    Promise<List<Coupon>> get()

    void update(Coupon coupon) throws NotFoundException

    void delete(String code) throws NotFoundException

}
