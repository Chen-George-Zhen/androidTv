package com.jingyubc.quotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends Activity {

    SharedPreferences sharedPreferences;
    TextView url_one;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        sharedPreferences = getSharedPreferences("setting_url", Context.MODE_PRIVATE);
        url_one = (TextView)findViewById(R.id.url_one);

        if(!sharedPreferences.getString("url_one", "").isEmpty()) {
            url_one.setText(sharedPreferences.getString("url_one", ""));
        } else {
            url_one.setText(getResources().getString(R.string.screen_url_1));
        }

        button = (Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(url_one.getText().toString() == "") {
                    Toast.makeText(ConfigActivity.this, "请输入输入地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                sharedPreferences.edit().putString("url_one", url_one.getText().toString()).commit();

                finish();
            }
        });
    }
}
