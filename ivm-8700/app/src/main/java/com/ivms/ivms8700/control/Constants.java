package com.ivms.ivms8700.control;

public final class Constants {

    private Constants() {
    }
    /**
     * 业务系统token for android
     */
    public static String APP_TOKEN = "4CE19CA8FCD150A4";
    /**
     * 日志tag名
     */
    public static String LOG_TAG = "ivmsdemo";
    /**
     * SharedPreferences数据表名称
     */
    public static String APP_DATA = "app_data";
    /**
     * SharedPreferences数据表用户名
     */
    public static String USER_NAME = "user_name";
    /**
     * SharedPreferences数据表用户密码
     */
    public static String PASSWORD = "password";
    /**
     * SharedPreferences数据表登录IP地址
     */
    public static String ADDRESS_NET = "address_net";

    /**
     * Intent相关常量
     */
    public interface IntentKey {
        /**
         * 获取根节点数据
         */
        String GET_ROOT_NODE = "getRootNode";
        /**
         * 获取子节点列表
         */
        String GET_SUB_NODE = "getChildNode";
        /**
         * 父节点类型
         */
        String PARENT_NODE_TYPE = "parentNodeType";
        /**
         * 父节点ID
         */
        String PARENT_ID = "parentId";
        /**
         * 监控点资源
         */
        String CAMERA = "Camera";
    }
    /**
     * 登录逻辑相关常量
     */
    public interface Login {

        /**
         * 登录成功
         */
        int LOGIN_SUCCESS = 1;
        /**
         * 登录失败
         */
         int LOGIN_FAILED = 2;

    }




    /**
     * EventBus相关常量
     */
    public interface Event {
        //获取消息
        int getMsg=1;

    }
}
