package com.dave.android.routerdocs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dave.android.router_annotation.PageFiled;
import com.dave.android.router_annotation.PageService;

@PageService(name = "主页面",path = "/app/main",desc = "主页面，这是介绍",version = "1.0.0")
public class MainActivity extends AppCompatActivity {

    @PageFiled(name = "类型",version = "1.0",required = true)
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
