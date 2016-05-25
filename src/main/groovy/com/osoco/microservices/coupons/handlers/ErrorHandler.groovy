package com.osoco.microservices.coupons.handlers

import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import groovy.util.logging.Slf4j
import ratpack.error.ServerErrorHandler
import ratpack.handling.Context
import ratpack.http.Status

@Slf4j
class ErrorHandler implements ServerErrorHandler {
    @Override
    void error(Context context, Throwable throwable) throws Exception {
        log.error "Error raised: ", throwable
        // TODO jbr - Improve this
        if (throwable instanceof AlreadyExistsException) {
            context.response.status(Status.of(409)).send()
        } else if (throwable instanceof NotFoundException) {
            context.response.status(Status.of(404)).send()
        } else {
            context.next()
        }
    }
}
