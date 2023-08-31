package com.newbeegpt.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.newbeegpt.demo.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class Client {

    private final String appId;
    private final String appSecret;

    private final String baseUrl;


    public Client(String appId, String appSecret, String baseUrl) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.baseUrl = baseUrl;
    }


    private void wrapHeader(Map<String, String> headers,
                            Map<String, ?> params) {
        headers.put("sign", appSecret);
    }

    /**
     * 验证授权码
     *
     * @param code 授权码
     * @return token信息
     */
    public ApiResponse<String> authorizationCode(String code) {
        String api = this.baseUrl;
        Map<String, String> params = new HashMap<String, String>();
        params.put("appId", this.appId);
        params.put("code", code);
        params.put("grant_type", "authorization_code");
        Map<String, String> headers = new HashMap<String, String>();
        wrapHeader(headers, params);
        return post(api, params, headers,
                new TypeReference<ApiResponse<String>>() {
                });
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新token
     * @return token信息
     */
    public ApiResponse<String> refreshToken(String refreshToken) {
        String api = this.baseUrl;
        Map<String, String> params = new HashMap<String, String>();
        params.put("appId", this.appId);
        params.put("refresh_token", refreshToken);
        params.put("grant_type", "refresh_token");
        Map<String, String> headers = new HashMap<String, String>();
        wrapHeader(headers, params);
        return post(api, params, headers,
                new TypeReference<ApiResponse<String>>() {
                });
    }


    public abstract <T> ApiResponse<T> postJson(String path, String json,
                                                Map<String, String> headers,
                                                TypeReference<ApiResponse<T>> responseType);

    public abstract <T> ApiResponse<T> get(String path,
                                           Map<String, ?> uriVariables,
                                           Map<String, String> headers,
                                           TypeReference<ApiResponse<T>> responseType);

    public abstract <T> ApiResponse<T> post(String path,
                                            Map<String, ?> uriVariables,
                                            Map<String, String> headers,
                                            TypeReference<ApiResponse<T>> responseType);

    public abstract void close();
}
