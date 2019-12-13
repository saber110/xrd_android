package com.example.test;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata.bean.CommonItemBean;
import com.example.collectdata.bean.SelectorItemBean;
import com.example.collectdata_01.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater layoutInflater;
    // 数据集
    private List<CommonItemBean> data;
    // 存储结果的map，在button的点击事件中添加

    private Map<String, String> resultMap = new HashMap<>(75);
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
        return data.get(p).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //创建viewHolder
        if (viewType == 0) {
            View view = layoutInflater.inflate(R.layout.message_item, parent, false);
            return new MyViewHolder(view);
//        }else if (viewType == 1){
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_selector_item, parent, false);
            return new MyViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textInputLayout.setHint(data.get(position).getTitle());
            TextWatcher watcher = (TextWatcher) ((MyViewHolder) holder).textInputEditText.getTag(((MyViewHolder) holder).textInputEditText.getId());
            if (watcher != null) {
                ((MyViewHolder) holder).textInputEditText.removeTextChangedListener(watcher);
            }
            if (resultMap.keySet().contains(data.get(position).getTitle())) {
                ((MyViewHolder) holder).textInputEditText.setText(resultMap.get(data.get(position).getTitle()));
            }
            ((MyViewHolder) holder).textInputEditText.addTextChangedListener(watcher);
            ((MyViewHolder) holder).textInputEditText.setTag(((MyViewHolder) holder).textInputEditText.getId(), watcher);
            holder.setIsRecyclable(false);
        } else {
            MyViewHolder2 buttonHolder = ((MyViewHolder2) holder);
            SelectorItemBean currentItemBean = (SelectorItemBean) data.get(position);

            buttonHolder.titleView2.setText(currentItemBean.getTitle());
            // 判断单选按钮是否选中，防止发生错乱
            buttonHolder.buttonToggleGroup.removeOnButtonCheckedListener(listener);

            // 多选框
            if (!currentItemBean.isSingle()) {
                List<String> select = currentItemBean.getCurrentSelects();
                for (String content : select) {
                    for (int i = 0; i < buttonHolder.buttonToggleGroup.getChildCount(); i++) {
                        MaterialButton button = (MaterialButton) ((MyViewHolder2) holder).buttonToggleGroup.getChildAt(i);
                        if (button.getText().equals(content)) {
                            button.setBackgroundColor(Color.RED);
                        } else {
                            button.setBackgroundColor(Color.WHITE);
                        }
                    }
                }
            } else if (resultMap.keySet().contains(currentItemBean.getTitle())) {
                for (int i = 0; i < buttonHolder.buttonToggleGroup.getChildCount(); i++) {
                    MaterialButton button = (MaterialButton) ((MyViewHolder2) holder).buttonToggleGroup.getChildAt(i);
                    if (button.getText().equals(resultMap.get(currentItemBean.getTitle()))) {
                        button.setBackgroundColor(Color.RED);
                    } else {
                        button.setBackgroundColor(Color.WHITE);
                    }
                }
            } else {
                buttonHolder.buttonToggleGroup.clearChecked();
            }
            buttonHolder.buttonToggleGroup.addOnButtonCheckedListener(listener);

            holder.setIsRecyclable(false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        TextInputEditText textInputEditText;

        public MyViewHolder(@NonNull View itemView) {
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
                    // TODO 在这可以添加输入验证，比如int和String验证
                    resultMap.put((String) textInputEditText.getTag(), s.toString());
                }
            };
            textInputEditText.addTextChangedListener(textWatcher);
            textInputEditText.setTag(textInputEditText.getId(), textWatcher);
            Log.i("recycler", "MyViewHolder");
        }
    }

    public void setData(List<CommonItemBean> data) {
        this.data = data;
    }

    public Map<String, String> getResultMap() {
        return resultMap;
    }

    class MyViewHolder2 extends RecyclerView.ViewHolder {
        TextView titleView2;
        MaterialButtonToggleGroup buttonToggleGroup;

        public MyViewHolder2(@NonNull View itemView) {
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
                if (!((SelectorItemBean) commonItemBean).isSingle()) {
                    for (String content : ((SelectorItemBean) commonItemBean).getCurrentSelects()) {
                        if (buttonContent.equals(content))
                            button.setBackgroundColor(Color.RED);
                    }
                } else
                    button.setBackgroundColor(Color.WHITE);
                button.setTextColor(Color.BLACK);
                buttonToggleGroup.addView(button);
            }
            //设置tag防止数据错乱，主要是title的错乱
            buttonToggleGroup.setTag(commonItemBean.getTitle());
            //设置监听函数
            buttonToggleGroup.addOnButtonCheckedListener(listener);
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

}
