package com.osoco.microservices.coupons

import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.dao.impl.CouponServiceImpl
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.ExecResult
import ratpack.exec.Promise
import ratpack.test.exec.ExecHarness
import spock.lang.AutoCleanup
import spock.lang.Specification

class CouponServiceSpec extends Specification {

    @AutoCleanup
    ExecHarness execHarness = ExecHarness.harness()
    CouponService service = new CouponServiceImpl()

    Coupon buildCoupon(code, name) {
        new Coupon(code: code, name: name)
    }

    private ExecResult<Coupon> addCouponForTesting() {
        execHarness.yield { service.add(buildCoupon("testCode", "testName")) }
    }

    def 'add coupons to service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [buildCoupon("testCode", "testName")]
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
        coupons == [buildCoupon("testCode", "testName")]
    }

    def 'retrieve coupon from service, by existing code'() {
        when:
        addCouponForTesting()

        and:
        Coupon coupon = execHarness.yield { service.get("testCode") }.value

        then:
        coupon == buildCoupon("testCode", "testName")
    }

    def 'retrieve coupon from service, by not existing code'() {
        when:
        Exception ex
        try {
            execHarness.yield { service.get("testCode") }.valueOrThrow
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
        coupons == [buildCoupon("testCode", "testName")]

        when:
        execHarness.yield { service.delete('testCode') }

        then:
        execHarness.yield { service.get() }.value == []
    }

    def 'update coupons in service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [buildCoupon("testCode", "testName")]

        when:
        execHarness.yield { service.update(buildCoupon("testCode", "testName2")) }
        and:
        coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [buildCoupon("testCode", "testName2")]
    }

}
