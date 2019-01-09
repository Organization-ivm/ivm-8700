package com.ivms.ivms8700.bean;


import java.util.List;

public class MessageEntity {
    /**
     * data : {"list":[{"msg":"{'type':'faceRecognize','stationCode':'310000L14S13','recognizeTime':'2018-12-13 16:37:42'}"},{"msg":"{'type':'safeCapRecognize','stationCode':'310000L14S13',' recognizeTime':'2018-11-07 11:22:42'}"}]}
     * msg : 获取成功!
     * result : success
     */

    private DataBean data;
    private String msg;
    private String result;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public class DataBean {
        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public  class ListBean {
            /**
             * msg : {'type':'faceRecognize','stationCode':'310000L14S13','recognizeTime':'2018-12-13 16:37:42'}
             */

            private String msg;

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }
        }
    }

    public static class Msg {
        private String type;
        private String stationName;
        private String stationCode;
        private String recognizeTime;

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStationCode() {
            return stationCode;
        }

        public void setStationCode(String stationCode) {
            this.stationCode = stationCode;
        }

        public String getRecognizeTime() {
            return recognizeTime;
        }

        public void setRecognizeTime(String recognizeTime) {
            this.recognizeTime = recognizeTime;
        }
    }


//    {"data":{"list":[{"msg":"{'type':'faceRecognize','stationCode':'310000L14S13','recognizeTime':'2018-12-13 16:37:42'}"},{"msg":"{'type':'safeCapRecognize','stationCode':'310000L14S13',' recognizeTime':'2018-11-07 11:22:42'}"}]},"msg":"获取成功!","result":"success"}


}