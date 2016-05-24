package com.osoco.microservices.coupons.dao.impl

import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.model.Coupon
import groovy.util.logging.Slf4j
import ratpack.exec.Blocking
import ratpack.exec.Promise

@Slf4j
class CouponRepositoryImpl implements CouponRepository {

    Map<String, Coupon> coupons = new HashMap<String, Coupon>()

    @Override
    void add(Coupon coupon) {
        Coupon existing = coupons.get(coupon.code)
        if (existing) {
            // TODO jbr - Return exception or sth
            log.info("Exising coupon: $coupon.code")
        } else {
            Blocking.op {
                coupons.put(coupon.code, coupon)
            }
        }
    }
}
