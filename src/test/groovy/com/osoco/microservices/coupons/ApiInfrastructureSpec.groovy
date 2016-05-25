package com.osoco.microservices.coupons

import com.osoco.microservices.coupons.model.Coupon
import spock.lang.Unroll

class APIInfrastructureSpec extends APIBaseSpec {

    Coupon coupon1, coupon2

    def setup() {
        coupon1 = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon1)

        coupon2 = buildCoupon("code2", "name2", "description2", 100, "2016/05/26", 25)
        resetRequest()
        setRequestBody(coupon2)
    }

    @Unroll
    def "Checking CORS headers in response for '#path'"() {

        get(path)

        expect:
        response.headers.contains("Access-Control-Allow-Origin") == result
        response.headers.contains("Access-Control-Allow-Headers") == result

        where:
        path                            || result
        "/api/coupons"                  || true
        "/api/coupons/1234"             || true
        "/api/coupons/1234/validations" || true
        "/api/coupons/1234/redemptions" || true
        "/other"                        || true
    }

    @Unroll
    def "Checking if GET '#path' returns #statusCode http status code"() {

        get path

        expect:
        response.statusCode == statusCode

        where:
        path                             || statusCode
        "/"                              || 404
        "/api/coupons"                   || 200
        "/api/coupons/"                  || 200
        "/api/coupons/code1/"            || 200
        "/api/coupons/code1/validations" || 405
        "/api/coupons/code1/redemptions" || 405
        "/other"                         || 404
    }

    @Unroll
    def "Checking if POST '#path' returns #statusCode http status code"() {

        post path

        expect:
        response.statusCode == statusCode

        where:
        path                            || statusCode
        "/"                             || 404
        "/api/coupons"                  || 200
        "/api/coupons/"                 || 200
        "/api/coupons/1234/"            || 405
        "/api/coupons/1234/validations" || 200
        "/api/coupons/1234/redemptions" || 200
        "/other"                        || 404
    }

    @Unroll
    def "Checking if PUT '#path' returns #statusCode http status code"() {
        setup:
        resetRequest()
        setRequestBody(coupon1)

        put(path)

        expect:
        response.statusCode == statusCode

        where:
        path                            || statusCode
        "/"                             || 404
        "/api/coupons"                  || 200
        "/api/coupons/"                 || 200
        "/api/coupons/1234/"            || 405
        "/api/coupons/1234/validations" || 405
        "/api/coupons/1234/redemptions" || 405
        "/other"                        || 404
    }

    @Unroll
    def "Checking if DELETE '#path' returns #statusCode http status code"() {

        delete(path)

        expect:
        response.statusCode == statusCode

        where:
        path                             || statusCode
        "/"                              || 404
        "/api/coupons"                   || 405
        "/api/coupons/"                  || 405
        "/api/coupons/code1/"            || 200
        "/api/coupons/code1/validations" || 405
        "/api/coupons/code1/redemptions" || 405
        "/other"                         || 404
    }


}
