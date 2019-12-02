package com.example.collectdata_01.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.R;
import com.example.map.dao.SearchGardenResultDao;

public class GardenListAdapter extends RecyclerView.Adapter<GardenListAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private SearchGardenResultDao.DataBean result;
    private MyItemClickListener mListener;

    public GardenListAdapter(Context context, SearchGardenResultDao.DataBean result) {
        this.context = context;
        this.result = result;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.graden_list_layout, parent, false), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Integer name = result.getBuildingKinds().get(position).getKindName();
        holder.textView.setText(name.toString());
    }

    @Override
    public int getItemCount() {
        return result.getBuildingKinds().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private MyItemClickListener listener;

        public ViewHolder(@NonNull View itemView, MyItemClickListener listener) {
            super(itemView);
            textView = itemView.findViewById(R.id.search_result);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        /**
         * 实现OnClickListener接口重写的方法
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemClick(v, getAdapterPosition());
            }

        }
    }


    /**
     * 创建一个回调接口
     */
    public interface MyItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * 在activity里面adapter就是调用的这个方法,将点击事件监听传递过来,并赋值给全局的监听
     *
     * @param myItemClickListener
     */
    public void setItemClickListener(MyItemClickListener myItemClickListener) {
        this.mListener = myItemClickListener;
    }
}
