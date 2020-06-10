package com.rexense.imoco.contract;

/**
 * Creator: xieshaobing
 * creat time: 2020-03-31 15:29
 * Description: 常量
 */
public class Constant {
    // 定义设备类型常量
    public static final String NODETYPE_GATEWAY                     = "GATEWAY";
    public static final int DEVICETYPE_GATEWAY                      = 1;

    // 定义内容类型
    public static final int CONTENTTYPE_PROPERTY                    = 1;
    public static final int CONTENTTYPE_EVENT_ALERT                 = 2;
    public static final int CONTENTTYPE_EVENT_INFO                  = 3;

    // 定义设备连接状态
    public static final int ADD_STATUS_SUCCESS                      = 0;
    public static final int ADD_STATUS_OTHERBIND                    = 1;
    public static final int ADD_STATUS_FAIL                         = 2;
    public static final int ADD_STATUS_LOGOUT                       = 3;

    // 定义设备连接状态
    public static final int CONNECTION_STATUS_UNABLED               = 0;
    public static final int CONNECTION_STATUS_ONLINE                = 1;
    public static final int CONNECTION_STATUS_OFFLINE               = 3;
    public static final int CONNECTION_STATUS_PROHIBIT              = 8;

    // 定义请求码
    public static final int REQUESTCODE_CALLMOREACTIVITY            = 1;
    public static final int REQUESTCODE_CALLCHOICECONTENTACTIVITY   = 2;
    public static final int REQUESTCODE_CALLCHOICEWIFIACTIVITY      = 3;

    // 定义结果码
    public static final int RESULTCODE_CALLMOREACTIVITYUNBIND       = 1000;
    public static final int RESULTCODE_CALLCHOICECONTENTACTIVITY    = 1001;
    public static final int RESULTCODE_CALLCHOICEWIFIACTIVITY       = 1002;

    // 定义API PATH常量
    public static final String API_PATH_GETCONFIGPROCDUCTLIST       = "/thing/productInfo/getByAppKey";
    public static final String API_PATH_GETGUIDANCEINFORMATION      = "/awss/enrollee/guide/get";
    public static final String API_PATH_CREATEHOME                  = "/living/home/create";
    public static final String API_PATH_GETHOMELIST                 = "/living/home/query";
    public static final String API_PATH_GETHOMEROOMLIST             = "/living/home/room/query";
    public static final String API_PATH_GETHOMEDEVICELIST           = "/living/home/device/query";
    public static final String API_PATH_BINDDEVICE                  = "/awss/token/user/bind";
    public static final String API_PATH_PERMITJOINSUBDEVICE         = "/thing/gateway/permit";
    public static final String API_PATH_BINDSUBDEVICE               = "/awss/time/window/user/bind";
    public static final String API_PATH_UNBINDDEVICE                = "/uc/unbindAccountAndDev";
    public static final String API_PATH_GETTSLPROPERTY              = "/thing/properties/get";
    public static final String API_PATH_GETTSLPROPERTYTIMELINEDATA  = "/living/device/property/timeline/get";
    public static final String API_PATH_GETTSLEVENTTIMELINEDATA     = "/living/device/event/timeline/get";
    public static final String API_PATH_SETTSLPROPERTY              = "/thing/properties/set";
    public static final String API_PATH_GETGATEWAYSUBDEVICELIST     = "/subdevices/list";
    public static final String API_PATH_GETUSERDEVICELIST           = "/uc/listBindingByAccount";
    public static final String API_PATH_SETDEVICENICKNAME           = "/uc/setDeviceNickName";
    public static final String API_PATH_UPDATEDEVICEROOM            = "/living/home/room/device/full/update";
    public static final String API_PATH_GETOTAFIRMWAREINFO          = "/thing/ota/info/queryByUser";
    public static final String API_PATH_GETTHINGBASEINFORMATION     = "/thing/info/get";
    public static final String API_PATH_GETSCENELIST                = "/scene/list/get";

    // 定义插件URL常量
    public static final String PLUGIN_URL_COUNDTIMER                ="link://router/cloudtime";
    public static final String PLUGIN_URL_SCENE                     ="link://router/scene";

    // 定义API返加代码常量
    public static final int API_CODE_SUCCESS                        = 200;

    // 定义长连接MQTT主题
    public static final String TOPIC_PROPERTYNOTIFY                 = "/thing/properties";
    public static final String TOPIC_EVENTNOTIFY                    = "/thing/events";
    public static final String TOPIC_STATUSNOTIFY                   = "/thing/status";
    public static final String TOPIC_SUBDEVICEJOINNOTIFY            = "/thing/topo/add/status";
    public static final String TOPIC_OTAUPGRADENOTITY               = "/app/down/ota/device/forward";

    // 定义一般消息常量
    public static final String API_MESSAGE_SUCCESS                  = "success";
    public static final int MSG_FAILURECODE                         = 0;
    public static final int MSG_SUCCESSCODE                         = 1;
    public static final int MSG_POSTLOGINPORCESS                    = 2;
    public static final int MSG_DOWNLOADIMAGE                       = 3;
    public static final int MSG_PARSE_CONFIGNETWORKFRAME            = 4;
    public static final int MSG_DIALOG_TWOCHOICEONE                 = 5;

