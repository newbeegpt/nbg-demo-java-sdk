package io.github.neweegpt;


import io.github.neweegpt.response.ApiResponse;


/**
 * @author newbeegpt
 */
public class Demo {
    public static void main(String[] args) {
        Client liveClient = new DefaultClient("appId", "appSecret", "https://github.com");
        String code = "1234";
        ApiResponse<String> authorizationCodeResponse = liveClient
                .authorizationCode(code);
        System.out.println(authorizationCodeResponse);
        if (authorizationCodeResponse == null
                || !authorizationCodeResponse.isSucceed()) {
            liveClient.close();
            return;
        }

        liveClient.close();
    }
}
