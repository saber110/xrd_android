package com.example.collectdata_01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.FakeGardenBean;
import com.example.collectdata_01.R;

import java.util.List;

public class FakeGardenListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private int layoutId;
    private List<FakeGardenBean.DataBean.GardenListBean>  datalist;
    private LayoutInflater layoutInflater;
    private onItemClickListener mOnItemClickListener = null;

    public FakeGardenListAdapter(Context context, int layoutId, List<FakeGardenBean.DataBean.GardenListBean> dataList) {
        this.context = context;
        this.layoutId = layoutId;
        this.datalist = dataList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(layoutId, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        FakeGardenBean.DataBean.GardenListBean bean = datalist.get(position);
        viewHolder.gardenName.setText(bean.getGardenName());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(position);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mOnItemClickListener.onItemLongClick(position);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public void setDataList(List<FakeGardenBean.DataBean.GardenListBean> list) {
        this.datalist = list;
    }

    public List<FakeGardenBean.DataBean.GardenListBean> getDatalist() {
        return datalist;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView gardenName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gardenName = itemView.findViewById(R.id.gardenName);
        }
    }

    public void setmOnItemClickListener(onItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}

