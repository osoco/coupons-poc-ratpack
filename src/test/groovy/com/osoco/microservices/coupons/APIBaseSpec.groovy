package com.osoco.microservices.coupons

import com.fasterxml.jackson.databind.ObjectMapper
import com.osoco.microservices.coupons.model.Coupon
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.http.MediaType
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

class APIBaseSpec extends Specification {

    protected static final String COUPONS_URL = "api/coupons"

    @AutoCleanup
    GroovyRatpackMainApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
    @Delegate
    TestHttpClient client = TestHttpClient.testHttpClient(aut)

    protected ObjectMapper objectMapper = new ObjectMapper()

    protected Coupon buildCoupon(code, name, description, maxUsage, expirationDate, discount) {
        Coupon coupon = [code: code, name: name, description: description, numMaxUsage: maxUsage, expirationDate: expirationDate, discount: discount]
        coupon
    }

    protected def populateForTesting(Coupon coupon) {
        def response = post(coupon)
        assert response.statusCode == 200
    }

    protected def post(Coupon coupon) {
        setRequestBody(coupon)
        post(COUPONS_URL)
    }

    protected void setRequestBody(Coupon coupon) {
        requestSpec { spec ->
            spec.headers.add("Content-Type", MediaType.APPLICATION_JSON)
            spec.body { b ->
                b.text(objectMapper.writeValueAsString(coupon))
            }
        }
    }

}
