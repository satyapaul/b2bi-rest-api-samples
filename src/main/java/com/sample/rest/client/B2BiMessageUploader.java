package com.sample.rest.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContexts;

public class B2BiMessageUploader {

    private static String apiUri = "/B2BAPIs/svc/messagebatches/";
    private static String serverUrl = "http://<replace-with-valid-ip-address>:45164";
	private static boolean isSSL = false;

    private static String sourceFileLoc = "C:\\Users\\satyajit.paul\\Downloads\\";
    private static String fileName = "image001.PNG"; 

    private static String mailboxPath = "/test123";
    
    public static void main(String[] args) {
        testRegularB2BAPIsUpload();
    }

    /**
    	TEST CODE
    */
    public static void testRegularB2BAPIsUpload() {

        Map < String, String > headerValues = new HashMap();

        // pain text --> Base64 Encoded String
        //testuser:password --> dGVzdHVzZXI6cGFzc3dvcmQ=
        //admin:password --> YWRtaW46cGFzc3dvcmQ=

        headerValues.put("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");

        String sourceFilePath = sourceFileLoc + fileName;


        try {
            uploadUsingMessageBatchesAPI(sourceFilePath, serverUrl, apiUri, mailboxPath, headerValues, isSSL);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }


	/**
	* Method does a single file upload, supports both SSL & Non-SSL
	*/
    public static void uploadUsingMessageBatchesAPI(String sourceFilePath, String serverUrl, String apiUri, String mailboxPath, Map < String, String > headerValues, boolean isSSL) throws IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
        long t0 = System.currentTimeMillis();


        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        File file = new File(sourceFilePath);
        InputStream inputStream = new FileInputStream(file);

        builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"), fileName);
        builder.addPart("mailboxName", new StringBody(mailboxPath, ContentType.MULTIPART_FORM_DATA));
        HttpEntity entity = builder.build();


        System.out.println(entity.isChunked());


        HttpPost request = new HttpPost(serverUrl + apiUri);

        request.setEntity(entity);

        for (String key: headerValues.keySet()) {
            String value = headerValues.get(key);
            request.addHeader(key, value);
        }

        CloseableHttpClient httpclient = isSSL ? getSecureHttpClient() : HttpClients.createDefault();
        try {
            System.out.println(request.getEntity().isStreaming());
            CloseableHttpResponse response = httpclient.execute(request);
            try {
                System.out.println(response.getCode());
                HttpEntity resEntity = response.getEntity();
                String content = EntityUtils.toString(resEntity);
                System.out.println(content);
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                }
                EntityUtils.consume(resEntity);

            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }

        long t1 = System.currentTimeMillis();
        System.out.println(" time taken (in ms) : " + (t1 - t0));
    }


	// Ignore this implementation, not tested yet
    public static void uploadMultipleFilesUsingMessageBatchesAPI() throws IOException, ParseException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        String path = "/B2BAPIs/svc/messagebatches/";

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        File file = new File(sourceFileLoc + fileName);

        InputStream inputStream = new FileInputStream(file);
        builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"), fileName);

		// Provide the mailbox name. Here it is /prod
        builder.addPart("mailboxName", new StringBody(mailboxPath , ContentType.MULTIPART_FORM_DATA));
        HttpEntity entity = builder.build();

        System.out.println(entity.isChunked());

        HttpPost request = new HttpPost(serverUrl + apiUri);

        request.setEntity(entity);

        request.addHeader("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            System.out.println(request.getEntity().isStreaming());
            CloseableHttpResponse response = httpclient.execute(request);
            try {
                System.out.println(response.getCode());
                HttpEntity resEntity = response.getEntity();
                String content = EntityUtils.toString(resEntity);
                System.out.println(content);
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                }
                EntityUtils.consume(resEntity);

            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpclient.close();
        }
    }

    // This method is used when the url is SSL
    private static CloseableHttpClient getSecureHttpClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();

        // Allow TLSv1.2 protocol only
        final SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
            .setHostnameVerifier(allowAllHosts)
            .setSslContext(SSLContexts.createSystemDefault())
            .setTlsVersions(TLS.V_1_2) 
            .build();
        final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(sslSocketFactory)
            .build();

        CloseableHttpClient httpclient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
        return httpclient;
    }

}
