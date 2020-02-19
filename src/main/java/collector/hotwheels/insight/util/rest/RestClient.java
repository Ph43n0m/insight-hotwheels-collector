package collector.hotwheels.insight.util.rest;

import collector.hotwheels.insight.util.StringUtils;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public void setClientSettings(ClientSettings clientSettings) {
        this.clientSettings = clientSettings;
    }

    private ClientSettings clientSettings;

    public DateTimeConverter getDateTimeConverter() {
        return dateTimeConverter;
    }

    public void setDateTimeConverter(DateTimeConverter dateTimeConverter) {
        this.dateTimeConverter = dateTimeConverter;
    }

    private DateTimeConverter dateTimeConverter;

    public String getEndPoint() {
        return endPointSettings.getEndpoint();
    }

    /**
     * set an endpoint path that extends the base url
     *
     * @param endPointPath (an endpoint path that extends the base url)
     * @param args (optional arguments that will be replaced in the string)
     */
    public void setEndPointPath(String endPointPath, String... args) {
        this.endPointPath = StringUtils.formatString(endPointPath, args);
    }

    public String getEndPointPath() {
        return endPointPath;
    }

    public void setEndPointPath(String endPointPath) {
        this.endPointPath = endPointPath;
    }

    private String endPointPath;

    public String getParams() {
        return params;
    }

    /**
     * set parameters that will be attached to the request
     *
     * @param params (the parameters without ? (example &format=xml&skip=10&max=100)
     */
    public void setParams(String params) {
        this.params = params;
    }

    private String params;
    private OkHttpClient httpClient;
    private EndPointSettings endPointSettings;

    /**
     * Initiate a RestClient
     */
    public RestClient(EndPointSettings endPointSettings) {
        this.clientSettings = new ClientSettings(5000, 60000, 2000, 0);
        this.endPointSettings = endPointSettings;
        this.dateTimeConverter = new DateConverter();
        this.dateTimeConverter.setPatterns(
                new String[] {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS",
                        "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy'T'HH:mm:ss", "dd/MM/yyyy'T'HH:mm:ss.SSS", "yyyy-MM-dd"});
        ConvertUtils.register(this.dateTimeConverter, Date.class);

    }

    private OkHttpClient getClient() {
        if (httpClient == null) {
            if (!StringUtils.isNullOrEmpty(endPointSettings.getCredentialBase())) {
                httpClient =
                        new OkHttpClient.Builder().readTimeout(clientSettings.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .connectTimeout(clientSettings.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                                .writeTimeout(clientSettings.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .addInterceptor(new BasicAuthInterceptor(endPointSettings.getCredentialBase()))
                                .build();
            } else {
                httpClient =
                        new OkHttpClient.Builder().readTimeout(clientSettings.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .connectTimeout(clientSettings.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .writeTimeout(clientSettings.getReadTimeout(), TimeUnit.MILLISECONDS)
                                .build();
            }

            OkHttpClient.Builder builder = httpClient.newBuilder();
            builder.sslSocketFactory(TrustManagerImpl.trustAllSslSocketFactory,
                    (X509TrustManager) TrustManagerImpl.trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            httpClient = builder.build();
        }

        return httpClient;
    }

    private String getTargetUrl(String path) {

        String ret = null;

        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(endPointSettings.getEndpoint());

            String sysQ = null;

            if (!StringUtils.isNullOrEmpty(path) && path.contains("?")) {
                sysQ = path.substring(path.indexOf("?") + 1);
                path = path.substring(0, path.indexOf("?"));
            }

            if (!StringUtils.isNullOrEmpty(path)) {
                uriBuilder.path(path);
            }

            ret = uriBuilder.build()
                    .toURL()
                    .toString();

            if (!StringUtils.isNullOrEmpty(sysQ)) {
                if (ret.contains("?")) {
                    ret += "&" + sysQ;
                } else {
                    ret += "?" + sysQ;
                }
            }
            if (!StringUtils.isNullOrEmpty(params) && params.startsWith("&")) {
                if (!ret.contains("?")) {
                    ret += "?" + params.substring(1);
                } else {
                    ret += params;
                }
            }
        } catch (Exception ex) {
            logger.error("Error building target URL.", ex);
        }
        return ret;
    }

    public String getResponseContent() {
        String ret = null;

        try {
            int responseCode = 500;
            int retry = 0;

            Request request;

            if (clientSettings.getHeaders() != null && !clientSettings.getHeaders()
                    .isEmpty()) {
                request = new Request.Builder().url(getTargetUrl(this.endPointPath))
                        .headers(Headers.of(clientSettings.getHeaders()))
                        .build();
            } else {
                request = new Request.Builder().url(getTargetUrl(this.endPointPath))
                        .build();
            }

            logger.debug("Try getting response from: " + request.url());

            while (responseCode == 500 && retry <= clientSettings.getExecutionRetries()) {
                if (retry > 0) {
                    logger.error(
                            String.format("Request limit reached. Do a retry (%s) for %s", retry, this.endPointPath));
                }

                try (Response response = getClient().newCall(request)
                        .execute()) {
                    responseCode = response.code();
                    if (!response.isSuccessful() && responseCode != 500) {
                        logger.warn(String.format("Response Error %s - %s for %s", response.code(), response.message(),
                                response.request()
                                        .url()));
                        return null;
                    } else if (response.isSuccessful()) {
                        ret = response.body()
                                .source()
                                .readString(Charset.forName(clientSettings.getCharset()));
                    }
                }
                if (responseCode == 500) {
                    Thread.sleep(clientSettings.getExecutionDelay());
                    retry++;
                }
            }

        } catch (Exception ex) {
            logger.error("Error getting response.", ex);
        }
        return ret;
    }

    public Document getXMLResponseContent() {
        Document ret = null;
        try {
            String content = getResponseContent();

            if (!StringUtils.isNullOrEmpty(content)) {
                ByteArrayInputStream input = null;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                StringBuilder xmlStringBuilder = new StringBuilder();
                String responseBody = content;

                if (!StringUtils.isNullOrEmpty(responseBody)) {
                    if (!responseBody.startsWith("<?xml")) {
                        xmlStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    } else if (responseBody.startsWith("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>")) {
                        responseBody = responseBody.replace("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>",
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }

                    xmlStringBuilder.append(responseBody);

                    input = new ByteArrayInputStream(xmlStringBuilder.toString()
                            .getBytes());

                    if (input != null) {
                        ret = builder.parse(input);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error getting XMLDocument result from resource.", ex);
            return null;
        }
        return ret;
    }

    public boolean downloadFile(String downloadPath, String url, String fileName) {

        boolean ret = false;

        try {
            final File folder = new File(downloadPath);
            if (folder.exists() && folder.isDirectory()) {

                int responseCode = 500;
                int retry = 0;

                Request request = new Request.Builder().url(url)
                        .build();

                while (responseCode == 500 && retry <= clientSettings.getExecutionRetries()) {
                    if (retry > 0) {
                        logger.error(String.format("Request limit reached. Do a retry (%s) for %s", retry,
                                this.endPointPath));
                    }
                    try (Response response = getClient().newCall(request)
                            .execute()) {
                        responseCode = response.code();
                        if (!response.isSuccessful() && responseCode != 500) {
                            logger.warn(
                                    String.format("Response Error %s - %s for %s", response.code(), response.message(),
                                            response.request()
                                                    .url()));
                            return false;
                        } else if (response.isSuccessful()) {
                            FileOutputStream fileOutputStream = new FileOutputStream(Paths.get(downloadPath, fileName)
                                    .toString());

                            fileOutputStream.write(response.body()
                                    .bytes());
                            fileOutputStream.close();

                            ret = true;
                        }
                    }
                    if (responseCode == 500) {
                        Thread.sleep(clientSettings.getExecutionDelay());
                        retry++;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error downloading file.", ex);
            return false;
        }

        return ret;
    }

    public boolean downloadFile(String downloadPath, String fileName) {
        return downloadFile(downloadPath, getTargetUrl(this.endPointPath), fileName);
    }
}
