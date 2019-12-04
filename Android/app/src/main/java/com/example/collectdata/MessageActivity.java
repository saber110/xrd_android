package com.example.collectdata;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata.adapter.MessageListAdapter;
import com.example.collectdata.listener.SubmitListener;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata_01.R;


public class MessageActivity extends AppCompatActivity {

    private ListView listView;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //获取列表组建
        listView = findViewById(R.id.message_list);

        MessageListAdapter mla = new MessageListAdapter(this, CacheTools.pageType);

        listView.setAdapter(mla);

        button = (Button)this.findViewById(R.id.message_submit);
        button.setOnClickListener(new SubmitListener(this,mla.getViewItemBeanMap()));

    }

}
