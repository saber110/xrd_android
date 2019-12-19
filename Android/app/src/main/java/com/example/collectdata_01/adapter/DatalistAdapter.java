package com.example.collectdata_01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.Datalist;
import com.example.collectdata_01.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatalistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int layoutId;
    private List<String> datalist;
    private HashMap<String, Boolean> resultMap = new HashMap<>();
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener = null;
    private AdapterView.OnItemLongClickListener mOnItemLongClickListener = null;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnItemLongClickListener(AdapterView.OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    public DatalistAdapter(Context context, int layoutId, List<String> list) {
        this.context = context;
        this.layoutId = layoutId;
        datalist = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (String s : list) {
            resultMap.put(s, true);
        }
    }

    @Override
    public int getItemViewType(int p) {
//        picName = datalist.get(p);
        return 0;
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(layoutId, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        String pic_name = datalist.get(position);
        itemViewHolder.checkBox.setTag(pic_name);
        // 点击事件
        itemViewHolder.checkBox.setOnCheckedChangeListener(checkBox_listener);

        itemViewHolder.itemView.setTag(position);
        if (mOnItemClickListener != null) {
            itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (view.getTag() != null) {
                        int pos = (int) view.getTag();
                        mOnItemClickListener.onItemLongClick(pos);
                    }
                    // 屏蔽点击事件
                    return true;
                }
            });
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() != null) {
                        int pos = (int) view.getTag();
                        mOnItemClickListener.onItemClick(pos);
                    }
                }

            });
        }

        itemViewHolder.textView.setText(pic_name);
        if (resultMap.keySet().contains(pic_name)) {
            if (resultMap.get(pic_name) != null)
                itemViewHolder.checkBox.setChecked(resultMap.get(pic_name));
        }

        itemViewHolder.setIsRecyclable(false);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox checkBox;
        TextView textView;

        public ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.isUpload);
            textView = itemView.findViewById(R.id.pic_name);
//            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(checkBox_listener);

        }
    }

    public HashMap<String, Boolean> getResultMap() {
        return resultMap;
    }

    private CompoundButton.OnCheckedChangeListener checkBox_listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isSelected) {
            String str = (String) compoundButton.getTag();
            if (isSelected) {
//                        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                resultMap.put(str, true);
            } else {
//                        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
                resultMap.put(str, false);
            }
        }
    };

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

}
