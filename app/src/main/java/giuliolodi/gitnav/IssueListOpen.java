/*
 * Copyright 2016 GLodi
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import giuliolodi.gitnav.Adapters.IssueAdapter;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IssueListOpen {

    @BindView(R.id.issuelist_open_progressbar) ProgressBar progressBar;
    @BindView(R.id.issuelist_open_rv) RecyclerView rv;

    private Context context;
    private Observable<List<Issue>> observable;
    private Observer<List<Issue>> observer;
    private Subscription subscription;
    private IssueService issueService;
    private List<Issue> issuesReceived, masterIssueList;
    private Map filterForOpen;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private IssueAdapter issueAdapter;

    private int DOWNLOAD_PAGE_N = 1;
    private int ITEMS_PER_PAGE = 1;

    public void populate(final Context context, View view, final String owner, final String repo) {
        this.context = context;

        ButterKnife.bind(this, view);

        progressBar.setVisibility(View.VISIBLE);

        filterForOpen = new HashMap();
        filterForOpen.put("state", "open");

        issueAdapter = new IssueAdapter(masterIssueList, context);
        linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(issueAdapter);

        observable = Observable.create(new Observable.OnSubscribe<List<Issue>>() {
            @Override
            public void call(Subscriber<? super List<Issue>> subscriber) {
                issuesReceived = new ArrayList<>(issueService.pageIssues(owner, repo, filterForOpen, DOWNLOAD_PAGE_N, ITEMS_PER_PAGE).next());
                subscriber.onNext(issuesReceived);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        observer = new Observer<List<Issue>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Issue> issues) {
                if (issues != null && !issues.isEmpty()) {
                    masterIssueList.addAll(issues);
                    issueAdapter.notifyDataSetChanged();
                } else {
                    subscription.unsubscribe();
                }
            }
        };

        subscription = observable.subscribe(observer);
    }

}