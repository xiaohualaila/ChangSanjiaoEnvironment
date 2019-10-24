package com.aier.environment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aier.environment.R;
import com.aier.environment.adapter.VideoAdapter;


public class SendVideoView extends LinearLayout {

    private ListView mListView;

    public SendVideoView(Context context) {
        super(context);
    }

    public SendVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListView = (ListView) findViewById(R.id.video_list_view);
    }

    public void setAdapter(VideoAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
