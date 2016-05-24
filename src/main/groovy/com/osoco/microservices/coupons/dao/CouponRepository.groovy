package com.osoco.microservices.coupons.dao

import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Promise

interface CouponRepository {

    void add(Coupon coupon)

}
