/*
 * Copyright (c)  2016 GLodi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package giuliolodi.navforgithub;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.StarService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StarredFragment extends Fragment {

    List<Repository> starredRepoList;
    List<Bitmap> starredRepoAuthorIcons = new ArrayList<Bitmap>();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    StarredAdapter starredAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.starred_fragment, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.starred_recycler_view);
        progressBar = (ProgressBar) v.findViewById(R.id.starred_progress_bar);
        new getStarred().execute();
        return v;
    }

    class getStarred extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Set progress bar visible
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... strings) {
            // Setup StarService
            StarService starService = new StarService();
            starService.getClient().setOAuth2Token(Constants.getToken(getContext()));

            // Store list of starred repos
            try {
                starredRepoList = starService.getStarred();
            } catch (IOException e) {e.printStackTrace();}

            // For each starred repo, save in separate bitmap list the authors icons
            for (int i = 0; i < starredRepoList.size(); i++) {
                try {
                    starredRepoAuthorIcons.add(Picasso.with(getContext()).load(starredRepoList.get(i).getOwner().getAvatarUrl()).get());
                } catch (IOException e) {e.printStackTrace();}
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // Set progress bar invisible
            progressBar.setVisibility(View.GONE);

            // Set adapter
            starredAdapter = new StarredAdapter(starredRepoList, starredRepoAuthorIcons);

            // Set adapter on RecyclerView and notify it
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(starredAdapter);
            starredAdapter.notifyDataSetChanged();
        }
    }

}

