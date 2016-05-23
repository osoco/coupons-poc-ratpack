package com.osoco.microservices.coupons

import groovy.json.JsonSlurper
import ratpack.groovy.test.GroovyRatpackMainApplicationUnderTest
import ratpack.test.http.TestHttpClient
import spock.lang.AutoCleanup
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson

class APIFunctionalSpec extends Specification {


    private static final String COUPONS_URL = "api/coupons"
    private static final JsonSlurper jsonSlurper = new JsonSlurper()

    @AutoCleanup
    GroovyRatpackMainApplicationUnderTest aut = new GroovyRatpackMainApplicationUnderTest()
    @Delegate
    TestHttpClient client = TestHttpClient.testHttpClient(aut)

    void "POSTing new coupon"() {
        setup:
        def coupon = [code: "testCode", name: "testName", description: "testDescription", numMaxUsage: 100, expirationDate: "2016/06/01", discount: 10]

        when:
        def response = client.requestSpec { spec ->
            spec.body { b ->
                b.text(toJson(coupon))
            }
        }.post(COUPONS_URL)

        then:
        response.statusCode == 200

//        when:
//        def json = client.get(COUPONS_URL+'/'+coupon.code).body.text
//
//        and:
//        def coupons = jsonSlurper.parseText(json) as List
//
//        then:
//        coupons == [coupon]
    }


}
