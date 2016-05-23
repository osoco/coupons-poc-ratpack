package com.osoco.microservices.coupons

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.ServerBackedApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.Specification
import spock.lang.Unroll

class APIInfracstructureSpec extends Specification {

    ServerBackedApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
    @Delegate
    TestHttpClient client = TestHttpClient.testHttpClient(aut)

    @Unroll
    def "Checking CORS headers in response for '#path'"() {

        client.get(path)

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

        client.get(path)

        expect:
        response.statusCode == statusCode

        where:
        path                            || statusCode
        "/"                             || 404
        "/api/coupons"                  || 200
        "/api/coupons/"                 || 200
        "/api/coupons/1234/"            || 200
        "/api/coupons/1234/validations" || 405
        "/api/coupons/1234/redemptions" || 405
        "/other"                        || 404
    }

    @Unroll
    def "Checking if POST '#path' returns #statusCode http status code"() {

        client.post(path)

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

        client.put(path)

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

        client.delete(path)

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
