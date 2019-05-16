package com.itsdf07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.itsdf07.afutils.log.FLog;
import com.itsdf07.afutils.okhttp3.NetCode;
import com.itsdf07.afutils.okhttp3.OkHttp3Utils;
import com.itsdf07.afutils.okhttp3.callback.HttpBaseCallback;
import com.itsdf07.afutils.views.FGesture2UnlockView;
import com.itsdf07.afutils.views.FSlide2UnlockView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String URL = "http://192.168.2.52:8080/itsdf07/getTestApi";
    //String URL = "http://baidu.com";
    private EditText etUrl;
    private FSlide2UnlockView fSlide2UnlockView;
    private FGesture2UnlockView fGesture2UnlockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = (EditText) findViewById(R.id.id_http_url);
        fSlide2UnlockView = (FSlide2UnlockView) findViewById(R.id.id_slide2unlock);

        fSlide2UnlockView.setUnlockListener(new FSlide2UnlockView.UnlockListener() {
            @Override
            public void onUnlock() {
                Toast.makeText(MainActivity.this, "可以往下做解锁后要执行的事情，如界面跳转", Toast.LENGTH_SHORT).show();
            }
        });

        fGesture2UnlockView = (FGesture2UnlockView) findViewById(R.id.id_gestrue2unlock);
        fGesture2UnlockView.setOnUnlockListener(new FGesture2UnlockView.UnlockListener() {
            @Override
            public boolean isUnlockSuccess(String result) {
                Toast.makeText(MainActivity.this, "手势解锁:result" + result, Toast.LENGTH_SHORT).show();
                return "123456".equals(result);
            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "手势解锁成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                Toast.makeText(MainActivity.this, "手势解锁失败", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.id_http_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject data = new JSONObject();
                try {
                    data.put("data1", "数据1");
                    data.put("data2", 11);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OkHttp3Utils.doPostAsynData(etUrl.getText().toString(), data.toString(), new HttpBaseCallback() {
                    @Override
                    public void onSuccess(String result) {
                        FLog.dTag("itsdf07", "result:%s", result);
                        Toast.makeText(MainActivity.this, "result:" + result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(NetCode netCode, String msg) {
                        FLog.eTag("itsdf07", "netCode:%s,msg:%s", netCode.getDesc(), msg);
                        Toast.makeText(MainActivity.this, "msg:" + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
