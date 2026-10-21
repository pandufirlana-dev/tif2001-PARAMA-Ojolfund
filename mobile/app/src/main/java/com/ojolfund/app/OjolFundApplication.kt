package com.ojolfund.app

import android.app.Application
import com.ojolfund.app.di.AppContainer

class OjolFundApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
