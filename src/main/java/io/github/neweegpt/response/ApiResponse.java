package io.github.neweegpt.response;

/**
 * @author newbeegpt
 */
public class ApiResponse<T> {

    /**
     * 0 succeed
     */
    private int code;

    /**
     * api result
     */
    private T result;

    public ApiResponse() {
    }

    public int getCode() {
        return code;
    }


    public T getResult() {
        return result;
    }

    /**
     * @return Whether the api is successful
     */
    public boolean isSucceed() {
        return code == 0 && result != null;
    }

    @Override
    public String toString() {
        return "CommonResponse{" +
                "code=" + code +
                ", result=" + result +
                '}';
    }
}
