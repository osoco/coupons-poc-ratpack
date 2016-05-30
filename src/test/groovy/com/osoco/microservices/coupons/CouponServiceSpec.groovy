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

import javax.validation.Validation

class CouponServiceSpec extends Specification {

    @AutoCleanup
    ExecHarness execHarness = ExecHarness.harness()
    CouponService service = new CouponServiceImpl(Validation.buildDefaultValidatorFactory().validator)

    private ExecResult<Coupon> addCouponForTesting() {
        execHarness.execute {
            service.add(APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25))
        }
    }

    def 'add coupons to service'() {
        when:
        addCouponForTesting()

        and:
        List<Coupon> coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)]
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
        coupons == [APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)]
    }

    def 'retrieve coupon from service, by existing code'() {
        when:
        addCouponForTesting()

        and:
        Coupon coupon = execHarness.yield { service.get("code1") }.value

        then:
        coupon == APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)
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
        coupons == [APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)]

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
        coupons == [APIBaseSpec.buildCoupon("code1", "name1", "description1", 100, "2016/05/26", 25)]

        when:
        execHarness.execute {
            service.update(APIBaseSpec.buildCoupon("code1", "name2", "description1", 100, "2016/05/26", 25))
        }
        and:
        coupons = execHarness.yield { service.get() }.value

        then:
        coupons == [APIBaseSpec.buildCoupon("code1", "name2", "description1", 100, "2016/05/26", 25)]
    }

}
