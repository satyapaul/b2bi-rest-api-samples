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

/**
* License Terms:
* Permission is hereby granted, free of charge, to any person obtaining a copy 
* of this software and associated documentation files (the "Software"), to deal 
* in the Software without restriction, including without limitation the rights 
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
* copies of the Software, and to permit persons to whom the Software is furnished 
* to do so, subject to the following conditions:
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
* IN THE SOFTWARE.
*
*/

/**
 * The program can upload one or multiple files to B2Bi mailbox using REST API.
 * This is a REST API Client that does a multipart file upload using following API
 * https://<servername>:<port>/B2BAPIs/svc/messagebatches/
 * It uses the Basic Authorization supported by B2Bi REST APIs.
 * @author satyajit.paul
 * @version 1.1
 *
 */
public class B2BiMessageUploader {

    private static String apiUri = "/B2BAPIs/svc/messagebatches/";
    private static String serverUrl = "http://b2biqarhlsd3:33983";//"http://<replace-with-valid-ip-address>:45164";
	private static boolean isSSL = false;

    private static String sourceFileLoc = "C:\\Users\\satyajit.paul\\Downloads\\";
    private static String fileName = "image001_1_1.PNG"; 
    private static String fileName2 = "image002_2_2.PNG";     
    private static String fileName3 = "image003_3_3.PNG"; 
    private static String fileName4 = "system-ocp.log";
    
    private static String mailboxPath = "/test123";
    
    public static void main(String[] args) {

        Map < String, String > headerValues = new HashMap();

        // pain text --> Base64 Encoded String
        // testuser:password --> dGVzdHVzZXI6cGFzc3dvcmQ=
        // admin:password --> YWRtaW46cGFzc3dvcmQ=

        headerValues.put("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");
        String sourceFilePath = sourceFileLoc + fileName;


        try {
        	/**
        	 * Example 1: Upload one single file.
        	 */
            uploadUsingMessageBatchesAPI(sourceFilePath, serverUrl, apiUri, mailboxPath, headerValues, isSSL);
            
            /**
        	 * Example 2: Upload multiple files.
        	 */
            uploadMultipleFilesUsingMessageBatchesAPI(sourceFilePath, serverUrl, apiUri, mailboxPath, headerValues, isSSL);
            
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
    
    /**
     * The method can upload only single file to B2Bi/SFG Mailbox, supports both SSL & Non-SSL.
     * @param sourceFilePath
     * @param serverUrl
     * @param apiUri
     * @param mailboxPath
     * @param headerValues
     * @param isSSL
     * @throws IOException
     * @throws ParseException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static void uploadUsingMessageBatchesAPI(String sourceFilePath, String serverUrl, String apiUri, String mailboxPath, Map < String, String > headerValues, boolean isSSL) throws IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");
        long t0 = System.currentTimeMillis();


        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

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


    
    /**
     * Method can upload multiple files to B2Bi mailbox, supports both SSL & Non-SSL.
     * 
     * @param sourceFilePath
     * @param serverUrl
     * @param apiUri
     * @param mailboxPath
     * @param headerValues
     * @param isSSL
     * @throws IOException
     * @throws ParseException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    public static void uploadMultipleFilesUsingMessageBatchesAPI(String sourceFilePath, String serverUrl, String apiUri, String mailboxPath, Map < String, String > headerValues, boolean isSSL)  throws IOException, ParseException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "DEBUG");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        File file2 = new File(sourceFileLoc + fileName2);
        InputStream inputStream = new FileInputStream(file2);
        builder.addBinaryBody("upstream", inputStream, ContentType.create("application/zip"), fileName2);
		// Provide the mailbox name. Here it is /prod
        builder.addPart("mailboxName", new StringBody(mailboxPath , ContentType.MULTIPART_FORM_DATA));
        
        File file3 = new File(sourceFileLoc + fileName3);
        InputStream inputStream1 = new FileInputStream(file3);
        builder.addBinaryBody("upstream", inputStream1, ContentType.create("application/zip"), fileName3);
		// Provide the mailbox name. Here it is /prod
        builder.addPart("mailboxName", new StringBody(mailboxPath , ContentType.MULTIPART_FORM_DATA));
        
        File file4 = new File(sourceFileLoc + fileName4);
        InputStream inputStream2 = new FileInputStream(file4);
        builder.addBinaryBody("upstream", inputStream2, ContentType.create("application/txt"), fileName3);
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

    
    /**
     * This method is used when the url is SSL
     * @return
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
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
