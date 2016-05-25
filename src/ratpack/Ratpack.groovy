import com.osoco.microservices.coupons.handlers.*
import com.osoco.microservices.coupons.modules.CouponModule
import ratpack.error.ServerErrorHandler
import ratpack.handling.RequestLogger

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add(RequestLogger.ncsa())
        add(new AuthHandler())
        add(new CORSHandler())
        add(new CouponHandler())
        add(new RedemptionHandler())
        add(new ValidationHandler())

        module(CouponModule)

        bind(ServerErrorHandler, ErrorHandler)
    }

    handlers {
        all(AuthHandler)
        all(CORSHandler)
        all(RequestLogger)
        prefix("api") {
            path("coupons", CouponHandler)
            path("coupons/:code", CouponHandler)
            path("coupons/:code/validations", ValidationHandler)
            path("coupons/:code/redemptions", RedemptionHandler)
        }

        files { dir "public" }
    }
}
