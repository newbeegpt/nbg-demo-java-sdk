package com.newbeegpt.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbeegpt.demo.response.ApiResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @author newbeegpt
 */
public final class DefaultClient extends Client {
    private static final Log log = LogFactory.getLog(DefaultClient.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private final CloseableHttpClient httpClient;

    public DefaultClient(String appId, String appSecret, String baseUrl) {
        super(appId, appSecret, baseUrl);
        this.httpClient = HttpClients.createDefault();
    }

    public DefaultClient(String appId, String appSecret, String baseUrl,
                         CloseableHttpClient httpClient) {
        super(appId, appSecret, baseUrl);
        this.httpClient = httpClient;
    }

    private String buildQueryString(Map<String, ?> queryParams) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, ?> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Object[]) {
                for (int i = 0; i < ((Object[]) value).length; i++) {
                    stringBuilder.append(key).append("=").append(((Object[]) value)[i]).append("&");
                }
            } else {
                stringBuilder.append(key).append("=").append(value).append("&");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }


    @Override
    public <T> ApiResponse<T> postJson(String url, String json,
                                       Map<String, String> headers,
                                       TypeReference<ApiResponse<T>> responseType) {

        HttpPost httpPost = new HttpPost(url);
        // 设置请求体
        StringEntity requestEntity = new StringEntity(json,
                ContentType.APPLICATION_JSON);
        httpPost.setEntity(requestEntity);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        String result = makeRequest(httpPost);
        if (result == null) {
            log.warn(String.format("POST %s, headers: %s, body: %s. result is null", url,
                    headers, json));
            return null;
        }
        ApiResponse<T> response = json2Object(result, responseType);
        if (response == null || !response.isSucceed()) {
            log.warn(String.format("POST %s, headers: %s, body: %s. response is %s", url,
                    headers, json, result));
        }
        return response;
    }

    @Override
    public <T> ApiResponse<T> get(String path,
                                  Map<String, ?> queryParams,
                                  Map<String, String> headers,
                                  TypeReference<ApiResponse<T>> responseType) {
        String url = path + "?" + buildQueryString(queryParams);
        HttpGet httpGet = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpGet.setHeader(entry.getKey(), entry.getValue());
        }
        String result = makeRequest(httpGet);
        if (result == null) {
            log.warn(String.format("GET %s, headers: %s. result is null", url, headers));
            return null;
        }
        ApiResponse<T> response = json2Object(result, responseType);
        if (response == null || !response.isSucceed()) {
            log.warn(String.format("GET %s, headers: %s. response is %s", url, headers,
                    result));
        }
        return response;

    }

    @Override
    public <T> ApiResponse<T> post(String path,
                                   Map<String, ?> queryParams,
                                   Map<String, String> headers,
                                   TypeReference<ApiResponse<T>> responseType) {
        String url = path + "?" + buildQueryString(queryParams);
        HttpPost httpPost = new HttpPost(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        String result = makeRequest(httpPost);
        if (result == null) {
            log.warn(String.format("POST %s, headers: %s. result is null", url, headers));
            return null;
        }
        ApiResponse<T> response = json2Object(result, responseType);
        if (response == null || !response.isSucceed()) {
            log.warn(String.format("POST %s, headers: %s. response is %s", url, headers,
                    result));
        }
        return response;
    }

    private String makeRequest(HttpRequestBase httpRequestBase) {
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpRequestBase);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity, "UTF-8");
            }
        } catch (IOException e) {
            log.error(String.format("makeRequest %s %s, error!",
                            httpRequestBase.getMethod(), httpRequestBase.getURI()),
                    e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        return null;
    }

    private static <T> T json2Object(String json,
                                     TypeReference<T> valueTypeRef) {
        T object = null;
        try {
            object = MAPPER.readValue(json, valueTypeRef);

        } catch (IOException e) {
            log.error("json2Object error:", e);
        }
        return object;
    }

    @Override
    public void close() {
        try {
            httpClient.close();
        } catch (IOException ignored) {
        }
    }
}
