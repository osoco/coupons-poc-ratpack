package com.osoco.microservices.coupons

import com.osoco.microservices.coupons.handlers.AuthHandler
import ratpack.groovy.test.handling.GroovyRequestFixture
import ratpack.test.handling.HandlingResult
import spock.lang.Specification
import spock.lang.Unroll

class AuthHandlingSpec extends Specification {

    @Unroll
    def 'With #authHeader and #value, request is authorized? #authorized'() {
        when:
        HandlingResult result = GroovyRequestFixture.handle(new AuthHandler(), { header(authHeader, value) })

        then:
        (result.status.code == 401) != authorized

        where:
        authHeader      | value        | authorized
        'WrongHeader'   | 'WrongValue' | false
        'WrongHeader'   | 'Test'       | false
        ''              | 'WrongValue' | false
        ''              | 'Test'       | false
        'x-auth-header' | 'WrongValue' | false
        'x-auth-header' | 'Test'       | true
    }
}
