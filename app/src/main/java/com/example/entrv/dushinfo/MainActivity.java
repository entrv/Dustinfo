package com.example.entrv.dushinfo;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.entrv.dushinfo.adapter.DataAdapter;
import com.example.entrv.dushinfo.model.dustmeasure.DustMeasureListInfo;
import com.example.entrv.dushinfo.model.dustnearby.Dustnearbyinfo;
import com.example.entrv.dushinfo.model.dustrealtime.DustrealtimeInfo;
import com.example.entrv.dushinfo.network.MsrstnInfoInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,
        LocationListener
{
    //측정소별 실시간 측정정보 조회
    //http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?stationName=평리동&dataTerm=month&pageNo=1&numOfRows=10&ServiceKey=OKt6Cg7BORv%2BMXEq%2FTGWZNp9efdv3fqcsWLLLdfhrQCqnn6Ww%2BtmgelpRgNwUwMFF%2BdO1BI7svGpcExzogsLqw%3D%3D&_returnType=json
    public static final String BASE_URL = "http://openapi.airkorea.or.kr/";

    private RecyclerView mRecyclerView;

    private CompositeDisposable mCompositeDisposable;

    private DataAdapter mAdapter;

    private ArrayList<com.example.entrv.dushinfo.model.dustrealtime.List> mAndroidArrayList;



    static EditText where;
    static Spinner sido,station;	//스피너
    static String sidolist[]={"서울","부산","대전","대구","광주","울산","경기","강원","충북","충남","경북","경남","전북","전남","제주"};
    static String stationlist[];	//측정소목록(이건 api로 가져올꺼라 몇개인지 모른다)
    static ArrayAdapter<String> spinnerSido,spinnerStation;	//spinner에 붙일 array adapter
    static Button getNearStation, getBtn;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private final int REQUEST_PERMISSION = 10;

    private LocationRequest locationRequest;
    private Location location;
    private long lastLocationTime = 0;

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
        getNearStation = (Button) findViewById(R.id.getNearStation);
        getNearStation.setOnClickListener(this);
        getBtn = (Button) findViewById(R.id.getBtn);
        getBtn.setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)	//google service
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(16);
    }

    // permission check
    public void checkPermission() {

        // 허가 여부
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationActivity();
        }
        // 아니면
        else{
            requestLocationPermission();
        }
    }

    // 허가를 구하는
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this, "허가되지 않았고 애플리가 실행되지 않았습니다.", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,}, REQUEST_PERMISSION);

        }
    }

    // 결과의 수용
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationActivity();
                return;

            } else {
                // 그래도 거부되었을 때의 대응
                Toast toast = Toast.makeText(this, "더 이상 아무것도 할 수 없습니다", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void locationActivity() {
        Log.d("entrv","locationActivity");
        mGoogleApiClient.connect();
    }

    public  void getNearStation(double yGrid,double xGrid){	//이건 측정소 정보가져올 스레드
/*
        OkHttpClient httpClient = new OkHttpClient.Builder()

                //here we can add Interceptor for dynamical adding headers
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("test", "test").build();
                        return chain.proceed(request);
                    }
                })
                //here we adding Interceptor for full level logging
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        */

        MsrstnInfoInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MsrstnInfoInterface.class);

        mCompositeDisposable.add(requestInterface.getNearbyMsrstnList(xGrid, yGrid, String.valueOf("1"),String.valueOf("100"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::ResponseNearbyMsrstnList, this::handleError));


    }
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.getBtn:	//대기정보 가져오는 버튼
                String stationName;
                stationName=where.getText().toString();
                Log.d("entrv","getFindDust:" + stationName);
                getFindDust(stationName);

                break;
            case R.id.getNearStation:
                if(Build.VERSION.SDK_INT >= 23){
                    checkPermission();
                }
                else{
                    locationActivity();
                }

                //
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

    public void getFindDust(String name){	//이건 미세먼지 보기 정보가져올 스레드
                //GetStationListThread.active=true;
        //GetStationListThread getstationthread=new GetStationListThread(false,name);		//스레드생성(UI 스레드사용시 system 뻗는다)
        //getstationthread.start();	//스레드 시작



        MsrstnInfoInterface requestInterface = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                //.client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(MsrstnInfoInterface.class);

        mCompositeDisposable.add(requestInterface.getMsrstnAcctoRltmMesureDnsty(name,String.valueOf("1"),String.valueOf("100"))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::ResponseMsrstnAcctoRltmMesureDnsty, this::handleError));
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

        stationlist=new String[dustMeasureListInfo.getTotalCount()];
        Iterator<com.example.entrv.dushinfo.model.dustmeasure.List> iterator = dustMeasureListInfo.getList().iterator();
        int data = 0;
        while (iterator.hasNext()) {
            com.example.entrv.dushinfo.model.dustmeasure.List element = iterator.next();
            stationlist[data] = element.getStationName();
            data++;
        }


        //if(stationCnt!=0){
        spinnerStation=new ArrayAdapter<>(getApplication(),R.layout.spinner_text,stationlist);
        station.setAdapter(spinnerStation);
        //}

    }
    private void ResponseNearbyMsrstnList(Dustnearbyinfo dustnearbyinfo) {
         ArrayList<com.example.entrv.dushinfo.model.dustnearby.List> mAndroidArrayList =
                 new ArrayList<>(dustnearbyinfo.getList());
        Log.d("entrv","ResponseNearbyMsrstnList=>" + mAndroidArrayList.get(0).getStationName() );
        where.setText(mAndroidArrayList.get(0).getStationName());	//측정소이름을 바로 입력해 준다.



    }
    private void ResponseMsrstnAcctoRltmMesureDnsty(DustrealtimeInfo dustrealtimeInfo) {
        mAndroidArrayList = new ArrayList<>(dustrealtimeInfo.getList());
        mAdapter = new DataAdapter(mAndroidArrayList);
        mRecyclerView.setAdapter(mAdapter);

    }


    private void handleError(Throwable error) {

        Toast.makeText(this, "Error " + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnected(Bundle bundle) {

// 허가 여부
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d("mLastLocation", String.valueOf(mLastLocation.getLatitude()) + "," + mLastLocation.getLongitude());
            if (mLastLocation != null && mLastLocation.getTime() > 20000) {
                if (mLastLocation != null) {
                    //totalcnt.setText(String.valueOf(mLastLocation.getLatitude()) + "," + mLastLocation.getLongitude());
                    getStation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {
                    //totalcnt.setText("위치를 알 수 없습니다.");
                    Toast.makeText(getApplication(), "위치를 알 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // 백그라운드에서 돌아 버리면 예외가 발생할 수있다
                try {
                    //
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                    // Schedule a Thread to unregister location listeners
                    Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                        @Override
                        public void run() {
                            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, MainActivity.this);
                        }
                    }, 60000, TimeUnit.MILLISECONDS);

                   // "onConnected(), requestLocationUpdates \n";


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(this, "예외가 발생 위치 정보의 Permission를 허용합니까?", Toast.LENGTH_SHORT);
                    toast.show();


                }
            }
            mGoogleApiClient.disconnect();
        }

    }
    void getStation(double lat,double lng){

        if(lat != 0 && lng != 0){
            GeoPoint in_pt = new GeoPoint( lng, lat);
            System.out.println("geo in : xGeo="  + in_pt.getX() + ", yGeo=" + in_pt.getY());
            GeoPoint tm_pt = GeoTrans.convert(GeoTrans.GEO, GeoTrans.TM, in_pt);
            System.out.println("tm : xTM=" + tm_pt.getX() + ", yTM=" + tm_pt.getY());
            getNearStation(tm_pt.getY(), tm_pt.getX());
        }else{
            Toast.makeText(getApplication(), "좌표값 잘못 되었습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocationTime = location.getTime() - lastLocationTime;
        String textLog = "";
        textLog += "---------- onLocationChanged \n";
        textLog += "Latitude=" + String.valueOf(location.getLatitude()) + "\n";
        textLog += "Longitude=" + String.valueOf(location.getLongitude()) + "\n";
        textLog += "Accuracy=" + String.valueOf(location.getAccuracy()) + "\n";
        textLog += "Altitude=" + String.valueOf(location.getAltitude()) + "\n";
        textLog += "Time=" + String.valueOf(location.getTime()) + "\n";
        textLog += "Speed=" + String.valueOf(location.getSpeed()) + "\n";
        textLog += "Bearing=" + String.valueOf(location.getBearing()) + "\n";
        textLog += "time= " + String.valueOf(lastLocationTime) + " msec \n";
        //textView.setText(textLog);
        Toast.makeText(getApplication(), "onLocationChanged", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

}
