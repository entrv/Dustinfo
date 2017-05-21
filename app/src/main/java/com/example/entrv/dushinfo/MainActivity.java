package com.example.entrv.dushinfo;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.entrv.dushinfo.adapter.DataAdapter;
import com.example.entrv.dushinfo.model.DustMeasureListInfo;
import com.example.entrv.dushinfo.network.MsrstnInfoInterface;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;


import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.entrv.dushinfo.R.id.where;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    //측정소별 실시간 측정정보 조회
    //http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=평리동&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=OKt6Cg7BORv%2BMXEq%2FTGWZNp9efdv3fqcsWLLLdfhrQCqnn6Ww%2BtmgelpRgNwUwMFF%2BdO1BI7svGpcExzogsLqw%3D%3D&_returnType=json
    public static final String BASE_URL = "http://openapi.airkorea.or.kr/";
    private RecyclerView mRecyclerView;

    private CompositeDisposable mCompositeDisposable;

    private DataAdapter mAdapter;

    private ArrayList<com.example.entrv.dushinfo.model.List> mAndroidArrayList;

    private static OkHttpClient client;

    static EditText where;
    static Spinner sido,station;	//스피너
    static String sidolist[]={"서울","부산","대전","대구","광주","울산","경기","강원","충북","충남","경북","경남","전북","전남","제주"};
    static String stationlist[];	//측정소목록(이건 api로 가져올꺼라 몇개인지 모른다)
    static ArrayAdapter<String> spinnerSido,spinnerStation;	//spinner에 붙일 array adapter


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCompositeDisposable = new CompositeDisposable();
        init();
        initRecyclerView();

    }

    private void init() {
        where=(EditText)findViewById(R.id.where);
        sido=(Spinner)findViewById(R.id.sido);	//시도 스피너
        station=(Spinner)findViewById(R.id.station);	//측정소 스피너
        sido.setOnItemSelectedListener(this);	//스피너 선택할때 작동시킬 리스너등록
        station.setOnItemSelectedListener(this);
        spinnerSido=new ArrayAdapter<>(getApplication(), R.layout.spinner_text,sidolist);	//array adapter에 시도 리스트를 넣어줌
        sido.setAdapter(spinnerSido);	//스피너에 adapter를 연결
    }

    public void onClick(View v) {

        switch(v.getId()){

            case R.id.getBtn:	//대기정보 가져오는 버튼
                String stationName;
                stationName=where.getText().toString();
                //getFindDust(stationName);

                break;
            case R.id.getNearStation:
                //mGoogleApiClient.connect();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch(parent.getId()){

            case R.id.sido:		//시도 변경 스피너
                getStationList(sidolist[position]);
                Log.d("entrv",">>" + sidolist[position]);


                break;
            case R.id.station:	//측정소 변경 스피너
                try{
                    Log.e("station name", stationlist[position]);
                }catch (Exception e){
                    Log.e("exception",""+e);
                }

                where.setText(stationlist[position]);	//측정소이름을 바로 입력해 준다.

                break;


            default:
                break;

        }


    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void initRecyclerView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
    }


    public void getStationList(String name){	//이건 측정소 정보가져올 스레드
        //GetStationListThread.active=true;
        //GetStationListThread getstationthread=new GetStationListThread(false,name);		//스레드생성(UI 스레드사용시 system 뻗는다)
        //getstationthread.start();	//스레드 시작

// init okhttp 3 logger
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
            @Override public void log(String message) {
                Log.d("MyTAG", "OkHttp: " + message);
            }
        });
       // HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
// set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!


        MsrstnInfoInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MsrstnInfoInterface.class);

        mCompositeDisposable.add(requestInterface.getMsrstnList(name,null,String.valueOf("1"),String.valueOf("100"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(DustMeasureListInfo dustMeasureListInfo) {

        mAndroidArrayList = new ArrayList<>(dustMeasureListInfo.getList());
        mAdapter = new DataAdapter(mAndroidArrayList);
        mRecyclerView.setAdapter(mAdapter);


        stationlist=new String[dustMeasureListInfo.getTotalCount()];
        Iterator<com.example.entrv.dushinfo.model.List> iterator = dustMeasureListInfo.getList().iterator();
        int data = 0;
        while (iterator.hasNext()) {
            com.example.entrv.dushinfo.model.List element = iterator.next();
            stationlist[data] = element.getStationName();
            data++;
        }


        //if(stationCnt!=0){
        spinnerStation=new ArrayAdapter<>(getApplication(),R.layout.spinner_text,stationlist);
        station.setAdapter(spinnerStation);
        //}

    }

    private void handleError(Throwable error) {

        Toast.makeText(this, "Error " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
