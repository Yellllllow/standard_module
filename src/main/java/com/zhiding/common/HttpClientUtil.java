package com.zhiding.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
 


import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
 


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
 
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 
public class HttpClientUtil {
    private static final  Logger logger  =  LoggerFactory.getLogger(HttpClientUtil.class);
    /**
     * 以Post方法访问
     * @param url 请求地址
     * @param argsMap 携带的参数
     * @param content 内容
     * @return  String 返回结果
     * @throws Exception
     */
    public static String POSTMethod(String url,Map<String, Object> argsMap,String content) throws Exception{
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        if (MapUtils.isNotEmpty(argsMap)) {
            //设置参数
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
            httpPost.setEntity(encodedFormEntity);
        }
        if (StringUtils.isNotEmpty(content)) {
            httpPost.setEntity(new ByteArrayEntity(content.getBytes()));
        }
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPost.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    /**
     * 可设置Cookie的Post方法
     * @param url POST方法请求url
     * @param argsMap 携带参数
     * @param content 内容
     * @param cookies cookies
     * @return
     * @throws Exception
     */
    public static String POSTMethodWithFiles(String url, Map<String, Object> argsMap,List<String> filePaths) throws Exception {
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
         
        MultipartEntity multipartEntity = new MultipartEntity();
        //上传多张图片
        if (CollectionUtils.isNotEmpty(filePaths)) {
            for (String filePath: filePaths) {
                File file = new File(filePath);
                ContentBody fileCont = new FileBody(file, file.getName(), "image/jpeg", "utf-8");
                FormBodyPart formBodyPart = new FormBodyPart("media", fileCont);
                multipartEntity.addPart(formBodyPart);
            }
        }
         
        //构建Form表单参数
        if (MapUtils.isNotEmpty(argsMap)) {
            Set<Entry<String, Object>> entrySet = argsMap.entrySet();
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
            while(iterator.hasNext()){
                Entry<String, Object> entry = iterator.next();
                String name = entry.getKey();
                Object value = entry.getValue();
//              StringBody strBody = new StringBody(value.toString(), "utf-8");
                StringBody strBody = new StringBody(value.toString(),Charset.forName("utf-8"));
                multipartEntity.addPart(name,strBody);
            }
        }
        httpPost.setEntity(multipartEntity);
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPost.abort();
        }
        // 将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
     
    /**
     * 可设置Cookie的Post方法
     * @param url POST方法请求url
     * @param argsMap 携带参数
     * @param content 内容
     * @param cookies cookies
     * @return
     * @throws Exception
     */
    public static String POSTMethodWithFilesContentType(String url, Map<String, Object> argsMap,List<String[]> files) throws Exception {
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
         
        MultipartEntity multipartEntity = new MultipartEntity();
        //上传多张图片
        if (CollectionUtils.isNotEmpty(files)) {
            for (String[] fileInfos: files) {
                String paramName = fileInfos[0];
                String contentType = fileInfos[1];
                String filePath = fileInfos[2];
                File file = new File(filePath);
                ContentBody fileCont = new FileBody(file, file.getName(),contentType, "utf-8");
                FormBodyPart formBodyPart = new FormBodyPart(paramName, fileCont);
                multipartEntity.addPart(formBodyPart);
            }
        }
         
        //构建Form表单参数
        if (MapUtils.isNotEmpty(argsMap)) {
            Set<Entry<String, Object>> entrySet = argsMap.entrySet();
            Iterator<Entry<String, Object>> iterator = entrySet.iterator();
            while(iterator.hasNext()){
                Entry<String, Object> entry = iterator.next();
                String name = entry.getKey();
                Object value = entry.getValue();
                StringBody strBody = new StringBody(value.toString(),Charset.forName("utf-8"));
                multipartEntity.addPart(name,strBody);
            }
        }
        httpPost.setEntity(multipartEntity);
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPost.abort();
        }
        // 将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
 
    /**
     * 携带Header参数的POST方法
     * @param url
     * @param argsMap
     * @param headers
     * @param content
     * @return
     * @throws Exception
     */
    public static String POSTMethodWidthHeader(String url,Map<String, Object> argsMap,Map<String, String> headers,String content,boolean isSSL)throws Exception{
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        //是否加载Https安全证书
        if (isSSL) {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }
                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                        String authType) throws CertificateException {
                }
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
 
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, new TrustManager[] { xtm }, null);
                SSLSocketFactory socketFactory = new SSLSocketFactory(ctx,SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443,(SchemeSocketFactory) socketFactory));
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
         
        if (MapUtils.isNotEmpty(argsMap)) {
            //设置参数
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
            httpPost.setEntity(encodedFormEntity);
        }
        //设置Header参数
        if (MapUtils.isNotEmpty(headers)) {
            Set<Entry<String, String>> entrySet = headers.entrySet();
            Iterator<Entry<String, String>> iterator = entrySet.iterator();
            while(iterator.hasNext()){
                Entry<String, String> entry = iterator.next();
                String headerName = entry.getKey();
                String headerValue = entry.getValue();
                httpPost.setHeader(headerName, headerValue);
            }
        }
        if (StringUtils.isNotEmpty(content)) {
            httpPost.setEntity(new ByteArrayEntity(content.getBytes()));
        }
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPost);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPost.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    /**
     * 以Get方法访问
     * @param url 请求地址
     * @param argsMap 请求携带参数
     * @return String
     * @throws Exception
     */
    public static String GETMethod(String url,Map<String, Object> argsMap) throws Exception{
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        //为GET请求链接构造参数
        url = formatGetParameter(url,argsMap);
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpGet.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    /**
     * PUT方法
     * @param url 请求地址
     * @param argsMap 携带地址
     * @param cookies cookies
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String PUTMethod(String url,Map<String, Object> argsMap,String cookies,String content)throws Exception{
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        //设置内容
        if (StringUtils.isNotEmpty(content)) {
            httpPut.setEntity(new ByteArrayEntity(content.getBytes()));
        }
        //设置Cookies
        if(StringUtils.isNotEmpty(cookies)){
            httpPut.setHeader("Cookie", cookies);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-Type", "application/json");
        }
        //设置参数
        if (MapUtils.isNotEmpty(argsMap)) {
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
            httpPut.setEntity(encodedFormEntity);
        }
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPut);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPut.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    /**
     * PUT请求方法
     * @param url 请求地址
     * @param argsMap 携带参数
     * @param headerParam header参数
     * @param content 内容
     * @return
     * @throws Exception
     */
    public static String PUTMethod(String url,Map<String, Object> argsMap,Map<String,String> headerParam,String content)throws Exception{
        byte[] dataByte = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpPut httpPut = new HttpPut(url);
        //设置内容
        if (StringUtils.isNotEmpty(content)) {
            httpPut.setEntity(new ByteArrayEntity(content.getBytes()));
        }
        //设置Cookies
        if(MapUtils.isNotEmpty(headerParam)){
            Set<Entry<String, String>> entrySet = headerParam.entrySet();
            Iterator<Entry<String, String>> entryIter = entrySet.iterator();
            while(entryIter.hasNext()){
                Entry<String,String> entry = entryIter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                httpPut.setHeader(key, value);
            }
        }
        //设置参数
        if (MapUtils.isNotEmpty(argsMap)) {
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(setHttpParams(argsMap), "UTF-8");
            httpPut.setEntity(encodedFormEntity);
        }
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpPut);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpPut.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    public static String DELETEMethod(String url,Map<String, Object> argsMap,Map<String,String> headerParam)throws Exception{
        byte[] dataByte = null;
        url = formatGetParameter(url, argsMap);
        HttpClient httpClient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(url);
        //设置Cookies
        if(MapUtils.isNotEmpty(headerParam)){
            Set<Entry<String, String>> entrySet = headerParam.entrySet();
            Iterator<Entry<String, String>> entryIter = entrySet.iterator();
            while(entryIter.hasNext()){
                Entry<String,String> entry = entryIter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                httpDelete.setHeader(key, value);
            }
        }
        // 执行请求
        HttpResponse httpResponse = httpClient.execute(httpDelete);
        // 获取返回的数据
        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            byte[] responseBytes = getData(httpEntity);
            dataByte = responseBytes;
            httpDelete.abort();
        }
        //将字节数组转换成为字符串
        String result = bytesToString(dataByte);
        return result;
    }
     
    /**
     * 构造GET请求地址的参数拼接
     * @param url 地址
     * @param argsMap 参数
     * @return String
     */
    public static String formatGetParameter(String url,Map<String, Object> argsMap)throws Exception{
        if (url!=null && url.length()>0 && MapUtils.isNotEmpty(argsMap)) {
            if (!url.endsWith("?")) {
                url = url +"?";
            }
            if (argsMap!=null && !argsMap.isEmpty()) {
                Set<Entry<String, Object>> entrySet = argsMap.entrySet();
                Iterator<Entry<String, Object>> iterator = entrySet.iterator();
                while(iterator.hasNext()){
                    Entry<String, Object> entry = iterator.next();
                    if (entry!=null) {
                        String key = entry.getKey();
                        Object value = entry.getValue();
//                      Object value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
                        url = url + key + "=" + value;
                        if (iterator.hasNext()) {
                            url = url +"&";
                        }
                    }
                }
            }
        }
        return url;
    }
     
    /**
     * 获取Entity中数据
     * @param httpEntity
     * @return
     * @throws Exception
     */
    public static byte[] getData(HttpEntity httpEntity) throws Exception{
        BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(httpEntity);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bufferedHttpEntity.writeTo(byteArrayOutputStream);
        byte[] responseBytes = byteArrayOutputStream.toByteArray();
        return responseBytes;
    }
     
    /**
     * 设置HttpPost请求参数
     * @param argsMap
     * @return BasicHttpParams
     */
    private static List<BasicNameValuePair> setHttpParams(Map<String, Object> argsMap){
        List<BasicNameValuePair> nameValuePairList = new ArrayList<BasicNameValuePair>();
        //设置请求参数
        if (argsMap!=null && !argsMap.isEmpty()) {
            Set<Entry<String, Object>> set = argsMap.entrySet();
            Iterator<Entry<String, Object>> iterator = set.iterator();
            while(iterator.hasNext()){
                Entry<String, Object> entry = iterator.next();
                BasicNameValuePair basicNameValuePair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                nameValuePairList.add(basicNameValuePair);
            }
        }
        return nameValuePairList;
    }
     
    /**
     * 将字节数组转换成字符串
     * @param bytes
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String bytesToString(byte[] bytes) throws UnsupportedEncodingException{
        if (bytes!=null) {
            String returnStr = new String(bytes,"UTF-8");
            returnStr = StringUtils.trim(returnStr);
            return returnStr;
        }
        return null;
    }
}