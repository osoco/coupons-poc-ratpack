package com.osoco.microservices.coupons

import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.http.MediaType
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson

class APIFunctionalSpec extends Specification {


    private static final String COUPONS_URL = "api/coupons"

    @AutoCleanup
    GroovyRatpackMainApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
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

    void "POSTing new coupon"() {
        when:
        def response = post(COUPONS_URL)

        then:
        response.statusCode == 200
    }


}
