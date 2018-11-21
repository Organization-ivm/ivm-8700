package com.ivms.ivms8700.control;

public final class Constants {
    private Constants() {
    }

    /**
     * 日志tag名
     */
    public static String LOG_TAG = "ivmsdemo";

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
        String CAMERA = "Camera";
    }

    /**
     * 登录逻辑相关常量
     */
    public interface Login {

        /**
         * 显示登陆进度条
         */
        int SHOW_LOGIN_PROGRESS = 0;
        /**
         * 关闭登陆进度条
         */
        int CANCEL_PROGRESS = 1;
        /**
         * 登陆成功
         */
        int LOGIN_SUCCESS = 2;
        /**
         * 登陆失败
         */
        int LOGIN_FAILED = 3;
    }

    /**
     * 退出逻辑相关常量
     */
    public interface Logout {

        int Base = 10;
        /**
         * 显示退出进度条
         */
        int SHOW_LOGOUT_PROGRESS = Base + 0;
        /**
         * 关闭退出进度条
         */
        int CANCEL_PROGRESS = Base + 1;
        /**
         * 退出成功
         */
        int LOGOUT_SUCCESS = Base + 2;
        /**
         * 退出失败
         */
        int LOGOUT_FAILED = Base + 3;
    }

    /**
     * 资源加载逻辑相关常量
     */
    public interface Resource {

        /**
         * 显示加载进度条
         */
        int SHOW_LOADING_PROGRESS = 0;
        /**
         * 关闭加载进度条
         */
        int CANCEL_LOADING_PROGRESS = 1;
        /**
         * 加载成功
         */
        int LOADING_SUCCESS = 2;

        /**
         * 加载成功—子节点
         */
        int LOADING_SUCCESS_TIER = 4;
        /**
         * 加载失败
         */
        int LOADING_FAILED = 3;
    }

    /**
     * 预览相关常量
     */
    public interface Live {

        /**
         * 获取监控点信息
         */
        int getCameraInfo = 1;
        /**
         * 获取监控点信息成功
         */
        int getCameraInfo_Success = 2;
        /**
         * 获取监控点信息失败
         */
        int getCameraInfo_failure = 3;
        /**
         * 获取设备信息
         */
        int getDeviceInfo = 4;
        /**
         * 获取设备信息成功
         */
        int getDeviceInfo_Success = 5;
        /**
         * 获取设备信息失败
         */
        int getDeviceInfo_failure = 6;
    }

    /**
     * 回放相关的常量
     */
    public interface PlayBack {

        /**
         * 显示登陆进度条
         */
        int SHOW_LOGIN_PROGRESS = 100;
        /**
         * 关闭登陆进度条
         */
        int CANCEL_PROGRESS = 101;

        /**
         * 获取监控点信息
         */
        int getCameraInfo = 10001;
        /**
         * 获取监控点信息成功
         */
        int getCameraInfo_Success = 10002;
        /**
         * 获取监控点信息失败
         */
        int getCameraInfo_failure = 10003;
        /**
         * 获取设备信息
         */
        int getDeviceInfo = 10004;
        /**
         * 获取设备信息成功
         */
        int getDeviceInfo_Success = 10005;
        /**
         * 获取设备信息失败
         */
        int getDeviceInfo_failure = 10006;

        /**
         * 查找录像片段
         */
        int queryRecordSegment = 10007;
        /**
         * 查找录像片段成功
         */
        int queryRecordSegment_Success = 10008;
        /**
         * 查找录像片段失败
         */
        int queryRecordSegment_failure = 10009;
    }
}
