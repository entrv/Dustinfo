package com.example.entrv.dushinfo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.entrv.dushinfo.R;
import com.example.entrv.dushinfo.model.DustMeasureListInfo;

import java.util.ArrayList;

/**
 * Created by entrv on 2017-05-20.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<com.example.entrv.dushinfo.model.List> mAndroidList;

    public DataAdapter(ArrayList<com.example.entrv.dushinfo.model.List> dustMeasureListInfo) {
        mAndroidList = dustMeasureListInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTvStationName.setText(mAndroidList.get(position).getStationName());
        holder.mTvAddr.setText(mAndroidList.get(position).getAddr());
        holder.mTvYear.setText(mAndroidList.get(position).getYear());
    }


    @Override
    public int getItemCount() {
        return mAndroidList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvStationName, mTvAddr, mTvYear;

        public ViewHolder(View view) {
            super(view);

            mTvStationName = (TextView) view.findViewById(R.id.tv_stationName);
            mTvAddr = (TextView) view.findViewById(R.id.tv_addr);
            mTvYear = (TextView) view.findViewById(R.id.tv_year);
        }
    }
}