package com.osoco.microservices.coupons.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.osoco.microservices.coupons.dao.CouponService
import com.osoco.microservices.coupons.dao.impl.CouponServiceImpl

class CouponModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    CouponService couponRepository() {
        return new CouponServiceImpl()
    }
}
