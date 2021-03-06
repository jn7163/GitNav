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

package giuliolodi.gitnav.ui.forklist

import giuliolodi.gitnav.di.scope.PerActivity
import giuliolodi.gitnav.ui.base.BaseContract
import org.eclipse.egit.github.core.Repository

/**
 * Created by giulio on 19/09/2017.
 */
interface ForkListContract {

    interface View : BaseContract.View {

        fun showForkList(forkList: List<Repository>)

        fun showLoading()

        fun hideLoading()

        fun showListLoading()

        fun hideListLoading()

        fun showNoForks()

        fun hideNoForks()

        fun showError(error: String)

        fun showNoConnectionError()

        fun intentToRepoActivity(owner: String, name: String)

    }

    @PerActivity
    interface Presenter<V: ForkListContract.View> : BaseContract.Presenter<V> {

        fun subscribe(isNetworkAvailable: Boolean, owner: String?, name: String?)

        fun onLastItemVisible(isNetworkAvailable: Boolean, dy: Int)

        fun onRepoClick(owner: String, name: String)

    }

}