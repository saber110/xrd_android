package com.example.BuildingMessage;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.ListItemBean;
import com.example.collectdata.bean.SelectorItemBean;
import com.example.collectdata_01.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    // 数据集用来动态创建view
    private List<CommonItemBean> data;
    // 存储结果的map，在button的点击事件中添加
    private HashMap<String, String> resultMap = new HashMap<>(75);
    private Map<String, String> result = new HashMap<>(75);
    //保存当前viewHolder数据的item
    private CommonItemBean commonItemBean;

    public MyAdapter(Context context, List<CommonItemBean> d) {
        this.context = context;
        data = d;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int p) {
        commonItemBean = null;
        commonItemBean = data.get(p);
        return data.get(p).getType();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.message_item, parent, false);
            return new TextViewHolder(view);
        } else if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_selector_item, parent, false);
            return new ButtonViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_item_linetitle, parent, false);
            return new ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        // 文字输入框复用
        if (holder instanceof TextViewHolder) {
            TextViewHolder textHolder = (TextViewHolder) holder;

            textHolder.textInputLayout.setHint(data.get(position).getTitle());
            // 复用前要移除listener，防止错误的添加数据到resultMap，下边类似
            TextWatcher watcher = (TextWatcher) textHolder.textInputEditText.getTag(textHolder.textInputEditText.getId());
            if (watcher != null) {
                textHolder.textInputEditText.removeTextChangedListener(watcher);
            }
            if (resultMap.keySet().contains(data.get(position).getTitle())) {
                textHolder.textInputEditText.setText(resultMap.get(data.get(position).getTitle()));
            }
            textHolder.textInputEditText.addTextChangedListener(watcher);
            textHolder.textInputEditText.setTag(textHolder.textInputEditText.getId(), watcher);
            holder.setIsRecyclable(false);
        } else if (holder instanceof ButtonViewHolder) {
            ButtonViewHolder radioHolder = (ButtonViewHolder) holder;

            radioHolder.titleView2.setText(data.get(position).getTitle());
            // 判断单选按钮是否选中，防止发生错乱
            radioHolder.buttonToggleGroup.removeOnButtonCheckedListener(listener);
            if (resultMap.keySet().contains(data.get(position).getTitle())) {
                for (int i = 0; i < radioHolder.buttonToggleGroup.getChildCount(); i++) {
                    MaterialButton button = (MaterialButton) radioHolder.buttonToggleGroup.getChildAt(i);
                    if (button.getText().equals(resultMap.get(data.get(position).getTitle()))) {
                        button.setBackgroundColor(Color.RED);
                    } else {
                        button.setBackgroundColor(Color.WHITE);
                    }
                }
            } else {
                radioHolder.buttonToggleGroup.clearChecked();
            }
            radioHolder.buttonToggleGroup.addOnButtonCheckedListener(listener);

            holder.setIsRecyclable(false);
        } else {
            ListViewHolder listHolder = (ListViewHolder) holder;

            for (int j = 0; j < ((ListItemBean) data.get(position)).getInnerItemList().size(); j++) {
                CommonItemBean commonItemBean = ((ListItemBean) data.get(position)).getInnerItemList().get(j);
                if (commonItemBean.getType() == 0) {// type为0表示这个列表项是textInputLayout
                    if (result.keySet().contains((String) listHolder.textView.getTag() + commonItemBean.getTitle())) {
                        List<TextInputEditText> list2 =
                                (List<TextInputEditText>) listHolder.textView.getTag(listHolder.textView.getId());
                        for (TextInputEditText textInputEditText : list2) {
                            TextWatcher watcher = (TextWatcher) textInputEditText.getTag(textInputEditText.getId());
                            if (watcher != null) {
                                textInputEditText.removeTextChangedListener(watcher);
                            }
                            if (((String) textInputEditText.getTag()).equals(commonItemBean.getTitle())
                                    && ((String) listHolder.textView.getTag()).equals(((ListItemBean) data.get(position)).getTitle())) {
                                textInputEditText.setText(result.get(listHolder.textView.getTag() + commonItemBean.getTitle()));
                            }
                            textInputEditText.addTextChangedListener(watcher);
                            textInputEditText.setTag(textInputEditText.getId(), watcher);
                        }
                    }
                } else {// ButtonToggleGroup
                    if (result.keySet().contains(commonItemBean.getTitle())) {
                        List<MaterialButtonToggleGroup> list =
                                (List<MaterialButtonToggleGroup>) listHolder.linearLayout.getTag(listHolder.linearLayout.getId());
                        for (MaterialButtonToggleGroup buttonToggleGroup : list) {
                            if (!buttonToggleGroup.getTag().equals(commonItemBean.getTitle()))
                                continue;
                            buttonToggleGroup.removeOnButtonCheckedListener(listener2);
                            for (int i = 0; i < buttonToggleGroup.getChildCount(); i++) {
                                MaterialButton button = (MaterialButton) buttonToggleGroup.getChildAt(i);
                                if (button.getText().equals(resultMap.get(commonItemBean.getTitle()))
                                        && ((String) buttonToggleGroup.getTag()).equals(commonItemBean.getTitle())) {
                                    button.setBackgroundColor(Color.RED);
                                    break;
                                } else {
                                    button.setBackgroundColor(Color.WHITE);
                                }
                            }
                            buttonToggleGroup.addOnButtonCheckedListener(listener2);
                        }
                    } else {
                        List<MaterialButtonToggleGroup> list = (List<MaterialButtonToggleGroup>) listHolder.linearLayout.getTag(listHolder.linearLayout.getId());
                        for (MaterialButtonToggleGroup buttonToggleGroup : list) {
                            buttonToggleGroup.removeOnButtonCheckedListener(listener2);
                            buttonToggleGroup.clearChecked();
                            buttonToggleGroup.addOnButtonCheckedListener(listener2);
                        }
                    }
                }
            }
            holder.setIsRecyclable(false);
        }
    }


    class TextViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        TextInputEditText textInputEditText;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textInputLayout = itemView.findViewById(R.id.message_item_title);
            textInputEditText = itemView.findViewById(R.id.message_content);
            textInputEditText.setTag(commonItemBean.getTitle());
            textInputEditText.setText(commonItemBean.getContent());
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //这个方法被调用，说明在s字符串中，从start位置开始的count个字符即将被长度为after的新文本所取代。
                    // 在这个方法里面改变s，会报错。
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //这个方法被调用，说明在s字符串中，从start位置开始的count个字符刚刚取代了长度为before的旧文本。
                    // 在这个方法里面改变s，会报错。
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //这个方法被调用，那么说明s字符串的某个地方已经被改变。
                    resultMap.put((String) textInputEditText.getTag(), s.toString());
                }
            };
            textInputEditText.addTextChangedListener(textWatcher);
            textInputEditText.setTag(textInputEditText.getId(), textWatcher);
        }
    }

    class ButtonViewHolder extends RecyclerView.ViewHolder {
        TextView titleView2;
        MaterialButtonToggleGroup buttonToggleGroup;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView2 = itemView.findViewById(R.id.message_selector_item_title);
            buttonToggleGroup = itemView.findViewById(R.id.message_selector_item_togglebutton);
            buttonToggleGroup.setSingleSelection(true);
            List<String> selectorMessage = ((SelectorItemBean) commonItemBean).getData();
            //初始化所有的单选按钮
            for (int i = 0; i < selectorMessage.size(); i++) {
                MaterialButton button = new MaterialButton(context);
                String buttonContent = selectorMessage.get(i);
                button.setText(buttonContent);
                if (buttonContent.equals(((SelectorItemBean) commonItemBean).getCurrentSelect())) {
                    button.setBackgroundColor(Color.RED);
                } else {
                    button.setBackgroundColor(Color.WHITE);
                }
                button.setTextColor(Color.BLACK);
                buttonToggleGroup.addView(button);
            }
            //设置tag防止数据错乱，主要是title的错乱
            buttonToggleGroup.setTag(commonItemBean.getTitle());
            //设置监听函数
            buttonToggleGroup.addOnButtonCheckedListener(listener);
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        TextView textView;
        ListItemBean listItemBean = (ListItemBean) commonItemBean;
        List<MaterialButtonToggleGroup> list = new ArrayList<>();
        List<TextInputEditText> list2 = new ArrayList<>();

        /**
         * itemView -->CardView --> LinearLayout |-->TextView （list的标题）
         * |-->LinearLayout -->TextInputLayout
         * |-->LinearLayout |-->TextView
         * |-->ScrollView -->ButtonGroup
         */
        public ListViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.message_linetitle_title);
            textView.setText(listItemBean.getTitle());
            textView.setTag(listItemBean.getTitle());
            linearLayout = itemView.findViewById(R.id.message_linetitle_area);
            int length = listItemBean.getLength();
            for (int i = 0; i < length; i++) {

                CommonItemBean currentItemBean = listItemBean.getInnerItem(i);
                int type = currentItemBean.getType();
                Log.i("MessageAdapter", "二级菜单添加了一个条目:" + currentItemBean);
                View view;
                if (type == 0) {
                    view = LayoutInflater.from(context).inflate(R.layout.message_item, null);
                    TextInputLayout textInputLayout = view.findViewById(R.id.message_item_title);
                    final TextInputEditText textInputEditText = view.findViewById(R.id.message_content);
                    textInputEditText.setText(currentItemBean.getContent());
                    textInputEditText.setTag(commonItemBean.getTitle());
                    TextWatcher textWatcher = new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            //这个方法被调用，说明在s字符串中，从start位置开始的count个字符即将被长度为after的新文本所取代。
                            // 在这个方法里面改变s，会报错。
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            //这个方法被调用，说明在s字符串中，从start位置开始的count个字符刚刚取代了长度为before的旧文本。
                            // 在这个方法里面改变s，会报错。
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            //这个方法被调用，那么说明s字符串的某个地方已经被改变。
                            resultMap.put(textView.getText() + (String) textInputEditText.getTag(), s.toString());
                            result.put(textView.getText() + (String) textInputEditText.getTag(), s.toString());
                        }
                    };
                    textInputEditText.addTextChangedListener(textWatcher);
                    textInputEditText.setTag(textInputEditText.getId(), textWatcher);
                    textInputEditText.setTag(currentItemBean.getTitle());
                    textInputLayout.setHint(currentItemBean.getTitle());
                    list2.add(textInputEditText);
                } else {
                    view = LayoutInflater.from(context).inflate(R.layout.message_selector_item, null);
                    TextView titleView2 = view.findViewById(R.id.message_selector_item_title);
                    titleView2.setText(currentItemBean.getTitle());
                    MaterialButtonToggleGroup buttonToggleGroup = view.findViewById(R.id.message_selector_item_togglebutton);
                    buttonToggleGroup.setSingleSelection(true);
                    List<String> selectorMessage = ((SelectorItemBean) currentItemBean).getData();
                    //初始化所有的单选按钮
                    for (int j = 0; j < selectorMessage.size(); j++) {
                        MaterialButton button = new MaterialButton(context);
                        String buttonContent = selectorMessage.get(j);
                        button.setText(buttonContent);
                        if (buttonContent.equals(((SelectorItemBean) currentItemBean).getCurrentSelect())) {
                            button.setBackgroundColor(Color.RED);
                        } else {
                            button.setBackgroundColor(Color.WHITE);
                        }
//                        button.setBackgroundColor(Color.WHITE);
                        button.setTextColor(Color.BLACK);
                        buttonToggleGroup.addView(button);
                    }
                    buttonToggleGroup.setTag(currentItemBean.getTitle());
                    buttonToggleGroup.addOnButtonCheckedListener(listener2);
                    list.add(buttonToggleGroup);
                }
                linearLayout.addView(view);
            }
            // 将buttonGroup的list设置到linearLayout的tag中，textInputEditText的list设置进textView的tag中
            linearLayout.setTag(linearLayout.getId(), list);
            textView.setTag(textView.getId(), list2);
            Log.i("test", "");
        }
    }

    private MaterialButtonToggleGroup.OnButtonCheckedListener listener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked)
                return;
            for (int i = 0; i < group.getChildCount(); i++) {
                MaterialButton button = (MaterialButton) group.getChildAt(i);
                if (button.getId() == checkedId) {
                    Log.i("Button", "button Id = " + button.getId() + "的按钮" + isChecked);
                    Log.i("Button", "button Id = " + button.getId() + "的按钮" + "被点击，颜色变为红色");
                    button.setBackgroundColor(Color.RED);
                    resultMap.put((String) group.getTag(), button.getText().toString());
                    Toast.makeText(context, button.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    button.setBackgroundColor(Color.WHITE);
                }
            }
        }
    };

    private MaterialButtonToggleGroup.OnButtonCheckedListener listener2 = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked)
                return;
            for (int i = 0; i < group.getChildCount(); i++) {
                MaterialButton button = (MaterialButton) group.getChildAt(i);
                if (button.getId() == checkedId) {
                    Log.i("Button", "button Id = " + button.getId() + "的按钮" + isChecked);
                    Log.i("Button", "button Id = " + button.getId() + "的按钮" + "被点击，颜色变为红色");
                    button.setBackgroundColor(Color.RED);
                    resultMap.put((String) group.getTag(), button.getText().toString());
                    result.put((String) group.getTag(), button.getText().toString());
                    Log.i("map", "result Map添加了" + group.getTag() + ":" + button.getText().toString());
                    Toast.makeText(context, button.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    button.setBackgroundColor(Color.WHITE);
                }
            }
        }
    };

    public void setData(List<CommonItemBean> data) {
        this.data = data;
    }

    HashMap<String, String> getResultMap() {
        return resultMap;
    }

    void setResultMap(HashMap<String, String> resultMap) {
        this.resultMap = resultMap;
    }

    void clearResultMap() {
        resultMap.clear();
    }

}