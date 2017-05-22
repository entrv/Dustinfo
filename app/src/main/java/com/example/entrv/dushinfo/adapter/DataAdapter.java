package com.example.entrv.dushinfo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.entrv.dushinfo.R;

import java.util.ArrayList;

/**
 * Created by entrv on 2017-05-20.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<com.example.entrv.dushinfo.model.dustrealtime.List> mAndroidList;

    public DataAdapter(ArrayList<com.example.entrv.dushinfo.model.dustrealtime.List> dustMeasureListInfo) {
        mAndroidList = dustMeasureListInfo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.date.setText(mAndroidList.get(position).getDataTime());
        holder.so2value.setText(mAndroidList.get(position).getSo2Value() +"ppm");
        holder.covalue.setText(mAndroidList.get(position).getCoValue() +"ppm"); //아황산가스 농도
        holder.no2value.setText(mAndroidList.get(position).getNo2Value()+"ppm");
        holder.o3value.setText(mAndroidList.get(position).getO3Value() +"ppm");
        holder.pm10value.setText(mAndroidList.get(position).getPm10Value()+"μg/m³");
        holder.khaivalue.setText(transGrade(mAndroidList.get(position).getKhaiValue()));
        holder.so2grade.setText(transGrade(mAndroidList.get(position).getSo2Grade()));
        holder.so2value.setText(transGrade(mAndroidList.get(position).getSo2Value()));
        holder.o3grade.setText(transGrade(mAndroidList.get(position).getO3Grade()) );
        holder.pm10grade.setText(transGrade(mAndroidList.get(position).getPm10Grade()));
    }

    static public String transGrade(String intGrade){
        String trans=null;
        switch (intGrade){
            case "1":
                trans="좋음";
                break;
            case "2":
                trans="보통";
                break;
            case "3":
                trans="나쁨";
                break;
            case "4":
                trans="매우나쁨";
                break;
            default:
                break;
        }
        return trans;
    }
    @Override
    public int getItemCount() {
        return mAndroidList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView date, so2value, covalue, o3value, no2value, pm10value, khaivalue, so2grade, o3grade, pm10grade;

        public ViewHolder(View view) {
            super(view);

            date = (TextView) view.findViewById(R.id.date);
            so2value = (TextView) view.findViewById(R.id.so2value);
            covalue = (TextView) view.findViewById(R.id.covalue);
            o3value = (TextView) view.findViewById(R.id.o3value);
            no2value = (TextView) view.findViewById(R.id.no2value);
            pm10value = (TextView) view.findViewById(R.id.pm10value);
            khaivalue = (TextView) view.findViewById(R.id.khaivalue);
            so2grade = (TextView) view.findViewById(R.id.so2grade);
            o3grade = (TextView) view.findViewById(R.id.o3grade);
            pm10grade = (TextView) view.findViewById(R.id.pm10grade);

        }
    }
}
