package com.osoco.microservices.coupons

import com.osoco.microservices.coupons.model.Coupon
import spock.lang.Shared
import spock.lang.Specification

class BaseSpec extends Specification {

    @Shared
    Coupon coupon1, coupon2

    def setupSpec() {
        coupon1 = buildCoupon("code1", "name1", "description1", 100, "2016-05-26", 25)
        coupon2 = buildCoupon("code2", "name2", "description2", 100, "2016-05-26", 25)
    }

    protected static Coupon buildCoupon(code, name, description, maxUsage, expirationDate, discount) {
        Coupon coupon = [code: code, name: name, description: description, numMaxUsage: maxUsage, expirationDate: expirationDate, discount: discount]
        coupon
    }


}
