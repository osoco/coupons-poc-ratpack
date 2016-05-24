import com.osoco.microservices.coupons.handlers.CORSHandler
import com.osoco.microservices.coupons.handlers.CouponHandler
import com.osoco.microservices.coupons.handlers.RedemptionHandler
import com.osoco.microservices.coupons.handlers.ValidationHandler
import com.osoco.microservices.coupons.modules.CouponModule
import ratpack.handling.RequestLogger

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add(RequestLogger.ncsa())
        add(new CORSHandler())
        add(new CouponHandler())
        add(new RedemptionHandler())
        add(new ValidationHandler())

        module(CouponModule.class)
    }

    handlers {
        all(RequestLogger)
        all(CORSHandler)
        prefix("api") {
            path("coupons", CouponHandler)
            path("coupons/:code", CouponHandler)
            path("coupons/:code/validations", ValidationHandler)
            path("coupons/:code/redemptions", RedemptionHandler)
        }

        files { dir "public" }
    }
}
