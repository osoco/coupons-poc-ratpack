package com.osoco.microservices.coupons.handlers

import groovy.util.logging.Slf4j
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context
import ratpack.http.Status

@Slf4j
class ErrorHandler implements ServerErrorHandler {
    @Override
    void error(Context context, Throwable exception) throws Exception {
        log.error "Error raised: ", exception

        if (exception.code) {
            context.response.status(Status.of(exception.code)).send()
        } else {
            context.next()
        }
    }
}
