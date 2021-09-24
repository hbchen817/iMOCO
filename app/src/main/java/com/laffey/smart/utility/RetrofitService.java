package com.laffey.smart.utility;

import com.alibaba.fastjson.JSONObject;
import com.laffey.smart.contract.Constant;
import com.laffey.smart.model.MacByIotIdRequest;
import com.laffey.smart.model.MacByIotIdResponse;
import com.laffey.smart.model.SceneListResponse;
import com.laffey.smart.model.TestResponse;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitService {

    // 根据iotId查询mac
    @POST(Constant.GET_MAC_BY_IOTID)
    Observable<MacByIotIdResponse> getMacByIotId(@Header("REX_TOKEN") String token, @Body MacByIotIdRequest requst);

    // 查询本地场景列表
    @POST(Constant.QUERY_SCENE_LIST)
    Observable<SceneListResponse> querySceneList(@Header("REX_TOKEN") String token, @Body RequestBody body);

    @FormUrlEncoded
    @POST("/query")
    Observable<JSONObject> test(@Field("type") String type, @Field("postid") String postid);
}
