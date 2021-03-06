package com.example.collectdata_01.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.Datalist;
import com.example.collectdata_01.R;
import com.example.database.ImageDb;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.collectdata_01.Datalist.pictures;
import static com.example.collectdata_01.MainActivity.mainDB;

public class DatalistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int layoutId;
    private List<String> datalist;
    private HashMap<String, Boolean> resultMap = new HashMap<>();
    private HashMap<String,String> downloadMap = new HashMap<>();
    private LayoutInflater layoutInflater;
    private OnItemClickListener mOnItemClickListener = null;
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    public DatalistAdapter(Context context, int layoutId, List<String> list) {
        this.context = context;
        this.layoutId = layoutId;
        datalist = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(String id:pictures.keySet()){
            for(String name:pictures.get(id)){
                resultMap.put(name, true);
            }
        }
        for (String s : list) {
            System.out.print(s+" ");
            downloadMap.put(s,"准备上传");
        }
        System.out.println("");
    }

    public void setProcess(String pic_name, String status){
        for(int i=0;i<datalist.size();i++){
            if(datalist.get(i).equals(pic_name)){
                downloadMap.put(pic_name,status);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int p) {
        try{
            Integer.parseInt(datalist.get(p));
            return 0;
        }catch (Exception e){
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1){
            View view = layoutInflater.inflate(layoutId, parent, false);
            return new ItemViewHolder(view);
        }
        else {
            View view = layoutInflater.inflate(R.layout.datalist_item2, parent, false);
            return new ItemViewHolder2(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final String pic_name = datalist.get(position);
        if (holder instanceof ItemViewHolder2){
            ItemViewHolder2 itemViewHolder = (ItemViewHolder2) holder;
            ArrayList<ImageDb> queryGardenName = mainDB.query(new QueryBuilder<ImageDb>(ImageDb.class)
                    .whereEquals(ImageDb.GARDENID_COL, pic_name));
            itemViewHolder.textView.setText(queryGardenName.get(0).getGardenName());
            itemViewHolder.textView.setTextColor(Color.rgb(88,88,88));
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mOnItemClickListener.onItemClick(position,true);
                }
            });
            return;
        }
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.textView.setText(pic_name);
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
                        mOnItemClickListener.onItemClick(pos,false);
                    }
                }

            });
        }
        //itemViewHolder.textView.setText(pic_name);
        //itemViewHolder.textView2.setText(downloadMap.get(pic_name));
        String status = downloadMap.get(pic_name);
        if("上传中".equals(status)){
            itemViewHolder.imageView.setImageResource(R.drawable.process_loading);
        }
        else if("上传成功".equals(status)){
            itemViewHolder.imageView.setImageResource(R.drawable.process_success);
        }
        else if("上传失败".equals(status)){
            itemViewHolder.imageView.setImageResource(R.drawable.process_failure);
        }
        if (resultMap.keySet().contains(pic_name)) {
            if (resultMap.get(pic_name) != null)
                itemViewHolder.checkBox.setChecked(resultMap.get(pic_name));
        }

        itemViewHolder.setIsRecyclable(false);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        MaterialCheckBox checkBox;
        TextView textView;//textView2;
        ImageView imageView;

        public ItemViewHolder(@NonNull final View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.isUpload);
            textView = itemView.findViewById(R.id.pic_name);
            imageView = itemView.findViewById(R.id.process);
//            textView2 = itemView.findViewById(R.id.process);
//            checkBox.setChecked(true);
            checkBox.setOnCheckedChangeListener(checkBox_listener);

        }
    }

    class ItemViewHolder2 extends RecyclerView.ViewHolder {
        TextView textView;
        public ItemViewHolder2(@NonNull final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.garden_name);
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
        void onItemClick(int position,boolean flag);
        void onItemLongClick(int position);
    }

}
