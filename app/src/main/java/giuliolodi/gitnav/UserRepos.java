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

package giuliolodi.gitnav;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vstechlab.easyfonts.EasyFonts;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindString;
import es.dmoral.toasty.Toasty;
import giuliolodi.gitnav.Adapters.RepoAdapter;

public class UserRepos {

    private List<Repository> repositoryList, repositoryListTemp;
    private List<Repository> t;
    private Context context;
    private String user;
    private RepoAdapter repoAdapter;
    private View v;
    private RecyclerView rv;
    private Map FILTER_OPTION;
    private RepositoryService repositoryService;
    private LinearLayoutManager mLayoutManager;
    private TextView noRepos;

    // Number of page that we have currently downloaded. Starts at 1
    private int DOWNLOAD_PAGE_N = 1;

    // Number of items downloaded per page
    private int ITEMS_DOWNLOADED_PER_PAGE = 10;

    // Flag that prevents multiple pages from being downloaded at the same time
    private boolean LOADING = false;

    private boolean NO_MORE = true;

    @BindString(R.string.network_error) String network_error;

    /*
        Populate() is called when a UserActivity is created. This is
        the first of three classes that populate the fragments below UserActivity:
        Repos, Followers and Following.
     */
    public void populate(String user, Context context, View v) {
        this.user = user;
        this.context = context;
        this.v = v;
        FILTER_OPTION = new HashMap();
        FILTER_OPTION.put("sort", "created");
        if (Constants.isNetworkAvailable(context))
            new getRepos().execute();
        else
            Toasty.warning(context, network_error, Toast.LENGTH_LONG).show();
    }

    private class getRepos extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... params) {
            repositoryService = new RepositoryService();
            repositoryService.getClient().setOAuth2Token(Constants.getToken(context));

            repositoryList = new ArrayList<>(repositoryService.pageRepositories(user, FILTER_OPTION, DOWNLOAD_PAGE_N, ITEMS_DOWNLOADED_PER_PAGE).next());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            noRepos = (TextView) v.findViewById(R.id.user_repos_tv);
            noRepos.setTypeface(EasyFonts.robotoRegular(context));

            if (repositoryList.isEmpty())
                noRepos.setVisibility(View.VISIBLE);

            repoAdapter = new RepoAdapter(repositoryList, context);
            mLayoutManager = new LinearLayoutManager(context);
            rv = (RecyclerView) v.findViewById(R.id.user_repos_rv);
            rv.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
            rv.setLayoutManager(mLayoutManager);
            rv.setItemAnimator(new DefaultItemAnimator());

            setupOnScrollListener();

            rv.setAdapter(repoAdapter);
            repoAdapter.notifyDataSetChanged();

        }
    }

    /*
       This will allow the recyclerview to load more content as the user scrolls down
    */
    private void setupOnScrollListener() {

        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (LOADING)
                    return;
                int visibleItemCount = mLayoutManager.getChildCount();
                int totalItemCount = mLayoutManager.getItemCount();
                int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    DOWNLOAD_PAGE_N += 1;
                    LOADING = true;
                    if (NO_MORE)
                        new getMoreRepos().execute();
                }
            }
        };

        rv.setOnScrollListener(mScrollListener);

    }

    private class getMoreRepos extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            repositoryList.add(null);
            repoAdapter.notifyItemChanged(repositoryList.size() - 1);
        }

        @Override
        protected String doInBackground(String... params) {
            t = new ArrayList<>(repositoryService.pageRepositories(user, FILTER_OPTION, DOWNLOAD_PAGE_N, ITEMS_DOWNLOADED_PER_PAGE).next());
            if (t.isEmpty())
                NO_MORE = false;
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            repositoryList.remove(repositoryList.lastIndexOf(null));
            for (int i = 0; i < t.size(); i++) {
                repositoryList.add(t.get(i));
            }
            if (NO_MORE)
                repoAdapter.notifyItemChanged(repositoryList.size() - 1);
            else
                repoAdapter.notifyDataSetChanged();
            LOADING = false;
        }
    }

}
