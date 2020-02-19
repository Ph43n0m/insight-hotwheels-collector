package collector.hotwheels.insight.util.rest;

import java.util.HashMap;
import java.util.Map;

public final class ClientSettings {

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    private int readTimeout;

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    private int connectionTimeout;

    public int getExecutionDelay() {
        return executionDelay;
    }

    public void setExecutionDelay(int executionDelay) {
        this.executionDelay = executionDelay;
    }

    private int executionDelay;

    public int getExecutionRetries() {
        return executionRetries;
    }

    public void setExecutionRetries(int executionRetries) {
        this.executionRetries = executionRetries;
    }

    private int executionRetries;

    private Map<String, String> headers;

    public Map<String, String> getHeaders() {
        return headers;
    }

    private String charset = "UTF-8";

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (headers.containsKey(key)) {
            headers.remove(key);
        }
        headers.put(key, value);
    }

    public ClientSettings() {
        this.connectionTimeout = 5000;
        this.readTimeout = 60000;
        this.executionDelay = 2000;
        this.executionRetries = 0;
    }

    public ClientSettings(int connectionTimeout, int readTimeout, int executionDelay, int executionRetries) {
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
        this.executionDelay = executionDelay;
        this.executionRetries = executionRetries;
    }
}
