package com.osoco.microservices.coupons.model

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includeFields = true)
class Coupon {

    String code
    String name
    String description
    Integer numMaxUsage
    String expirationDate
    Integer discount
}
