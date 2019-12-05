package com.example.collectdata_01.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.R;
import com.example.map.dao.LocationAllDao;
import com.example.map.dao.DistrictDao;

public class GetDistrictViewAdapter extends RecyclerView.Adapter<GetDistrictViewAdapter.ViewHolder> {


    private Context context;
    private LayoutInflater inflater;
    private DistrictDao.DataBean dataBean;
    private MyItemClickListener mItemClickListener;

    public GetDistrictViewAdapter(Context context, DistrictDao.DataBean dataBean) {
        this.context = context;
        this.dataBean = dataBean;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.location_id_layout,parent,false),mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DistrictDao.DataBean.DistrictsBean districtsBean = dataBean.getDistricts().get(position);
        holder.locationName.setText(districtsBean.getName());
    }

    @Override
    public int getItemCount() {
        return dataBean.getDistricts().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationName;
        private MyItemClickListener mListener;

        public ViewHolder(@NonNull View itemView, MyItemClickListener listener) {
            super(itemView);
            locationName = itemView.findViewById(R.id.location_id_view);
            itemView.setOnClickListener(this);
            this.mListener = listener;
        }

        /**
         * 实现OnClickListener接口重写的方法
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getAdapterPosition());
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
        this.mItemClickListener = myItemClickListener;
    }
}
