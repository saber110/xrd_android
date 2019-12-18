package com.example.collectdata;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.collectdata_01.R;
import com.example.dao.Position;

import java.util.ArrayList;

public class BuweiActivity extends AppCompatActivity {
    private LinearLayout Linayout;
    private TextView textView;
    private CheckBox checkBox;
    private ArrayList<Position> positions = new ArrayList<>();
    public static String retString = "";
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buwei);
        Linayout=findViewById(R.id.table);
        textView = (TextView)findViewById(R.id.textView3);
        checkBox = (CheckBox)findViewById(R.id.check);
        save = (Button)findViewById(R.id.save_buwei);
        checkBox.setChecked(true);
//        Linayout.setGravity(Gravity.CENTER_HORIZONTAL);
        initPositions();
        for (int i=0;i<7;i++){
            //循环表格为12行
            LinearLayout varlayout=new LinearLayout(this);
            varlayout.setOrientation(LinearLayout.HORIZONTAL);
            //new 一个线性布局用来画每一行，设置它为水平
            for (int a=0;a<7;a++){
                //循环它每一行显示4条数据，这就用到Textview了
                TextView text=new TextView(this);
                text.setWidth(120);
                text.setHeight(60);
                //text.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1));
                //设置Textview宽为0，高为不确定，Layout_weight为1
                for(int j=0;j<positions.size();j++){
                    if(i==positions.get(j).getRow()&&a==positions.get(j).getColumn()) {
                        text.setText(positions.get(j).getText());
                        text.setBackground(this.getResources().getDrawable(R.drawable.textview_border));
                        final int finalI = i;
                        final int finalA = a;
                        final int finalJ = j;
                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                createInputDialog(positions.get(finalJ).getText());
                            }
                        });
                        break;
                    }
                    else text.setText("");
                }
                text.setGravity(Gravity.CENTER);
                //使它居中
                varlayout.addView(text);//添加到水平线性布局
//                TextView reit=new TextView(this);
//                //这个Textview画表格竖线
//                reit.setLayoutParams(new LinearLayout.LayoutParams(2,
//                        LinearLayout.LayoutParams.WRAP_CONTENT ));
//                //设置Textview宽为2dp，高为不确定
//                reit.setBackgroundColor(Color.BLACK);
//                varlayout.addView(reit);//把他添加到水平线性布局里

            }
//            TextView reit=new TextView(this);
//            //这个Textview用来画横线,
//            reit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2 ));
//            reit.setBackgroundColor(Color.BLACK);
//            Linayout.addView(reit);
            Linayout.addView(varlayout, LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("locationDescription",retString);
                setResult(RESULT_OK, i);
                Toast.makeText(BuweiActivity.this,"保存成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void initPositions(){
        positions.add(new Position("西北",0,1));
        positions.add(new Position("北西",0,2));
        positions.add(new Position("北中",0,3));
        positions.add(new Position("北东",0,4));
        positions.add(new Position("东北",0,5));
        positions.add(new Position("西中",1,1));
        positions.add(new Position("东中",1,5));
        positions.add(new Position("西中",2,1));
        positions.add(new Position("中中",2,3));
        positions.add(new Position("东中",2,5));
        positions.add(new Position("西中",3,1));
        positions.add(new Position("东中",3,5));
        positions.add(new Position("南西",4,0));
        positions.add(new Position("南东",4,6));
        positions.add(new Position("西南",5,1));
        positions.add(new Position("南中",5,3));
        positions.add(new Position("东南",5,5));
        positions.add(new Position("南中",6,2));
        positions.add(new Position("南中",6,4));
    }
    private void createInputDialog(final String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text);    //设置对话框标题
        final EditText edit = new EditText(this);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkBox.isChecked()){
                    String[] t = edit.getText().toString().split(" ");
                    for (int i = 0; i < t.length; i++) {
                        retString += text + "-0-" + t[i] + ";";
                    }
                }
                else {
                    String[] t = edit.getText().toString().split(";");
                    for (int i = 0; i < t.length; i++) {
                        retString += text + "-" + t[i] + ";";
                    }
                }
                 textView.setText(retString.substring(0,retString.length()-1));
                 Log.i("retString","retString="+retString);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }
}
