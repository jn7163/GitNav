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

package giuliolodi.gitnav.data

import android.content.Context
import giuliolodi.gitnav.di.scope.AppContext
import giuliolodi.gitnav.data.api.ApiHelper
import giuliolodi.gitnav.data.prefs.PrefsHelper
import io.reactivex.Completable
import io.reactivex.Observable
import org.eclipse.egit.github.core.event.Event
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by giulio on 12/05/2017.
 */

@Singleton
class DataManagerImpl : DataManager {

    private val TAG = "DataManager"

    private val mContext: Context
    private val mApiHelper: ApiHelper
    private val mPrefsHelper: PrefsHelper

    @Inject
    constructor(@AppContext context: Context, apiHelper: ApiHelper, prefsHelper: PrefsHelper) {
        mContext = context
        mApiHelper = apiHelper
        mPrefsHelper = prefsHelper
    }

    override fun tryAuthentication(user: String, pass: String): Completable {
        return Completable.fromAction {
            val token: String
            try {
                token = apiAuthToGitHub(user, pass)
            } catch (e: IOException) {
                throw e
            }
            if (!token.isEmpty()) {
                mPrefsHelper.storeAccessToken(token)
            }
        }
    }

    override fun downloadEvents(pageN: Int, itemsPerPage: Int): Observable<List<Event>> {
        return apiDownloadEvents(mPrefsHelper.getToken(), pageN, itemsPerPage)
    }

    override fun storeAccessToken(token: String) {
        mPrefsHelper.storeAccessToken(token)
    }

    override fun getToken(): String {
        return mPrefsHelper.getToken()
    }

    override fun apiAuthToGitHub(user: String, pass: String): String {
        return mApiHelper.apiAuthToGitHub(user, pass)
    }

    override fun apiDownloadEvents(token: String, pageN: Int, itemsPerPage: Int): Observable<List<Event>> {
        return mApiHelper.apiDownloadEvents(token, pageN, itemsPerPage)
    }

}