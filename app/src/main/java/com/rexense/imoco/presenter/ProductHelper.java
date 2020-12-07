package com.rexense.imoco.presenter;

import android.content.Context;
import android.os.Handler;

import com.rexense.imoco.contract.Constant;
import com.rexense.imoco.model.EAPIChannel;
import com.rexense.imoco.model.EProduct;
import com.rexense.imoco.sdk.APIChannel;
import com.rexense.imoco.utility.Logger;

import java.util.List;


/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 产品助手
 */
public class ProductHelper {
    private Context mContext;

    // 构造
    public ProductHelper(Context context) {
        this.mContext = context;
    }

    // 获取支持配网产品列表
    public void getConfigureList(Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETCONFIGPROCDUCTLIST;
        requestParameterEntry.version = "1.1.3";
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETCONFIGPRODUCTLIST;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取配网引导信息
    public void getGuidanceInformation(String productKey, Handler commitFailureHandler,
                              Handler responseErrorHandler,
                              Handler processDataHandler) {
        if(processDataHandler == null){
            Logger.e("The processDataHandler!");
            return;
        }

        //设置请求参数
        EAPIChannel.requestParameterEntry requestParameterEntry = new EAPIChannel.requestParameterEntry();
        requestParameterEntry.path = Constant.API_PATH_GETGUIDANCEINFORMATION;
        requestParameterEntry.version = "1.1.3";
        requestParameterEntry.addParameter("productKey", productKey);
        requestParameterEntry.callbackMessageType = Constant.MSG_CALLBACK_GETGUIDANCEINFOMATION;
        //提交
        new APIChannel().commit(requestParameterEntry, commitFailureHandler, responseErrorHandler, processDataHandler);
    }

    // 获取产品名称
    public static String getProductName(String productKey, List<EProduct.configListEntry> productList){
        String name = "";
        for(EProduct.configListEntry product : productList){
            if(product.productKey.equalsIgnoreCase(productKey)){
                name = product.name;
                break;
            }
        }
        return name;
    }

    // 替换产品品牌
    public static String replaceBrand(String sourceContent){
        if(!SystemParameter.getInstance().getBrand().equalsIgnoreCase(SystemParameter.getInstance().getBrandShow())){
            if (sourceContent.indexOf(SystemParameter.getInstance().getBrand()+"-") == 0)
                return sourceContent.replace(SystemParameter.getInstance().getBrand() + "-", SystemParameter.getInstance().getBrandShow());
            return  sourceContent.replace(SystemParameter.getInstance().getBrand(), SystemParameter.getInstance().getBrandShow());
        } else {
            return sourceContent;
        }
    }
}
