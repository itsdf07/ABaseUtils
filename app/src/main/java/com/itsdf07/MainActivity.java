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

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String URL = "http://192.168.2.52:8080/itsdf07/getTestApi";
    //String URL = "http://baidu.com";
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUrl = (EditText) findViewById(R.id.id_http_url);
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
