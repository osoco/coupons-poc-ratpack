package com.osoco.microservices.coupons

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.osoco.microservices.coupons.model.Coupon
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.http.MediaType
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

class APIFunctionalSpec extends Specification {


    private static final String COUPONS_URL = "api/coupons"

    @AutoCleanup
    GroovyRatpackMainApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
    @Delegate
    TestHttpClient client = TestHttpClient.testHttpClient(aut)

    ObjectMapper objectMapper = new ObjectMapper()

    def setup() {
        resetRequest()
    }

    Coupon buildCoupon(code, name, description, maxUsage, expirationDate, discount) {
        Coupon coupon = [code: code, name: name, description: description, numMaxUsage: maxUsage, expirationDate: expirationDate, discount: discount]
        coupon
    }

    def post(Coupon coupon) {
        requestSpec { spec ->
            spec.headers.add("Content-Type", MediaType.APPLICATION_JSON)
            spec.body { b ->
                b.text(objectMapper.writeValueAsString(coupon))
            }
        }
        post(COUPONS_URL)
    }

    def populateForTesting(Coupon coupon) {
        def response = post(coupon)
        assert response.statusCode == 200
    }

    def parseAsCouponList(String text) {
        objectMapper.readValue(text, TypeFactory.defaultInstance().constructCollectionType(List.class, Coupon.class))
    }

    def parseAsCoupon(String text) {
        objectMapper.readValue(text, Coupon.class)
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


}
