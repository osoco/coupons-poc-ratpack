package com.osoco.microservices.coupons.model

import groovy.transform.EqualsAndHashCode
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@EqualsAndHashCode(includeFields = true)
class Coupon {

    @NotEmpty
    String code

    @NotEmpty
    String name

    @NotEmpty
    String description

    @NotNull
    @Min(value = 0L)
    @Max(value = 100L)
    Integer numMaxUsage

    @NotEmpty
    String expirationDate

    @NotNull
    @Min(value = 0L)
    @Max(value = 100L)
    Integer discount
}
