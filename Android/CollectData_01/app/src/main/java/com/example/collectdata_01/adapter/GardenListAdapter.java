package com.example.collectdata_01.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.collectdata_01.R;
import com.example.map.baidu_map.BaiduMapActivity;
import com.example.map.dao.SearchGardenResultDao;

public class GardenListAdapter extends RecyclerView.Adapter<GardenListAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private SearchGardenResultDao.DataBean result;

    public GardenListAdapter(Context context, SearchGardenResultDao.DataBean result) {
        this.context = context;
        this.result = result;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.graden_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        String name = result.getBuildingKinds().get(position).getKindName()+"";
        final String id = result.getBuildingKinds().get(position).getId();
        holder.textView.setText(name);
        /**
         * 点击小区后进行跳转
         */
        holder.searchResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BaiduMapActivity.class);
                intent.putExtra("gardenId",id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.getBuildingKinds().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private View searchResultView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.search_result);
            searchResultView = itemView.findViewById(R.id.search_result_view);
        }
    }
}
