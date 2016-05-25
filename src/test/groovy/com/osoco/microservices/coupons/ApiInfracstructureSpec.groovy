package com.osoco.microservices.coupons

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.http.MediaType
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Unroll

import static groovy.json.JsonOutput.toJson

class APIInfracstructureSpec extends Specification {

    @AutoCleanup
    ServerBackedApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
    @Delegate
    TestHttpClient client = TestHttpClient.testHttpClient(aut)

    def setup() {
        def coupon = [code: "testCode", name: "testName", description: "testDescription", numMaxUsage: 100, expirationDate: "2016/06/01", discount: 10]

        resetRequest()
        requestSpec { spec ->
            spec.headers.add("Content-Type", MediaType.APPLICATION_JSON)
            spec.body { b ->
                b.text(toJson(coupon))
            }
        }
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

        get(path)

        expect:
        response.statusCode == statusCode

        where:
        path                            || statusCode
        "/"                             || 404
        "/api/coupons"                  || 200
        "/api/coupons/"                 || 200
        "/api/coupons/1234/"            || 404
        "/api/coupons/1234/validations" || 405
        "/api/coupons/1234/redemptions" || 405
        "/other"                        || 404
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
        path                            || statusCode
        "/"                             || 404
        "/api/coupons"                  || 405
        "/api/coupons/"                 || 405
        "/api/coupons/1234/"            || 200
        "/api/coupons/1234/validations" || 405
        "/api/coupons/1234/redemptions" || 405
        "/other"                        || 404
    }


}
