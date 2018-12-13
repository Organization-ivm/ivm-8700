package com.ivms.ivms8700.mysdk;

import android.annotation.SuppressLint;

import com.hik.mcrsdk.rtsp.PlaybackInfo;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.data.TempData;
import com.hikvision.sdk.net.asynchttp.AsyncHttpExecute;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.bean.MAGServer;
import com.hikvision.sdk.net.bean.MagMessageNcgItem;
import com.hikvision.sdk.net.bean.MagMessageNcgParams;
import com.hikvision.sdk.net.bean.Message;
import com.hikvision.sdk.net.bean.RecordBody;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.business.BaseBusiness;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.CNetSDKLog;
import com.hikvision.sdk.utils.SDKUtil;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressLint({"DefaultLocale"})
public class MyLiveOrPalyBackBusiness extends BaseBusiness {
    private static String TAG =MyLiveOrPalyBackBusiness.class.getSimpleName();
    private static volatile MyLiveOrPalyBackBusiness mIns;

    private MyLiveOrPalyBackBusiness() {
    }

    public static MyLiveOrPalyBackBusiness getInstance() {
        if (mIns == null) {
            Class var0 = MyLiveOrPalyBackBusiness.class;
            synchronized(MyLiveOrPalyBackBusiness.class) {
                if (mIns == null) {
                    mIns = new MyLiveOrPalyBackBusiness();
                }
            }
        }

        return mIns;
    }

