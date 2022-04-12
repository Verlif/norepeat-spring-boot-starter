package idea.verlif.spring.norepeat.entity;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:33
 */
public final class RequestFlag implements Serializable {

    private final long time;

    private final long interval;

    private final String uri;

    private final String body;

    private final Map<String, String> headerMap;

    private final Map<String, String[]> paramMap;

    public RequestFlag(HttpServletRequest request, long interval) {
        this.time = System.currentTimeMillis();
        this.interval = interval;
        this.uri = request.getRequestURI();
        this.headerMap = new HashMap<>();
        this.paramMap = new HashMap<>();
        this.paramMap.putAll(request.getParameterMap());

        // 添加头
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            this.headerMap.put(header, request.getHeader(header));
        }
        String temp;
        StringBuilder sb = new StringBuilder();
        try (InputStream inputStream = request.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            temp = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            temp = "";
        }
        body = temp;
    }

    public long getTime() {
        return time;
    }

    public long getInterval() {
        return interval;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public Map<String, String[]> getParamMap() {
        return paramMap;
    }

    @Override
    public String toString() {
        return "RequestFlag{" +
                "time=" + time +
                ", body='" + body + '\'' +
                ", headerMap=" + Arrays.toString(headerMap.values().toArray()) +
                ", paramMap=" + Arrays.deepToString(paramMap.values().toArray(new String[0][0])) +
                '}';
    }
}
