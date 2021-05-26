package com.rexense.wholehouse.utility;

import android.content.Context;

import com.jdsh.sdk.ir.JdshIRInterfaceImpl;
import com.jdsh.sdk.ir.model.Brand;
import com.jdsh.sdk.ir.model.BrandResult;
import com.jdsh.sdk.ir.model.DeviceType;
import com.jdsh.sdk.ir.model.DeviceTypeResult;
import com.jdsh.sdk.ir.model.MatchRemoteControl;
import com.jdsh.sdk.ir.model.MatchRemoteControlResult;
import com.jdsh.sdk.ir.model.RemoteControlResult;
import com.rexense.wholehouse.contract.Constant;
import com.rexense.wholehouse.presenter.MocoApplication;
import com.vise.log.ViseLog;

import java.util.List;

public class JDInterfaceImplUtil {
    private static JDInterfaceImplUtil instance;
    private Context context;
    private JdshIRInterfaceImpl jdshIRInterface;

    public JDInterfaceImplUtil(Context context) {
        context = context;
        jdshIRInterface = new JdshIRInterfaceImpl(context, Constant.JD_URL, Constant.JD_APP_ID, Constant.JD_DEV_ID);
    }

    public static JDInterfaceImplUtil getInstance() {
        if (instance == null) {
            instance = new JDInterfaceImplUtil(MocoApplication.sContext);
        }
        return instance;
    }

    public void registerDevice() {
        String s = jdshIRInterface.registerDevice();
        ViseLog.d("registerDevice = " + s);
    }

    public DeviceTypeResult getDeviceTypeResult() {
        return jdshIRInterface.getDeviceType();
    }

    public List<DeviceType> getDeviceTypes() {
        return getDeviceTypeResult().getRs();
    }

    public BrandResult getBrandResult(int devType) {
        return jdshIRInterface.getBrandsByType(devType);
    }

    public List<Brand> getBrands(int devType) {
        return jdshIRInterface.getBrandsByType(devType).getRs();
    }

    public MatchRemoteControlResult getRemoteMatched(int bid, int tid, int codeVer, int zip) {
        return jdshIRInterface.getRemoteMatched(bid, tid, codeVer, zip);
    }

    public List<MatchRemoteControl> getRemoteControls(int bid, int tid, int codeVer, int zip) {
        return getRemoteMatched(bid, tid, codeVer, zip).getRs();
    }
}
