import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.dao.impl.CouponServiceImpl
import com.osoco.microservices.coupons.handlers.*
import ratpack.error.ServerErrorHandler
import ratpack.groovy.sql.SqlModule
import ratpack.handling.RequestLogger
import ratpack.hikari.HikariModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add(RequestLogger.ncsa())
        add(new AuthHandler())
        add(new CORSHandler())
        add(new CouponHandler())
        add(new RedemptionHandler())
        add(new ValidationHandler())

        module(SqlModule)
        module(HikariModule) { c ->
            c.dataSourceClassName = 'org.postgresql.ds.PGSimpleDataSource'
            c.addDataSourceProperty 'serverName', 'localhost'
            c.addDataSourceProperty 'databaseName', 'microservice'
            c.addDataSourceProperty 'user', 'postgres'
            c.addDataSourceProperty 'password', 'postgres'

            c.username = 'postgres'
            c.password = 'postgres'
        }

        bind(CouponService, CouponServiceImpl)
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