    // 定义回调消息常量
    public static final int MSG_CALLBACK_APICOMMITFAIL              = 100;
    public static final int MSG_CALLBACK_APIRESPONSEERROR           = 101;
    public static final int MSG_CALLBACK_CREATEHOME                 = 102;
    public static final int MSG_CALLBACK_GETHOMELIST                = 103;
    public static final int MSG_CALLBACK_GETHOMEROOMLIST            = 104;
    public static final int MSG_CALLBACK_GETCONFIGPRODUCTLIST       = 105;
    public static final int MSG_CALLBACK_GETGUIDANCEINFOMATION      = 106;
    public static final int MSG_CALLBACK_GETHOMEDEVICELIST          = 107;
    public static final int MSG_CALLBACK_GETHOMEGATWAYLIST          = 108;
    public static final int MSG_CALLBACK_BINDEVICE                  = 109;
    public static final int MSG_CALLBACK_UNBINDEVICE                = 110;
    public static final int MSG_CALLBACK_PERMITJOINSUBDEVICE        = 111;
    public static final int MSG_CALLBACK_BINDSUBDEVICE              = 112;
    public static final int MSG_CALLBACK_GETTSLPROPERTY             = 113;
    public static final int MSG_CALLBACK_GETTSLPROPERTYTIMELINEDATA = 114;
    public static final int MSG_CALLBACK_GETTSLEVENTTIMELINEDATA    = 115;
    public static final int MSG_CALLBACK_GETGATEWAYSUBDEVICTLIST    = 116;
    public static final int MSG_CALLBACK_GETUSERDEVICTLIST          = 117;
    public static final int MSG_CALLBACK_SETDEVICENICKNAME          = 118;
    public static final int MSG_CALLBACK_UPDATEDEVICEROOM           = 119;
    public static final int MSG_CALLBACK_GETOTAFIRMWAREINFO         = 120;
    public static final int MSG_CALLBACK_GETTHINGBASEINFO           = 121;
    public static final int MSG_CALLBACK_GETSCENELIST               = 122;
    public static final int MSG_CALLBACK_LNCONNECTSTATENOTIFY       = 200;
    public static final int MSG_CALLBACK_LNPROPERTYNOTIFY           = 201;
    public static final int MSG_CALLBACK_LNEVENTNOTIFY              = 202;
    public static final int MSG_CALLBACK_LNSTATUSNOTIFY             = 203;
    public static final int MSG_CALLBACK_LNSUBDEVICEJOINNOTIFY      = 204;
    public static final int MSG_CALLBACK_LNOTAUPGRADENOTIFY         = 205;

    // 定义配网进度消息常量
    public static final int MSG_CONFIGNETWORK_STEP_START            = 300;
    public static final int MSG_CONFIGNETWORK_STEP1_SSIDACK         = 301;
    public static final int MSG_CONFIGNETWORK_STEP2_PWDACK          = 302;
    public static final int MSG_CONFIGNETWORK_STEP3_DN              = 303;
    public static final int MSG_CONFIGNETWORK_STEP4_TOKEN           = 304;
    public static final int MSG_CONFIGNETWORK_STEP5_BIND            = 305;
    public static final int MSG_CONFIGNETWORK_STEP_END              = 306;
    public static final int MSG_CONFIGNETWORK_REMAIN_SECOND         = 307;
    public static final int MSG_CONFIGNETWORK_FAILURE               = 308;
    public static final int MSG_CONFIGNETWORK_TIMEOUT               = 310;

    // 定义允许入网进度消息常量
    public static final int MSG_PERMITJOIN_STEP_START               = 400;
    public static final int MSG_PERMITJOIN_STEP_END                 = 406;
    public static final int MSG_PERMITJOIN_REMAIN_SECOND            = 407;
    public static final int MSG_PERMITJOIN_TIMEOUT                  = 410;

    // 定义配网常量
    public static final String CONFIGNETWORK_AES_CBC_IV             = "3333333333333333";
    public static final String CONFIGNETWORK_AES_CBC_YZ             = "8888888888888888";
    public static final int CONFIGNETWORK_FRAME_MINSIZE             = 7;
    public static final int CONFIGNETWORK_FRAME_HEADER              = 0x5505;
    public static final int CONFIGNETWORK_FRAME_NONACK              = 0xFF;
    public static final int CONFIGNETWORK_FRAME_FOOTER              = 0x33;
    public static final int CONFIGNETWORK_BUFFER_MAXSIZE            = 256;
    public static final int CONFIGNETWORK_ACK_SUCCESS               = 0;
    public static final int CONFIGNETWORK_CMD_NULL                  = 0;
    public static final int CONFIGNETWORK_CMD_SENDSSID              = 1;
    public static final int CONFIGNETWORK_CMD_SENDPASSWORD          = 2;
    public static final int CONFIGNETWORK_CMD_SENDYZ                = 3;
    public static final int CONFIGNETWORK_CMD_RECEIVEDN             = 4;
    public static final int CONFIGNETWORK_CMD_RECEIVETOKEN          = 5;
    public static final int CONFIGNETWORK_CMD_RECEIVESTATUS         = 6;
    public static final int CONFIGNETWORK_CMD_READTOKEN             = 7;

    // 定义配网发送数据状态机
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_0      = 0;
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_1      = 1;
    public static final int CONFIGNETWORK_SEND_STATUSMACHINE_2      = 2;
}
