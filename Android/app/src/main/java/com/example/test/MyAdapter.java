package com.example.test;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

    MyAdapter(Context context, List<CommonItemBean> d) {
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
            return new TextViewHolder(view);
//        }else if (viewType == 1){
        } else if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_selector_item, parent, false);
            return new ButtonViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_selector_item, parent, false);
            return new MulButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        boolean isRequire = data.get(position).isRequire();

        if (holder instanceof TextViewHolder) {
            TextViewHolder textholder = (TextViewHolder) holder;

            setViewBorder(isRequire,textholder.itemView);
            textholder.textInputLayout.setHint(data.get(position).getTitle());
            TextWatcher watcher = (TextWatcher) textholder.textInputEditText.getTag(textholder.textInputEditText.getId());
            if (watcher != null) {
                textholder.textInputEditText.removeTextChangedListener(watcher);
            }
            if (resultMap.keySet().contains(data.get(position).getTitle())) {
                textholder.textInputEditText.setText(resultMap.get(data.get(position).getTitle()));
            }
            textholder.textInputEditText.addTextChangedListener(watcher);
            textholder.textInputEditText.setTag(textholder.textInputEditText.getId(), watcher);
            holder.setIsRecyclable(false);
        } else if (holder instanceof ButtonViewHolder) {
            ButtonViewHolder buttonHolder = ((ButtonViewHolder) holder);
            SelectorItemBean currentItemBean = (SelectorItemBean) data.get(position);

            setViewBorder(isRequire,buttonHolder.itemView);
            buttonHolder.titleView2.setText(currentItemBean.getTitle());
            // 判断单选按钮是否选中，防止发生错乱
            buttonHolder.buttonToggleGroup.removeOnButtonCheckedListener(listener);
            if (resultMap.keySet().contains(currentItemBean.getTitle())) {
                for (int i = 0; i < buttonHolder.buttonToggleGroup.getChildCount(); i++) {
                    MaterialButton button = (MaterialButton) ((ButtonViewHolder) holder).buttonToggleGroup.getChildAt(i);
                    if (button.getText().equals(resultMap.get(currentItemBean.getTitle()))) {
                        button.setBackgroundColor(context.getResources().getColor(R.color.button_selected));
                    } else {
                        button.setBackgroundColor(Color.WHITE);
                    }
                }
            } else {
                buttonHolder.buttonToggleGroup.clearChecked();
            }
            buttonHolder.buttonToggleGroup.addOnButtonCheckedListener(listener);

            holder.setIsRecyclable(false);
        } else if (holder instanceof MulButtonViewHolder) {
            MulButtonViewHolder mulbuttonHolder = ((MulButtonViewHolder) holder);
            SelectorItemBean currentItemBean = (SelectorItemBean) data.get(position);

            setViewBorder(isRequire,mulbuttonHolder.itemView);
            mulbuttonHolder.titleView2.setText(currentItemBean.getTitle());
            mulbuttonHolder.buttonToggleGroup.removeOnButtonCheckedListener(mullistener);

            for (int i = 0; i < mulbuttonHolder.buttonToggleGroup.getChildCount(); i++) {

                MaterialButton button = (MaterialButton) mulbuttonHolder.buttonToggleGroup.getChildAt(i);
                String s = button.getText().toString();
                String content = resultMap.get((String) mulbuttonHolder.buttonToggleGroup.getTag());

                String c[] = content.split("&");
                for (int j = 0; j < c.length; j++) {
                    if (c[j].equals(s)) {
                        button.setBackgroundColor(context.getResources().getColor(R.color.button_selected));
                        break;
                    }
                    button.setBackgroundColor(Color.WHITE);
                }
            }
            mulbuttonHolder.buttonToggleGroup.addOnButtonCheckedListener(mullistener);
            mulbuttonHolder.setIsRecyclable(false);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class TextViewHolder extends RecyclerView.ViewHolder {
        TextInputLayout textInputLayout;
        TextInputEditText textInputEditText;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setPadding(5, 0, 5, 0);
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

    class ButtonViewHolder extends RecyclerView.ViewHolder {
        TextView titleView2;
        MaterialButtonToggleGroup buttonToggleGroup;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setPadding(5, 0, 5, 0);
            titleView2 = itemView.findViewById(R.id.message_selector_item_title);
            buttonToggleGroup = itemView.findViewById(R.id.message_selector_item_togglebutton);
            buttonToggleGroup.setSingleSelection(true);
            List<String> selectorMessage = ((SelectorItemBean) commonItemBean).getData();
            //初始化所有的单选按钮
            for (int i = 0; i < selectorMessage.size(); i++) {
                MaterialButton button = new MaterialButton(context);
                String buttonContent = selectorMessage.get(i);
                button.setText(buttonContent);
                button.setTextColor(Color.BLACK);
                buttonToggleGroup.addView(button);
            }
            //设置tag防止数据错乱，主要是title的错乱
            buttonToggleGroup.setTag(commonItemBean.getTitle());
            //设置监听函数
            buttonToggleGroup.addOnButtonCheckedListener(listener);
        }
    }

    class MulButtonViewHolder extends RecyclerView.ViewHolder {
        TextView titleView2;
        MaterialButtonToggleGroup buttonToggleGroup;

        public MulButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setPadding(5, 0, 5, 0);
            titleView2 = itemView.findViewById(R.id.message_selector_item_title);
            buttonToggleGroup = itemView.findViewById(R.id.message_selector_item_togglebutton);
            buttonToggleGroup.setSingleSelection(true);
            List<String> selectorMessage = ((SelectorItemBean) commonItemBean).getData();
            //初始化所有的按钮
            for (int i = 0; i < selectorMessage.size(); i++) {
                MaterialButton button = new MaterialButton(context);
                String buttonContent = selectorMessage.get(i);
                button.setText(buttonContent);
                if (!((SelectorItemBean) commonItemBean).isSingle()) {
                    for (String content : ((SelectorItemBean) commonItemBean).getCurrentSelects()) {
                        if (buttonContent.equals(content)) {
                            button.setBackgroundColor(Color.RED);
                            break;
                        }
                        button.setBackgroundColor(Color.WHITE);
                    }
                } else
                    button.setBackgroundColor(Color.WHITE);
                button.setTextColor(Color.BLACK);
                buttonToggleGroup.addView(button);
            }
            //设置tag防止数据错乱，主要是title的错乱
            buttonToggleGroup.setTag(commonItemBean.getTitle());
            //设置监听函数
            buttonToggleGroup.addOnButtonCheckedListener(mullistener);
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
                    button.setBackgroundColor(context.getResources().getColor(R.color.button_selected));
                    resultMap.put((String) group.getTag(), button.getText().toString());
                    Toast.makeText(context, button.getText().toString(), Toast.LENGTH_SHORT).show();
                } else {
                    button.setBackgroundColor(Color.WHITE);
                }
            }

        }
    };

    private MaterialButtonToggleGroup.OnButtonCheckedListener mullistener = new MaterialButtonToggleGroup.OnButtonCheckedListener() {
        @Override
        public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
            if (!isChecked)
                return;

            for (int i = 0; i < group.getChildCount(); i++) {
                boolean iscontain = false;
                MaterialButton button = (MaterialButton) group.getChildAt(i);
                if (button.getId() == checkedId) {
                    String s = button.getText().toString();
                    String content = resultMap.get((String) group.getTag());
                    // 多选按钮一个都没被选中
                    if (content == null || content.equals("")) {
                        button.setBackgroundColor(context.getResources().getColor(R.color.button_selected));
                        resultMap.put((String) group.getTag(), s);
                        return;
                    }
                    String c[] = content.split("&");
                    content = "";
                    // 点击的按钮已经被选中
                    for (int j = 0; j < c.length; j++) {
                        if (c[j].equals(s)) {
                            iscontain = true;
                            button.setBackgroundColor(Color.WHITE);
                        } else {
                            content += c[j];
                            content += "&";
                        }
                    }
                    // 点击的按钮未被选中
                    if (!iscontain) {
                        content += s;
                        button.setBackgroundColor(context.getResources().getColor(R.color.button_selected));
                    }
                    resultMap.put((String) group.getTag(), content);
//                    Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    public void setData(List<CommonItemBean> data) {
        this.data = data;
    }

    Map<String, String> getResultMap() {
        return resultMap;
    }

    private void setViewBorder(boolean isRequire, View view) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        if (isRequire) {
            drawable.setStroke(5, Color.RED);
            drawable.setCornerRadius(10);
        } else {
            drawable.setStroke(3, context.getResources().getColor(R.color.border_color));
            drawable.setCornerRadius(10);
        }
    }

}
