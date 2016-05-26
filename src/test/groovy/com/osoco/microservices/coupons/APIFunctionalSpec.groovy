package com.osoco.microservices.coupons

import com.fasterxml.jackson.databind.type.TypeFactory
import com.osoco.microservices.coupons.model.Coupon

class APIFunctionalSpec extends APIBaseSpec {

    def setup() {
        resetRequest()
    }

    def put(Coupon coupon) {
        setRequestBody(coupon)
        put(COUPONS_URL)
    }

    def parseAsCouponList(String text) {
        objectMapper.readValue(text, TypeFactory.defaultInstance().constructCollectionType(List.class, Coupon.class))
    }

    def parseAsCoupon(String text) {
        objectMapper.readValue(text, Coupon.class)
    }

    private void setAuthHeader() {
        requestSpec { spec ->
            addHeader(spec.headers, "x-auth-header", "Test")
        }
    }

    void "Adding new coupon"() {
        when:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)

        and:
        def response = post(coupon)

        then:
        response.statusCode == 200

        when:
        response = get(COUPONS_URL + "/" + coupon.code)

        and:
        Coupon parsedCoupon = parseAsCoupon(response.body.text)

        then:
        response.statusCode == 200
        coupon.equals(parsedCoupon)
    }

    void "Adding existing coupon"() {
        setup:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon)

        when:
        def response = post(coupon)

        then:
        response.statusCode == 409
    }

    void "Get not existing coupon"() {
        setup:
        setAuthHeader()

        when:
        def response = get(COUPONS_URL + "/1234")

        then:
        response.statusCode == 404
    }

    void "Get existing coupon"() {
        setup:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon)

        when:
        def response = get(COUPONS_URL + "/" + coupon.code)

        and:
        Coupon parsedCoupon = parseAsCoupon(response.body.text)

        then:
        response.statusCode == 200

        and:
        coupon.equals(parsedCoupon)
    }

    void "Get empty coupons list"() {
        setup:
        setAuthHeader()

        when:
        def response = get(COUPONS_URL)

        and:
        List<Coupon> list = parseAsCouponList(response.body.text)

        then:
        response.statusCode == 200

        and:
        list.isEmpty()
    }

    void "Get not empty coupons list"() {
        setup:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon)

        when:
        def response = get(COUPONS_URL)

        and:
        List<Coupon> list = parseAsCouponList(response.body.text)

        then:
        response.statusCode == 200

        and:
        list.size() == 1
        list.get(0).equals(coupon)
    }

    void "Get not empty coupons list, 2 elements"() {
        setup:
        Coupon coupon1 = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon1)

        Coupon coupon2 = buildCoupon("code2", "name2", "description2", 100, "2016/05/26", 25)
        populateForTesting(coupon2)

        when:
        def response = get(COUPONS_URL)

        and:
        List<Coupon> list = parseAsCouponList(response.body.text)

        then:
        response.statusCode == 200

        and:
        list.size() == 2
        list.get(0).equals(coupon1) || list.get(0).equals(coupon2)
        list.get(1).equals(coupon1) || list.get(1).equals(coupon2)
    }

    void "Updating coupon that doesn't exist"() {
        when:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)

        and:
        def response = put(coupon)

        then:
        response.statusCode == 404
    }

    void "Updating coupon that exists"() {
        setup:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon)

        when:
        coupon.name = "updatedName"

        and:
        def response = put(coupon)

        then:
        response.statusCode == 200

        when:
        response = get(COUPONS_URL + "/" + coupon.code)

        and:
        Coupon parsedCoupon = parseAsCoupon(response.body.text)

        then:
        response.statusCode == 200
        coupon.equals(parsedCoupon)
    }

    void "Delete coupon that doesn't exist"() {
        setup:
        setAuthHeader()

        when:
        def response = delete(COUPONS_URL + "/doesntExist")

        then:
        response.statusCode == 404
    }

    void "Delete coupon that exists"() {
        setup:
        Coupon coupon = buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
        populateForTesting(coupon)

        when:
        def response = delete(COUPONS_URL + "/" + coupon.code)

        then:
        response.statusCode == 200
    }

}
