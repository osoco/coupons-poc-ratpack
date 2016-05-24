package com.osoco.microservices.coupons.modules

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.osoco.microservices.coupons.dao.CouponRepository
import com.osoco.microservices.coupons.dao.impl.CouponRepositoryImpl

class CouponModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    CouponRepository couponRepository() {
        return new CouponRepositoryImpl()
    }
}
