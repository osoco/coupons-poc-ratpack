import com.osoco.microservices.coupons.config.DbConfig
import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.dao.impl.CouponRepositoryImpl
import com.osoco.microservices.coupons.handlers.*
import ratpack.config.ConfigData
import ratpack.error.ServerErrorHandler
import ratpack.groovy.sql.SqlModule
import ratpack.handling.RequestLogger
import ratpack.hikari.HikariModule
import ratpack.service.Service
import ratpack.service.StartEvent

import javax.sql.DataSource
import javax.validation.Validation
import javax.validation.Validator
import java.sql.Connection

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        def configData = ConfigData.of { d ->
            d.props("$serverConfig.baseDir.file/application.properties")
                    .sysProps("msc.")
                    .env("MSC_")
                    .build()
        }

        bindInstance(Validator, Validation.buildDefaultValidatorFactory().validator)

        add(RequestLogger.ncsa())
        add(new AuthHandler())
        add(new CORSHandler())
        add(new CouponHandler())
        add(new RedemptionHandler())
        add(new ValidationHandler())

        module(SqlModule)
        module(HikariModule) { hikariConfig ->
            DbConfig dbConfig = configData.get("/db", DbConfig)
            hikariConfig.dataSourceClassName = 'org.h2.jdbcx.JdbcDataSource'
            hikariConfig.username = dbConfig.username
            hikariConfig.password = dbConfig.password

            hikariConfig.addDataSourceProperty 'URL', dbConfig.buildUrl()
        }

        bind(CouponRepository, CouponRepositoryImpl)
        bind(ServerErrorHandler, ErrorHandler)

        bindInstance(new Service() {
            @Override
            public void onStart(StartEvent event) throws Exception {
                DataSource dataSource = event.getRegistry().get(DataSource.class)
                Connection connection = dataSource.getConnection()
                connection.createStatement()
                        .execute(CouponRepository.SCHEMA)
            }
        })
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
    }
}
