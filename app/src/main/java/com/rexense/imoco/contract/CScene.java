package com.rexense.imoco.contract;

/**
 * Creator: xieshaobing
 * creat time:  11:19
 * Description: 场景常量
 */
public class CScene {
    // 操作
    public static final int OPERATE_CREATE                  = 1;
    public static final int OPERATE_UPDATE                  = 2;

    // 类型
    public static final String TYPE_MANUAL                  = "0";
    public static final String TYPE_AUTOMATIC               = "1";
    public static final String TYPE_IFTTT                   = "IFTTT";
    public static final String TYPE_CA                      = "CA";

    // 缺省图标URL
    public static final String DEFAULT_ICON_URL             ="http://iotx-paas-admin.oss-cn-shanghai.aliyuncs.com/publish/image/1585899444167.png";

    // 模板代码
    public static final int SMC_NONE                        = 0;
    public static final int SMC_NIGHT_RISE_ON               = 1;
    public static final int SMC_UNMANNED_OFF                = 2;
    public static final int SMC_ALARM_ON                    = 3;
    public static final int SMC_REMOTE_CONTROL_ON           = 4;
    public static final int SMC_OPEN_DOOR_ON                = 5;
    public static final int SMC_BELL_PLAY                   = 6;
    public static final int SMC_ALARM_PLAY                  = 7;
    public static final int SMC_PIR_DEPLOY_ALARM            = 8;
    public static final int SMC_DOOR_DEPLOY_ALARM           = 9;
    public static final int SMC_AUTOMATIC_MAX               = 9;
    public static final int SMC_GO_HOME_PATTERN             = 10;
    public static final int SMC_LEAVE_HOME_PATTERN          = 11;
    public static final int SMC_SLEEP_PATTERN               = 12;
    public static final int SMC_GETUP_PATTERN               = 13;

    // 参数类型
    public static final int SPT_TRIGGER_TITLE               = 0;
    public static final int SPT_TRIGGER                     = 1;
    public static final int SPT_CONDITION_TITLE             = 2;
    public static final int SPT_CONDITION_TIME              = 3;
    public static final int SPT_CONDITION_STATE             = 4;
    public static final int SPT_RESPONSE_TITLE              = 5;
    public static final int SPT_RESPONSE                    = 6;

    // 星期编码
    public static final int WEEK_CODE_MIN                   = 1;
    public static final int WEEK_CODE_MON                   = 1;
    public static final int WEEK_CODE_TUE                   = 2;
    public static final int WEEK_CODE_WED                   = 3;
    public static final int WEEK_CODE_THU                   = 4;
    public static final int WEEK_CODE_FRI                   = 5;
    public static final int WEEK_CODE_SAT                   = 6;
    public static final int WEEK_CODE_SUN                   = 7;
    public static final int WEEK_CODE_MAX                   = 7;

    // 规则限制
    public static final int RULE_MAX_NUMBER                 = 200;
    public static final int RULE_ONE_SCENE_MAX_TRIGGER      = 10;
    public static final int RULE_ONE_SCENE_MAX_CONDITION    = 5;
    public static final int RULE_ONE_SCENE_MAX_ACTION       = 30;

    // URI
    public static final String URI_TRIGGER_PROPERTY         = "trigger/device/property";
    public static final String URI_CONDITION_PROPERTY       = "condition/device/property";
    public static final String URI_CONDITION_TIME_RANGE     = "condition/timeRange";
    public static final String URI_ACTION_SET_PROPERTY      = "action/device/setProperty";
    public static final String URI_ACTION_INVOKE_SERVICE    = "action/device/invokeService";

}
