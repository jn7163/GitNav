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

package giuliolodi.gitnav.ui.issue

import android.content.Context
import android.content.Intent
import android.os.Bundle
import giuliolodi.gitnav.R
import giuliolodi.gitnav.ui.base.BaseActivity

/**
 * Created by giulio on 14/11/2017.
 */
class IssueActivity : BaseActivity() {

    private val ISSUE_FRAGMENT_TAG = "ISSUE_FRAGMENT_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.issue_activity)

        var issueFragment: IssueFragment? = supportFragmentManager.findFragmentByTag(ISSUE_FRAGMENT_TAG) as IssueFragment?
        if (issueFragment == null) {
            issueFragment = IssueFragment()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.issue_activity_frame, issueFragment, ISSUE_FRAGMENT_TAG)
                    .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0,0)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, IssueActivity::class.java)
        }
    }

}