package com.osoco.microservices.coupons.config

class DbConfig {

    String host
    String name
    String username
    String password
    Boolean inMemory

    String buildUrl() {
        if (inMemory) {
            "jdbc:h2:mem:${name}"
        } else {
            "jdbc:h2:${host}~/${name}"
        }

    }
}
