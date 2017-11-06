/*
 * Copyright 2017 GLodi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package giuliolodi.gitnav

import android.app.Application
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import giuliolodi.gitnav.di.component.AppComponent
import giuliolodi.gitnav.di.component.DaggerAppComponent
import giuliolodi.gitnav.di.module.AppModule
import com.google.firebase.crash.FirebaseCrash
import giuliolodi.gitnav.di.module.NetModule
import timber.log.Timber

/**
 * Created by giulio on 12/05/2017.
 */
class App : Application() {

    lateinit var mAppComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        mAppComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .netModule(NetModule("https://github.com/"))
                .build()

        mAppComponent.inject(this)

        Timber.plant(if (BuildConfig.DEBUG)
            Timber.DebugTree()
        else
            CrashReportingTree())

        FirebaseAnalytics.getInstance(this)
    }

    fun getAppComponent(): AppComponent {
        return mAppComponent
    }

    private inner class CrashReportingTree : Timber.Tree() {
        override fun log(priority: Int, tag: String, message: String, throwable: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            val t = throwable ?: Exception(message)
            FirebaseCrash.logcat(priority, tag, message)
            FirebaseCrash.report(t)
        }
    }

}
