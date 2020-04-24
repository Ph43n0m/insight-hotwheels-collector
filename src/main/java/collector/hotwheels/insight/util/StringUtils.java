package collector.hotwheels.insight.util;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import java.text.MessageFormat;

public final class StringUtils {
    private final static StringBuilder sb = new StringBuilder();

    public static String formatString(String pattern, String... args) {
        String ret = pattern;
        if (args != null) {
            MessageFormat formatter = new MessageFormat(pattern);
            StringBuffer output = new StringBuffer(256);
            ret = formatter.format(args, output, null)
                    .toString();
        }
        return ret;
    }

    public static String cleanDateString(String content) {
        if (!isNullOrEmpty(content) && !content.startsWith("http://") && !content.startsWith("https://") &&
                content.contains("-") && content.contains(":") && content.contains(".")) {
            return content.substring(0, content.lastIndexOf("."));
        } else {
            return content;
        }
    }

    public static boolean isNullOrEmpty(String text) {
        return text == null || org.apache.commons.lang.StringUtils.isEmpty(text);
    }

    public static String concatenate(String... args) {
        sb.delete(0, sb.length());

        if (args == null) {
            return "";
        }

        for (String s : args) {
            if (!isNullOrEmpty(s)) {
                sb.append(s);
            }
        }

        return sb.toString();
    }

    public static String trimReduce(String text) {
        return text.replaceAll("\\s+", "")
                .trim();
    }

    public static String URIDecode(String text) {
        String ret = text;

        try {
            ret = URIUtil.decode(ret);
        } catch (URIException ignore) {
        }
        return ret;
    }
}
