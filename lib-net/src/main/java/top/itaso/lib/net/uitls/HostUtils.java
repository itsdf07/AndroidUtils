package top.itaso.lib.net.uitls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Author itsdf07
 * @E-Mail 923255742@qq.com
 * @Github https://github.com/itsdf07
 * @Date 2020/11/5
 */
public class HostUtils {

    /**
     * 正则表达式过滤 url 中的 host
     *
     * @param url
     * @return
     */
    public static String filterHost(String url) {
        String host = "";
        if (url == null || url.length() == 0) {
            return host;
        }
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            host = matcher.group();
        }
        return host;
    }
}
