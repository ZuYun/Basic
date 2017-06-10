package com.a4399.miniworld.data.netsource.urlapi;

import com.baselib.http.HttpResult;
import com.a4399.miniworld.data.bean.Daily;
import com.a4399.miniworld.data.bean.DailyList;
import com.a4399.miniworld.data.bean.GanHuoData;
import com.a4399.miniworld.data.bean.MeiZhi;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by _SOLID
 * Date:2016/8/3
 * Time:9:28
 */
public interface GankService {

    String BASE_URL = "http://www.gank.io/api/";

    /**
     * 获取发布干货的日期
     *
     * @return
     */
    @GET("day/history")
    Single<HttpResult<List<String>>> getRecentlyDate();


    /***
     * 根据类别查询干货
     *
     * @param category
     * @param pageIndex
     * @return
     */
    @GET("data/{category}/10/{pageIndex}")
    Single<HttpResult<List<GanHuoData>>> getGanHuo(@Path("category") String category
            , @Path("pageIndex") int pageIndex);

    /**
     * 获取某天的干货
     *
     * @param date
     * @return
     */
    @GET("day/{date}")
    Single<HttpResult<DailyList>> getRecentlyGanHuo(@Path("date") String date);


    @GET("history/content/10/{pageIndex}")
    Single<HttpResult<List<Daily>>> getRecently(@Path("pageIndex") int pageIndex);


    /***
     * 根据类别查询干货
     * @param pageIndex
     * @return
     */
    @GET("data/福利/20/{pageIndex}")
    Single<HttpResult<List<MeiZhi>>> getGanHuo(@Path("pageIndex") int pageIndex);
}
