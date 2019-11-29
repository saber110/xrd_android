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
    private TextView neighbourWorking;
    private Dialog dialog;
    private Integer gardenId;

    public GardenListAdapter(Context context, SearchGardenResultDao.DataBean result, TextView neighbourWorking, Dialog dialog, Integer gardenId) {
        this.context = context;
        this.result = result;
        this.dialog = dialog;
        this.neighbourWorking = neighbourWorking;
        this.gardenId = gardenId;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.graden_list_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final int name = result.getBuildingKinds().get(position).getKindName();
        holder.textView.setText(name+"");
        /**
         * 点击小区后进行跳转
         */
        holder.searchResultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                neighbourWorking.setText(name+"");
                dialog.cancel();
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
