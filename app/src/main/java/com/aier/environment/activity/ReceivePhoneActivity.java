package com.aier.environment.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.aier.environment.R;

public class ReceivePhoneActivity extends BaseActivity implements View.OnClickListener{
    private Context mContext;
    private TextView btn_receive,btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_receive_phone);
        btn_receive = findViewById(R.id.btn_receive);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_receive.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_receive:
               /// goToActivity(mContext, );
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }

    }
}
