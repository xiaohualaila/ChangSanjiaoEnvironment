package com.aier.environment.activity.historyfile.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.aier.environment.R;
import com.aier.environment.activity.historyfile.controller.HistoryFileController;
import com.aier.environment.activity.historyfile.view.HistoryFileView;

/**
 * Created by ${chenyn} on 2017/8/23.
 */

public class HistoryFileActivity extends FragmentActivity {
    private HistoryFileView mView;
    private HistoryFileController mController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_file);
        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        long groupId = intent.getLongExtra("groupId", 0);
        boolean isGroup = intent.getBooleanExtra("isGroup", false);

        mView = (HistoryFileView) findViewById(R.id.send_file_view);
        mView.initModule();
        mController = new HistoryFileController(this, mView, userName, groupId, isGroup);
        mView.setOnClickListener(mController);
        mView.setOnPageChangeListener(mController);
        mView.setScroll(true);
    }

    public FragmentManager getSupportFragmentManger() {
        return getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.trans_finish_in);
    }
}
