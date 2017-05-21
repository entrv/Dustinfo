package com.example.entrv.dushinfo.network;

import com.example.entrv.dushinfo.model.DustMeasureListInfo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Created by entrv on 2017-05-20.
 */

public interface MsrstnInfoInterface {
    //측정소정보 조회 서비스 Call<List<Task>> getTasks(@Query("sort") String order);

    //http://openapi.airkorea.or.kr/openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?addr=서울&stationName=&pageNo=1&numOfRows=100&ServiceKey=OKt6Cg7BORv%2BMXEq%2FTGWZNp9efdv3fqcsWLLLdfhrQCqnn6Ww%2BtmgelpRgNwUwMFF%2BdO1BI7svGpcExzogsLqw%3D%3D&_returnType=json
    //
@GET("openapi/services/rest/MsrstnInfoInqireSvc/getMsrstnList?ServiceKey=OKt6Cg7BORv%2BMXEq%2FTGWZNp9efdv3fqcsWLLLdfhrQCqnn6Ww%2BtmgelpRgNwUwMFF%2BdO1BI7svGpcExzogsLqw%3D%3D&_returnType=json")
Observable<DustMeasureListInfo> getMsrstnList(
        @Query("addr") String addr,
        @Query("stationName") String stationName,
        @Query("pageNo") String pageNo,
        @Query("numOfRows") String numOfRows
        );
}
