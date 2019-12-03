package com.example.collectdata.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.ListItemBean;
import com.example.collectdata.bean.MessageListBean;
import com.example.collectdata.bean.SelectorItemBean;
import com.example.collectdata.bean.ViewItemBean;
import com.example.collectdata.exception.PageInitException;
import com.example.collectdata.listener.ListButtonListener;
import com.example.collectdata.tools.CacheTools;
import com.example.collectdata.tools.ConstTools;
import com.example.collectdata_01.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * 数据收集页面列表的适配器，用于显示不同类型信息条目
 */
public class MessageListAdapter extends BaseAdapter {

    private final int adapterType;
    private LayoutInflater layoutInflater;
    private Context context;
    private MessageListBean messageListBean;
    private HashMap<String,ViewItemBean> viewItemBeanMap = new HashMap<>();

    public static final int TYPE_COUNT = 3;

    /**
     * @param context
     * @param adapterType 适配器类型
     */
    public MessageListAdapter(Context context,int adapterType) {
        super();
        this.adapterType = adapterType;
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //初始化BeanList
        try {
            try {
                messageListBean = MessageListBean.getInstance(adapterType);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (PageInitException e) {
            e.printStackTrace();
            Toast.makeText(context, ConstTools.EXCEPTION_PAGE_INIT,Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public int getItemViewType(int position) {
        return messageListBean.getList().get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getCount() {
        return messageListBean.getList().size();
    }

    @Override
    public Object getItem(int i) {
        return messageListBean.getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("INDEX","获取条目成功   " + 24);
//        map.put(position, convertView)
        if (CacheTools.listViewMessage.get(position) != null) {
            return CacheTools.listViewMessage.get(position);
        }
        CommonItemBean commonItemBean = messageListBean.getItemBean(position);
        int type = commonItemBean.type;

        switch (type) {
            case ConstTools.MESSAGE_BEANTYPE_COMMON:
                convertView = getCommonView(convertView,position,commonItemBean);
             break;
            case ConstTools.MESSAGE_BEANTYPE_SELECTOR:
                convertView = getSelectorView(convertView,commonItemBean,position,false);
             break;
             case ConstTools.MESSAGE_BEANTYPE_TITLELINE:
                convertView = getTitleLineView(convertView,commonItemBean,position);
        }
        CacheTools.listViewMessage.put(position,convertView);
        return convertView;
    }

    /**
     * 获取输入框
     * @return
     */
    private View getCommonView(View convertView, int position, CommonItemBean commonItemBean){

        Log.i("INDEX","条目为普通条目   " + position);
        convertView = layoutInflater.inflate(R.layout.message_item,null);
//        TextView titleView = convertView.findViewById(R.id.message_item_title);
//        titleView.setText(commonItemBean.getTitle());
        TextInputLayout textInputLayout = convertView.findViewById(R.id.message_item_title);
        textInputLayout.setHint(commonItemBean.getTitle());
        //TODO 判断是否为首次输入，如果否，显示之前的内容

        TextInputEditText textInputEditText = convertView.findViewById(R.id.message_content);
        textInputEditText.setText(commonItemBean.getContent());
        //添加组件
        Log.i("添加组件",commonItemBean.getTitle());
        viewItemBeanMap.put(commonItemBean.getTitle(),new ViewItemBean(textInputEditText));
//                    viewItemBeanList.add(new ViewItemBean(editText));

        return convertView;
    }


    private View getSelectorView(View convertView, CommonItemBean commonItemBean, int position, boolean isInner){
        SelectorItemBean selectorItemBean = (SelectorItemBean)commonItemBean;
        Log.i("INDEX","条目为下拉框   " + position);
            convertView = layoutInflater.inflate(R.layout.message_selector_item,null);

        //初始化标题栏
        TextView titleView2 = convertView.findViewById(R.id.message_selector_item_title);
        titleView2.setText(commonItemBean.getTitle());

        //初始化单选框
        MaterialButtonToggleGroup buttonToggleGroup =
                convertView.findViewById(R.id.message_selector_item_togglebutton);
        buttonToggleGroup.setSingleSelection(true);
        List<String> selectorMessage = selectorItemBean.getData();
        Log.i("selectorMessage", "getSelectorView: " + selectorMessage.size());

        for (int i = 0 ; i < selectorMessage.size() ; i ++){
            //初始化Button
            View buttonView = layoutInflater.inflate(R.layout.message_selector_button,null);
            Button button = buttonView.findViewById(R.id.message_selector_item_button);
            button.setId(View.generateViewId());
            //设置button需要显示的内容
            String buttonContent = selectorMessage.get(i);
            button.setText(buttonContent);
            //如果content和当前选择相同，则按钮高亮
            //TODO 设置高亮
//            Log.i("MessageListAdapter",buttonContent + "...." + MessageListBean.getCurrentSelect(position));
//            if (buttonContent.equals(MessageListBean.getCurrentSelect(position))){
//                button.setSelected(true);
//                Log.i("MessageListAdapter","设置成功");
//            }

            //添加监听器
            button.setOnClickListener(new ListButtonListener(position));
            //添加进group
            buttonToggleGroup.addView(button);
            Log.i("MessageAdapter","添加了：" + selectorMessage.get(i));
        }

        //添加组件
        Log.i("添加组件",commonItemBean.getTitle());
        viewItemBeanMap.put(commonItemBean.getTitle(),new ViewItemBean(buttonToggleGroup));

        return convertView;

    }

    private View getTitleLineView(View convertView, CommonItemBean commonItemBean, int position){
        Log.i("INDEX","条目为二级列表条目   ");
        ListItemBean listItemBean = (ListItemBean)commonItemBean;
        convertView = layoutInflater.inflate(R.layout.message_item_linetitle,null);
        TextView textView = convertView.findViewById(R.id.message_linetitle_title);
        textView.setText(listItemBean.getTitle());
        LinearLayout linearLayout= convertView.findViewById(R.id.message_linetitle_area);
        int length = listItemBean.getLength();
        Log.i("MessageAdapter","二级菜单长度为:" + length);
        View view;
        for (int i = 0 ; i < length ; i ++) {
            Log.i("i=", i+"");
            CommonItemBean currentItemBean = listItemBean.getInnerItem(i);
            int type = currentItemBean.type;
//            currentItemBean.setTitle(currentItemBean.getTitle() + "ceshi");
            Log.i("MessageAdapter","二级菜单添加了一个条目:" + currentItemBean);
            if (type == ConstTools.MESSAGE_BEANTYPE_COMMON)
                view = getCommonView(convertView,position,currentItemBean);
            else if (type == ConstTools.MESSAGE_BEANTYPE_SELECTOR)
                view = getSelectorView(convertView,currentItemBean,position,true);
            else
                break;
            linearLayout.addView(view);
        }

        //添加组件测试
//        View buttonView = layoutInflater.inflate(R.layout.message_item,null);
//        TextInputLayout textInputLayout = buttonView.findViewById(R.id.message_item_title);
//        textInputLayout.setHint("测试输入框");
//        linearLayout.addView(buttonView);
        return convertView;
    }

    public HashMap<String, ViewItemBean> getViewItemBeanMap() {
        return viewItemBeanMap;
    }
}
