package com.ivms.ivms8700.mysdk;

import android.content.Context;
import android.text.TextUtils;

import com.hikvision.sdk.utils.SDKUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;

public class MyAsyncHttpExecute {
    private static final int TIME_OUT = 5000;
    private static final int RETRIES = 1;
    private static volatile MyAsyncHttpExecute ins;
    private AsyncHttpClient client = new AsyncHttpClient();
    private CookieStore cookieStore = new PersistentCookieStore(MyVMSNetSDK.getApplication().getApplicationContext());
    private SSLSocketFactory mSSLSocketFactory;

    private MyAsyncHttpExecute() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLSocketFactory sf = new MySSLSocketFactory(keyStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            this.client.setSSLSocketFactory(sf);
        } catch (Exception var3) {
            ;
        }

        this.client.setCookieStore(this.cookieStore);
        this.client.setTimeout(5000);
        this.client.setMaxConnections(1);
    }

    public static MyAsyncHttpExecute getIns() {
        if (ins == null) {
            Class var0 = MyAsyncHttpExecute.class;
            synchronized(MyAsyncHttpExecute.class) {
                if (ins == null) {
                    ins = new MyAsyncHttpExecute();
                }
            }
        }

        return ins;
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        if (sf != null) {
            this.mSSLSocketFactory = sf;
            this.client.setSSLSocketFactory(sf);
        }
    }

    public Object parser(String str, Class<?> cls) {
        try {
            if (!SDKUtil.isEmpty(new String[]{str}) && cls != null) {
                Serializer serializer = new Persister();
                InputStreamReader is = new InputStreamReader(new ByteArrayInputStream(str.getBytes("UTF-8")), "utf-8");
                Object body = serializer.read(cls, is);
                return body;
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return null;
    }

    public void execute(String url, AsyncHttpResponseHandler handler) {
        if (handler != null) {
            this.client.post(url, handler);
        }
    }

    public void execute(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        if (params != null && handler != null) {
            this.client.post(url, params, handler);
        }
    }

    public void executeHttpPost(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        if (params != null && handler != null && !TextUtils.isEmpty(url)) {
            this.client.post(url, params, handler);
        }
    }

    public void execute(String url, RequestParams params, AsyncHttpResponseHandler handler, SyncHttpClient client) {
        if (params != null && handler != null && client != null) {
            try {
                client.setCookieStore(this.cookieStore);
                client.setSSLSocketFactory(this.mSSLSocketFactory);
                client.setTimeout(5000);
                client.setMaxConnections(1);
                client.post(url, params, handler);
            } catch (Exception var6) {
                var6.printStackTrace();
            }

        }
    }

    public void execute(String url, AsyncHttpResponseHandler handler, SyncHttpClient client) {
        if (handler != null && client != null) {
            try {
                client.setCookieStore(this.cookieStore);
                client.setSSLSocketFactory(this.mSSLSocketFactory);
                client.setTimeout(5000);
                client.setMaxConnections(1);
                client.post(url, handler);
            } catch (Exception var5) {
                var5.printStackTrace();
            }

        }
    }

    public void execute(Context context, String url, int command, String body, AsyncHttpResponseHandler handler, SyncHttpClient client) {
        String xml = this.packageMagXml(command, body);

        try {
            client.setCookieStore(this.cookieStore);
            client.setSSLSocketFactory(this.mSSLSocketFactory);
            client.setTimeout(5000);
            client.setMaxConnections(1);
            client.post(context, url, this.createHeaders(), this.createEntity(xml), (String)null, handler);
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public void execute(Context context, String url, int command, String body, AsyncHttpResponseHandler handler) {
        String xml = this.packageMagXml(command, body);
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><MagMessage><Version>0.1</Version><Sequence>1</Sequence><CommandType>0</CommandType><Command>1</Command><Params><PTZControl><SessionId>9B49EDEEAC96E0D46F91F01CC24F0C51</SessionId><SzCamIndexCode>9e70b1aeaa1f4600bf9c4cfcceb04c77</SzCamIndexCode><IPtzCommand>22</IPtzCommand><IAction>0</IAction><IIndex>5</IIndex><ISpeed>5</ISpeed><IPriority>100</IPriority><IUserId></IUserId><IMatrixCameraId></IMatrixCameraId><IMonitorId></IMonitorId><ILockTime></ILockTime><IPtzCruisePoint></IPtzCruisePoint><IPtzCruiseInput></IPtzCruiseInput><Param1>5</Param1><Param2>2</Param2><Param3>2</Param3><Param4>2</Param4></PTZControl></Params></MagMessage>";
        HttpEntity entity =this.createEntity(xml);
        this.client.post(context, url, entity, "application/x-www-urlencoded", handler);
    }

    protected Header[] createHeaders() {
        ArrayList headers = new ArrayList();

        try {
            headers.add(new BasicHeader("Connection", "Keep-Alive"));
            headers.add(new BasicHeader("Content-Type", "application/x-www-urlencoded"));
            headers.add(new BasicHeader("; charset=", "UTF-8"));
            return (Header[])headers.toArray(new Header[headers.size()]);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    protected String packageMagXml(int command, String body) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<?xml version='1.0' encoding='UTF-8' ?>");
        stringBuffer.append("<MagMessage>");
        stringBuffer.append("<Version>").append(0.1D).append("</Version>");
        stringBuffer.append("<Sequence>").append(1).append("</Sequence>");
        stringBuffer.append("<CommandType>").append(0).append("</CommandType>");
        stringBuffer.append("<Command>").append(command).append("</Command>");
        stringBuffer.append("<Params>").append(body).append("</Params>");
        stringBuffer.append("</MagMessage>");
        return stringBuffer.toString();
    }

    protected HttpEntity createEntity(String requestxml) {
        if (SDKUtil.isEmpty(new String[]{requestxml})) {
            return null;
        } else {
            try {
                byte[] reqBytes = requestxml.getBytes("utf-8");
                return new ByteArrayEntity(reqBytes);
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }
}
