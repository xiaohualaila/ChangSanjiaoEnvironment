package com.aier.environment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aier.environment.R;
import com.aier.environment.adapter.AudioAdapter;


public class SendAudioView extends LinearLayout {

    private ListView mListView;

    public SendAudioView(Context context) {
        super(context);
    }

    public SendAudioView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initModule() {
        mListView = (ListView) findViewById(R.id.audio_list_view);
    }

    public void setAdapter(AudioAdapter adapter) {
        mListView.setAdapter(adapter);
    }
}
