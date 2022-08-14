package june1.open.common.util;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ApiUtil {

    private final HttpMethod method;
    private final String url;
    private final String mediaType;
    private final String body;
    private Map<String, String> params;
    private Map<String, String> headers;

    @Builder
    public ApiUtil(HttpMethod method, String url, String mediaType, String body) {
        this.method = method;
        this.url = url;
        this.mediaType = mediaType;
        this.body = body;
    }

    private Request.Builder getRequest() {
        //PUT Method
        if (method.equals(HttpMethod.PUT)) {
            return new Request.Builder()
                    .url(setParams())
                    .method(HttpMethod.PUT.name(),
                            RequestBody.create(MediaType.parse(mediaType), body));
        }
        //GET Method
        else if (method.equals(HttpMethod.GET)) {
            return new Request.Builder()
                    .url(setParams())
                    .method(HttpMethod.GET.name(), null);
        }

        log.error("해당 메서드[{}]를 지원하지 않습니다.", method.name());
        throw new IllegalStateException("지정된 메서드를 지원하지 않음");
    }

    public String apiCall() throws IOException {
        try {
            String ret = null;
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = getRequest();
            headers.forEach(builder::addHeader);
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    ret = responseBody.string();
                    responseBody.close();
                }
            } else {
                log.error("url=[{}] api 호출을 실패했습니다.", url);
            }

            return ret;

        } catch (Exception e) {
            log.error("url=[{}] api 호출을 실패했습니다. 메시지=[{}]",
                    url, e.getMessage());
            throw e;
        }
    }

    public byte[] apiCallForByte() throws IOException {
        try {
            byte[] ret = null;
            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = getRequest();
            headers.forEach(builder::addHeader);
            Request request = builder.build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    ret = responseBody.bytes();
                    responseBody.close();
                }
            } else {
                log.error("url=[{}] api 호출을 실패했습니다.", url);
            }

            return ret;

        } catch (Exception e) {
            log.error("url=[{}] api 호출을 실패했습니다. 메시지=[{}]",
                    url, e.getMessage());
            throw e;
        }
    }

    public ApiUtil addHeader(String key, String value) {
        if (headers == null) headers = new HashMap<>();
        headers.put(key, value);
        return this;
    }

    public ApiUtil addParam(String key, String value) {
        if (params == null) params = new HashMap<>();
        params.put(key, value);
        return this;
    }

    private String setParams() {
        if (params == null || params.isEmpty()) return url;
        StringBuilder sb = new StringBuilder(url + "?");
        params.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
