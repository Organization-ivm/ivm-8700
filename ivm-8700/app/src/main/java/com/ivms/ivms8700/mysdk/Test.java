package com.ivms.ivms8700.mysdk;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    public static String postSOAP(String url, String soapContent) {

        HttpClient httpclient = null;
        HttpPost httpPost = null;
        BufferedReader reader = null;
        int i = 0;

        while (i < 4) {
            try {
                httpclient = HttpClientBuilder.create().build();
                httpPost = new HttpPost(url);
                StringEntity myEntity = new StringEntity(soapContent, "UTF-8");
                httpPost.addHeader("Content-Type", "text/xml; charset=UTF-8");
                httpPost.setEntity(myEntity);
                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    reader = new BufferedReader(new InputStreamReader(resEntity
                            .getContent(), "UTF-8"));
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        sb.append("\r\n");
                    }
                    return sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                i++;
                if (i == 4) {
                    System.out.println("=-=="+"not connect:" + url + "\n" + e.getMessage());
                }
            } finally {
                if (httpPost != null) {
                    httpPost.abort();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (httpclient != null) {
                    httpclient.getConnectionManager().shutdown();
                }
            }
        }
        return "none";
    }

    public static void main(String[] args) {
        String url = "http://222.66.82.2:6713/mag/ptz";
        String soap = "<?xml version='1.0' encoding='UTF-8' ?><MagMessage><Version>0.1</Version><Sequence>1</Sequence><CommandType>0</CommandType><Command>1</Command><Params><PTZControl><SessionId>99FC8DC50E63DE8F970E834801DD84E3</SessionId><SzCamIndexCode>1c649fd262214b6ba9753fa24eb49387</SzCamIndexCode><IPtzCommand>21</IPtzCommand><IAction>0</IAction><IIndex>0</IIndex><ISpeed>4</ISpeed><IPriority>17</IPriority><IUserId>17</IUserId><IMatrixCameraId>17</IMatrixCameraId><IMonitorId>17</IMonitorId><ILockTime>17</ILockTime><IPtzCruisePoint>17</IPtzCruisePoint><IPtzCruiseInput>17</IPtzCruiseInput><Param1>17</Param1><Param2>17</Param2><Param3>17</Param3><Param4>17</Param4></PTZControl></Params></MagMessage>";
        System.out.println(postSOAP(url, soap));

    }

}
