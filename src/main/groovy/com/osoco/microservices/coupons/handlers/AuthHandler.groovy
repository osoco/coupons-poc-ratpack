package com.osoco.microservices.coupons.handlers

import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.Status

class AuthHandler implements Handler {

    private static final String AUTH_HEADER = "x-auth-header"

    @Override
    void handle(Context ctx) throws Exception {
        String header = ctx.request.headers.get(AUTH_HEADER)
        if (!header || !header.equals("Test")) {
            ctx.response.status(Status.of(401)).send()
        } else {
            ctx.next()
        }
    }
}
