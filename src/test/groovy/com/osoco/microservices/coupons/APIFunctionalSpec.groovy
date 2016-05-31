package com.osoco.microservices.coupons

import com.fasterxml.jackson.databind.type.TypeFactory
import com.osoco.microservices.coupons.model.Coupon

class APIFunctionalSpec extends APIApplicationBaseSpec {

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
        def response = post(coupon1)

        then:
        response.statusCode == 200

        when:
        response = get(COUPONS_URL + "/" + coupon1.code)

        and:
        Coupon parsedCoupon = parseAsCoupon(response.body.text)

        then:
        response.statusCode == 200
        coupon1.equals(parsedCoupon)
    }

    void "Adding existing coupon"() {
        setup:
        populateForTesting(coupon1)

        when:
        def response = post(coupon1)

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
        populateForTesting(coupon1)

        when:
        def response = get(COUPONS_URL + "/" + coupon1.code)

        and:
        Coupon parsedCoupon = parseAsCoupon(response.body.text)

        then:
        response.statusCode == 200

        and:
        coupon1.equals(parsedCoupon)
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
        populateForTesting(coupon1)

        when:
        def response = get(COUPONS_URL)

        and:
        List<Coupon> list = parseAsCouponList(response.body.text)

        then:
        response.statusCode == 200

        and:
        list.size() == 1
        list.get(0).equals(coupon1)
    }

    void "Get not empty coupons list, 2 elements"() {
        setup:
        populateForTesting(coupon1)
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
        def response = put(coupon1)

        then:
        response.statusCode == 404
    }

    void "Updating coupon that exists"() {
        setup:
        Coupon coupon = buildCoupon("code3", "name3", "description3", 100, "2016-05-26", 25)
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
        populateForTesting(coupon1)

        when:
        def response = delete(COUPONS_URL + "/" + coupon1.code)

        then:
        response.statusCode == 200
    }

}
