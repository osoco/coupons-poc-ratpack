package com.osoco.microservices.coupons

import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.dao.impl.CouponRepositoryImpl
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import groovy.sql.Sql
import ratpack.exec.ExecResult
import ratpack.exec.Promise
import ratpack.test.exec.ExecHarness
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.validation.Validation

class CouponServiceSpec extends APIBaseSpec {

    @AutoCleanup
    ExecHarness execHarness = ExecHarness.harness()
    @Shared
    CouponRepository service
    @Shared
    Sql sql

    def setupSpec() {
        sql = Sql.newInstance("jdbc:h2:mem:test")
        sql.execute(CouponRepository.SCHEMA)
        service = new CouponRepositoryImpl(sql, Validation.buildDefaultValidatorFactory().validator)
    }

    def cleanupSpec() {
        sql.execute("DROP TABLE coupon")
    }

    def cleanup() {
        sql.execute("DELETE FROM coupon")
    }

    private ExecResult<Coupon> addCouponForTesting() {
        execHarness.execute {
            service.add(coupon1)
        }
    }

    def 'add coupons to service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [coupon1]
    }

    def 'retrieve coupons from service, empty list'() {
        when:
        ExecResult<Promise<List<Coupon>>> result = execHarness.yield { service.get() }

        then:
        result.value == []
    }

    def 'retrieve coupons from service, not empty list'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [coupon1]
    }

    def 'retrieve coupon from service, by existing code'() {
        when:
        addCouponForTesting()

        and:
        Coupon coupon = execHarness.yield { service.get("code1") }.value

        then:
        coupon == coupon1
    }

    def 'retrieve coupon from service, by not existing code'() {
        when:
        Exception ex
        try {
            execHarness.yield { service.get("code1") }.valueOrThrow
        } catch (NotFoundException nfe) {
            ex = nfe
        }

        then:
        ex ? ex instanceof NotFoundException : false
    }

    def 'delete coupons from service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [coupon1]

        when:
        execHarness.execute { service.delete('code1') }

        then:
        execHarness.yield { service.get() }.value == []
    }

    def 'update coupons in service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [coupon1]

        when:
        Coupon existingCoupon = coupons[0]
        existingCoupon.name = 'NameChanged'
        execHarness.execute {
            service.update(existingCoupon)
        }
        and:
        coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [existingCoupon]
    }

}
