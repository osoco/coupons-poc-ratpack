import com.osoco.microservices.coupons.handlers.CORSHandler
import com.osoco.microservices.coupons.handlers.CouponsHandler
import com.osoco.microservices.coupons.handlers.RedemptionsHandler
import com.osoco.microservices.coupons.handlers.ValidationsHandler

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add(new CORSHandler())
        add(new CouponsHandler())
        add(new RedemptionsHandler())
        add(new ValidationsHandler())
    }

    handlers {
        all(CORSHandler)
        prefix("api") {
            path("coupons") {
                byMethod {
                    get {
                        render "GET simple"
                    }
                    post {
                        render "POST simple"
                    }
                    put {
                        render "PUT simple"
                    }
                }
            }
            path("coupons/:code") {
                byMethod {
                    get {
                        render "GET with coupon $pathTokens.code"
                    }
                    delete { ctx ->
                        render "DELETE with coupon $pathTokens.code"
                    }
                }
            }
            path("coupons/:code/validations", ValidationsHandler)
            path("coupons/:code/redemptions", RedemptionsHandler)
        }

        files { dir "public" }
    }
}
