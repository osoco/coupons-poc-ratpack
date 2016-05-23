package com.osoco.microservices.coupons.handlers

import ratpack.handling.Context
import ratpack.handling.Handler
import ratpack.http.MutableHeaders

class CORSHandler implements Handler {
    @Override
    void handle(Context ctx) throws Exception {
        MutableHeaders headers = ctx.getResponse().getHeaders()
        headers.set("Access-Control-Allow-Origin", "*")
        headers.set("Access-Control-Allow-Headers", "x-requested-with, origin, content-type, accept")
        ctx.next()
    }
}
