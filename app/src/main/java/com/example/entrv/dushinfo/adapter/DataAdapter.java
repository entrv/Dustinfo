package com.example.entrv.dushinfo.adapter;

import android.graphics.Color;
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

    public  void setTextViewBackgroundColor(TextView view, String str, String ppm){
        int pmValue = -1;
        try{
            pmValue = Integer.parseInt(str);
            view.setText(String.format("%d " + ppm, pmValue) );
        }catch(Exception e){
            e.printStackTrace();
            view.setText("No Data");
        }
        if(pmValue>151)
            view.setBackgroundColor(Color.rgb(236,61,61));
        else if(pmValue>81)
            view.setBackgroundColor(Color.rgb(239,239,71));
        else if(pmValue>31)
            view.setBackgroundColor(Color.rgb(71,227,134));
        else if(pmValue >0)
            view.setBackgroundColor(Color.rgb(80,100,254));
        else{
            view.setBackgroundColor(Color.rgb(222,222,222));
        }
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //TextView date, so2value, covalue, o3value, no2value, pm10value, khaivalue;
        //TextView khaigrade, so2grade, cograde, o3grade, no2grade, pm10grade;
        holder.date.setText(mAndroidList.get(position).getDataTime());
        holder.so2value.setText(mAndroidList.get(position).getSo2Value() +"ppm");
        holder.covalue.setText(mAndroidList.get(position).getCoValue() +"ppm"); //아황산가스 농도

        holder.o3value.setText(mAndroidList.get(position).getO3Value() +"ppm");
        holder.no2value.setText(mAndroidList.get(position).getNo2Value()+"ppm");
        holder.pm10value.setText(mAndroidList.get(position).getPm10Value()+"μg/m³");
        holder.khaivalue.setText(mAndroidList.get(position).getKhaiValue());
        holder.khaigrade.setText(mAndroidList.get(position).getKhaiGrade());
        holder.so2grade.setText(transGrade(mAndroidList.get(position).getSo2Grade()));
        holder.covalue.setText(mAndroidList.get(position).getCoValue());
        holder.o3grade.setText(transGrade(mAndroidList.get(position).getO3Grade()) );
        holder.no2grade.setText(transGrade(mAndroidList.get(position).getNo2Grade()) );
        holder.pm10grade.setText(transGrade(mAndroidList.get(position).getPm10Grade()));


//        setTextViewBackgroundColor(holder.so2value, mAndroidList.get(position).getSo2Value() ,"ppm");
//        setTextViewBackgroundColor(holder.covalue, mAndroidList.get(position).getCoValue() ,"ppm");
//        setTextViewBackgroundColor(holder.no2value, mAndroidList.get(position).getNo2Value() ,"ppm");
//        setTextViewBackgroundColor(holder.o3value, mAndroidList.get(position).getO3Value() ,"ppm");
//        setTextViewBackgroundColor(holder.pm10value, mAndroidList.get(position).getPm10Value() ,"ppm");
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

        private TextView date, so2value, covalue, o3value, no2value, pm10value, khaivalue;
        private TextView khaigrade, so2grade, cograde, o3grade, no2grade, pm10grade;

        public ViewHolder(View view) {
            super(view);

            date = (TextView) view.findViewById(R.id.date);
            so2value = (TextView) view.findViewById(R.id.so2value);
            covalue = (TextView) view.findViewById(R.id.covalue);
            o3value = (TextView) view.findViewById(R.id.o3value);
            no2value = (TextView) view.findViewById(R.id.no2value);
            pm10value = (TextView) view.findViewById(R.id.pm10value);
            khaivalue = (TextView) view.findViewById(R.id.khaivalue);
            khaigrade = (TextView) view.findViewById(R.id.khaigrade);
            so2grade = (TextView) view.findViewById(R.id.so2grade);
            cograde = (TextView) view.findViewById(R.id.cograde);
            o3grade = (TextView) view.findViewById(R.id.o3grade);
            no2grade = (TextView) view.findViewById(R.id.no2grade);
            pm10grade = (TextView) view.findViewById(R.id.pm10grade);

        }
    }
}
