package group.chatroom.chatroomclient.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper {

    //applicationMethod 前面要斜杠！
    public static JsonNode get(String applicationMethod, Map<String, Object> params) {
        return execute(buildRequest("get", applicationMethod, buildRequestBody(params)));
    }

    //applicationMethod 前面要斜杠！
    public static JsonNode post(String applicationMethod, Map<String, Object> params) {
        return execute(buildRequest("post", applicationMethod, buildRequestBody(params)));
    }

    //execute request and convert result to json
    private static JsonNode execute(Request request) {
        JsonNode responseJson = null;
        try {
            Response response = HTTP_CLIENT.newCall(request).execute();
            responseJson = stringToJson(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseJson;
    }

    //
    private static RequestBody buildRequestBody(Map<String, Object> params) {
        return RequestBody.create(Objects.requireNonNull(jsonToString(mapToJson(params))), JSON_MEDIA_TYPE);
    }

    //applicationMethod 前面要斜杠！
    private static Request buildRequest(String httpMethod, String applicationMethod, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder();
        //httpMethod
        if ("get".equals(httpMethod.toLowerCase())) builder.get();
        else if ("post".equals(httpMethod.toLowerCase())) builder.post(requestBody);
        else throw new RuntimeException("不支持的请求方法！");
        //url
        builder.url(SERVER_HOST + applicationMethod);
        //
        return builder.build();
    }

    /**
     * map转jsonNode
     *
     * @param map json的map
     * @return jsonNode
     */
    public static JsonNode mapToJson(Map<String, Object> map) {
        return OBJECT_MAPPER.convertValue(map, JsonNode.class);
    }

    /**
     * json转字符串，序列化
     *
     * @param jsonNode jsonNode
     * @return 序列化后字符串
     */
    public static String jsonToString(JsonNode jsonNode) {
        try {
            return OBJECT_MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转Json
     *
     * @param jsonString 序列化的json对象字符串
     * @return 反序列化的JsonNode
     */
    public static JsonNode stringToJson(String jsonString) {
        try {
            return OBJECT_MAPPER.readTree(jsonString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String SERVER_HOST = "*****";
}