    public boolean queryRecoredInfo(CameraInfo cameraInfo, Calendar startTime, Calendar endTime, int storageType, String guid, OnVMSNetSDKBusiness mOnVMSNetSDKBusiness) {
        LoginData loginData = TempData.getIns().getLoginData();
        if (loginData == null) {
            this.mLastError = 129;
            this.mLastErrorDescribe = "VMSNetSDK::login data is null, plese relogin";
            return false;
        } else if (cameraInfo == null) {
            this.mLastError = 1100;
            this.mLastErrorDescribe = "VMSNetSDK::cameraInfo is null, plese reselect";
            return false;
        } else {
            MAGServer mag = loginData.getMAGServer();
            if (mag == null) {
                this.mLastError = 130;
                this.mLastErrorDescribe = "VMSNetSDK::mag is null, plese check web config mag and relogin";
                return false;
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                int i;
                if (cameraInfo.getCascadeFlag() == 0) {
                    stringBuffer.append("<QueryCondition>");
                    stringBuffer.append("<cameraindexcode>").append(cameraInfo.getSysCode()).append("</cameraindexcode>");
                    stringBuffer.append("<Guid>").append(guid).append("</Guid>");
                    stringBuffer.append("<StorageType>").append(storageType - 1).append("</StorageType>");
                    if (storageType == 4) {
                        stringBuffer.append("<BeginTime>").append(startTime.getTimeInMillis() / 1000L).append("</BeginTime>");
                        stringBuffer.append("<EndTime>").append(endTime.getTimeInMillis() / 1000L).append("</EndTime>");
                    } else {
                        stringBuffer.append("<BeginTime>").append(SDKUtil.converTimeCalendar(startTime)).append("</BeginTime>");
                        stringBuffer.append("<EndTime>").append(SDKUtil.converTimeCalendar(endTime)).append("</EndTime>");
                    }

                    int[] types = new int[]{1, 2, 4, 16};
                    if (types != null && types.length > 0) {
                        stringBuffer.append("<RecTypes>");

                        for(i = 0; i < types.length; ++i) {
                            stringBuffer.append("<RecType>").append(types[i]).append("</RecType>");
                        }

                        stringBuffer.append("</RecTypes>");
                    }

                    stringBuffer.append("</QueryCondition>");
                } else {
                    stringBuffer.append("<QueryCondition>");
                    stringBuffer.append("<CameraIndexcode>").append(cameraInfo.getGbSysCode()).append("</CameraIndexcode>");
                    stringBuffer.append("<BeginTime>").append(SDKUtil.converTimeTZ(startTime)).append("</BeginTime>");
                    stringBuffer.append("<EndTime>").append(SDKUtil.converTimeTZ(endTime)).append("</EndTime>");
                    stringBuffer.append("<RecordPos>0</RecordPos>");
                    stringBuffer.append("<RecordType>23</RecordType>");
                    stringBuffer.append("<FromIndex>1</FromIndex>");
                    stringBuffer.append("<ToIndex>10</ToIndex>");
                    stringBuffer.append("</QueryCondition>");
                }

                String url = "";
                if (cameraInfo.getCascadeFlag() == 0) {
                    url = String.format("http://%s:%d/mag/queryVrmRecording", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                } else {
                    url = String.format("http://%s:%d/mag/queryNcgRecording", mag.getMAGAddr(), mag.getMAGHttpSerPort());
                }

                i = 1 == cameraInfo.getCascadeFlag() ? 10 : 13;
                CNetSDKLog.info("url---->" + url + "?" + stringBuffer.toString());

                try {
                    MyAsyncHttpExecute.getIns().execute(VMSNetSDK.getApplication().getApplicationContext(), url, i, stringBuffer.toString(), this.getQueryRecoredNetCallBack(cameraInfo, mOnVMSNetSDKBusiness));
                    return true;
                } catch (Exception var13) {
                    this.mLastError = 127;
                    this.mLastErrorDescribe = "http execution occur exception,msg:[" + var13.getMessage() + "]";
                    CNetSDKLog.error("VMSNetSDK::getCameraInfo() ==>>http execution occur exception,msg:[" + var13.getMessage() + "]");
                    return false;
                }
            }
        }
    }

    private TextHttpResponseHandler getQueryRecoredNetCallBack(final CameraInfo cameraInfo, final OnVMSNetSDKBusiness mOnVMSNetSDKBusiness) {
        TextHttpResponseHandler netCallBack = new TextHttpResponseHandler() {
            public void onSuccess(int arg0, Header[] arg1, String arg2) {
                CNetSDKLog.info("net connection success status:" + arg0 + " msg:" + arg2);
                MyLiveOrPalyBackBusiness.this.isNetSuccess = true;
                int start = arg2.indexOf("<Params>");
                int end = arg2.indexOf("</Params>");
                if (start > 0 && start < end) {
                    String result = arg2.substring(start, end);
                    if (cameraInfo.getCascadeFlag() == 0) {
                        MyLiveOrPalyBackBusiness.this.processNotCascadeRecordSegment(result, mOnVMSNetSDKBusiness);
                    } else {
                        MyLiveOrPalyBackBusiness.this.processCascadeRecordSegment(result, mOnVMSNetSDKBusiness);
                    }

                } else {
                    if (mOnVMSNetSDKBusiness != null) {
                        mOnVMSNetSDKBusiness.onFailure();
                    }

                }
            }

            public void onFailure(int arg0, Header[] arg1, String arg2, Throwable arg3) {
                MyLiveOrPalyBackBusiness.this.isNetSuccess = false;
                CNetSDKLog.error("net connection failure status:" + arg0 + " msg:" + arg2);
                if (mOnVMSNetSDKBusiness != null) {
                    mOnVMSNetSDKBusiness.onFailure();
                }

                MyLiveOrPalyBackBusiness.this.mLastError = MyLiveOrPalyBackBusiness.this.mLastError;
                MyLiveOrPalyBackBusiness.this.mLastErrorDescribe = MyLiveOrPalyBackBusiness.this.mLastErrorDescribe;
            }
        };
        return netCallBack;
    }

    private void processNotCascadeRecordSegment(String result, OnVMSNetSDKBusiness mOnVMSNetSDKBusiness) {
        RecordBody body = (RecordBody) AsyncHttpExecute.getIns().parser(result, RecordBody.class);
        if (body == null) {
            if (mOnVMSNetSDKBusiness != null) {
                mOnVMSNetSDKBusiness.onFailure();
            }

        } else if (body.getBComplete() == 0) {
            if (mOnVMSNetSDKBusiness != null) {
                mOnVMSNetSDKBusiness.onFailure();
            }

        } else {
            if (body.getQueryResult() != null && mOnVMSNetSDKBusiness != null) {
                mOnVMSNetSDKBusiness.onSuccess(body.getQueryResult());
            }

        }
    }

    private void processCascadeRecordSegment(String result, OnVMSNetSDKBusiness mOnVMSNetSDKBusiness) {
        MagMessageNcgParams ncgParams = (MagMessageNcgParams) AsyncHttpExecute.getIns().parser(result, MagMessageNcgParams.class);
        if (ncgParams == null) {
            if (mOnVMSNetSDKBusiness != null) {
                mOnVMSNetSDKBusiness.onFailure();
            }

        } else if (ncgParams.getBComplete() == 0) {
            if (mOnVMSNetSDKBusiness != null) {
                mOnVMSNetSDKBusiness.onFailure();
            }

        } else {
            List<Message> msgList = ncgParams.getMessages();
            if (msgList != null && msgList.size() != 0) {
                RecordInfo recordInfo = new RecordInfo();
                LinkedList<RecordSegment> recordSegmentList = new LinkedList();
                Iterator var8 = msgList.iterator();

                while(var8.hasNext()) {
                    Message message = (Message)var8.next();
                    MagMessageNcgItem msgItem = message.getPack().getFile().getFileInfolist().getItem();
                    RecordSegment mRecordSegment = new RecordSegment();
                    mRecordSegment.setBeginTime(msgItem.getBeginTime());
                    mRecordSegment.setEndTime(msgItem.getEndTime());
                    mRecordSegment.setRecordType(msgItem.getRecordType());
                    recordSegmentList.add(mRecordSegment);
                }

                recordInfo.setSegmentList(recordSegmentList);
                if (mOnVMSNetSDKBusiness != null) {
                    mOnVMSNetSDKBusiness.onSuccess(recordInfo);
                }

            } else {
                if (mOnVMSNetSDKBusiness != null) {
                    mOnVMSNetSDKBusiness.onFailure();
                }

            }
        }
    }

    public String getPlayUrl(CameraInfo cameraInfo, int streamType) {
        if (cameraInfo == null) {
            this.mLastError = 1100;
            this.mLastErrorDescribe = "VMSNetSDK::cameraInfo is null, plese reselect";
            return "";
        } else {
            LoginData loginData = TempData.getIns().getLoginData();
            if (loginData == null) {
                this.mLastError = 129;
                this.mLastErrorDescribe = "VMSNetSDK::login data is null, plese relogin";
                return "";
            } else {
                MAGServer mag = loginData.getMAGServer();
                if (mag == null) {
                    this.mLastError = 130;
                    this.mLastErrorDescribe = "VMSNetSDK::mag is null, plese check web config mag and relogin";
                    return "";
                } else {
                    String magIp = mag.getMAGAddr();
                    String magStreamPort = String.valueOf(mag.getMAGStreamSerPort());
                    String cameraId = cameraInfo.getSysCode();
                    String cnId = String.valueOf(loginData.getAppNetID());
                    String pnId = String.valueOf(cameraInfo.getDeviceNetID());
                    String auth = String.valueOf(loginData.getUserAuthority());
                    String gbSysCode = cameraInfo.getGbSysCode();
                    StringBuilder sb = new StringBuilder();
                    String streamTypeName = "MAIN";
                    switch(streamType) {
                        case 1:
                            streamTypeName = "MAIN";
                            break;
                        case 2:
                            streamTypeName = "SUB";
                            break;
                        case 3:
                            streamTypeName = "SUB";
                    }

                    if (cameraInfo.getCascadeFlag() == 0) {
                        sb.append("rtsp://").append(magIp).append(":").append(magStreamPort).append("/realplay://").append(cameraId).append(":").append(streamTypeName).append(":TCP?").append("cnid=").append(cnId).append("&").append("pnid=").append(pnId).append("&").append("token=&").append("auth=").append(auth).append("&").append("redirect=0&");
                    } else {
                        sb.append("rtsp://").append(magIp).append(":").append(magStreamPort).append("/ncg://").append(gbSysCode).append(":").append(streamTypeName).append("?").append("token=&");
                    }

                    if (streamType != 2) {
                        sb.append("transcode=0&resolution=2&bitrate=100&framerate=10&videotype=2&systemformat=2");
                    } else {
                        sb.append("transcode=1&resolution=2&bitrate=100&framerate=10&videotype=2&systemformat=2");
                    }

                    return sb.toString();
                }
            }
        }
    }

    public String getPlayBackRtspUrl(CameraInfo cameraInfo, String url, Calendar start, Calendar stop) {
        LoginData loginData = TempData.getIns().getLoginData();
        if (loginData == null) {
            this.mLastError = 129;
            this.mLastErrorDescribe = "VMSNetSDK::login data is null, plese relogin";
            return "";
        } else {
            MAGServer mag = loginData.getMAGServer();
            if (mag == null) {
                this.mLastError = 130;
                this.mLastErrorDescribe = "VMSNetSDK::mag is null, plese check web config mag and relogin";
                return "";
            } else {
                String rtspUri = "";
                if (cameraInfo.getCascadeFlag() == 0) {
                    if (url == null || url.length() <= 0) {
                        this.mLastError = 10001;
                        this.mLastErrorDescribe = "There is not has playback";
                        return "";
                    }

                    PlaybackInfo palybackInfo = new PlaybackInfo();
                    palybackInfo.setMagIp(mag.getMAGAddr());
                    palybackInfo.setMagPort(mag.getMAGStreamSerPort());
                    palybackInfo.setPlaybackUrl(url);
                    rtspUri = RtspClient.getInstance().generatePlaybackUrl(palybackInfo);
                } else {
                    rtspUri = String.format("rtsp://%s:%s/ncg_playback://%s:%s:%s:1?token=&recordPos=0&recordType=23", mag.getMAGAddr(), String.valueOf(mag.getMAGStreamSerPort()), cameraInfo.getGbSysCode(), SDKUtil.converTimeTZ(start), SDKUtil.converTimeTZ(stop));
                }

                return rtspUri;
            }
        }
    }
}
