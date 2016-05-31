package com.osoco.microservices.coupons.dao

import com.osoco.microservices.coupons.exception.AlreadyExistsException
import com.osoco.microservices.coupons.exception.NotFoundException
import com.osoco.microservices.coupons.exception.ValidationException
import com.osoco.microservices.coupons.model.Coupon
import ratpack.exec.Operation
import ratpack.exec.Promise


interface CouponRepository {

    public static
    final String SCHEMA = "CREATE TABLE coupon(code varchar(10) primary key, name varchar(50) not null, description varchar(255) not null, maxUsage int not null, expiration date not null, discount int not null)"

    Operation add(Coupon coupon) throws AlreadyExistsException, ValidationException

    Promise<Coupon> get(String code) throws NotFoundException

    Promise<List<Coupon>> get()

    Operation update(Coupon coupon) throws NotFoundException, ValidationException

    Operation delete(String code) throws NotFoundException

}
