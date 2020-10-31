package com.moon.core.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.Labels;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.moon.core.io.IOUtil;
import com.moon.core.lang.StringUtil;
import com.moon.core.util.TestUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author moonsky
 */
class TreeElementTestTest {

    @Test
    void testBase64() throws Exception {
        byte[] bytes = Base64.getDecoder().decode("5pys5pyI5qaC5Ya1");
        System.out.println(new String(bytes));
        bytes = Base64.getUrlDecoder()
            .decode("5LiK5rW35Zu96ZmF5LyB5Lia5ZWG5Yqh5ZKo6K-i5pyN5Yqh5pyJ6ZmQ5YWs5Y-477yI5Yy65Z-f77yJ");
        System.out.println(new String(bytes));
    }

    @Test
    @Disabled
    void testOfArray() throws Exception {
        List<KeyValue> list = getCitiesList();
        /**
         * 树化
         */
        List<TreeElement<KeyValue>> elements = TreeElement.fromList(list, kv -> {
            String key = kv.getKey();
            if (key.endsWith("0000")) {
                return null;
            }
            if (key.endsWith("00")) {
                return key.substring(0, 2) + "0000";
            }
            return key.substring(0, 4) + "00";
        }, KeyValue::getKey, KeyValue::getValue, key -> {
            if (key.endsWith("00")) {
                return key.substring(0, 2) + "0000";
            } else {
                return key.substring(0, 4) + "00";
            }
        });
        System.out.println(elements);

        SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
        filter.getExcludes().add("data");
        System.out.println(JSON.toJSONString(elements, Labels.excludes("data")));
        System.out.println("========================================");
        System.out.println(JSON.toJSONString(elements, filter));
    }

    /**
     * 获取所有区级行政区代码
     *
     * @return
     *
     * @throws IOException
     */
    private List<KeyValue> getCitiesList() throws IOException {
        Document document = Jsoup.connect("http://www.mca.gov.cn///article/sj/xzqh/2020/2020/2020072805001.html").get();
        List<Element> list = document.body().select("td").stream().filter(element -> {
            return StringUtil.isNotBlank(element.text());
        }).collect(Collectors.toList());
        Iterator<Element> iterator = list.iterator();
        List<KeyValue> cities = new ArrayList<>();
        while (true) {
            String cityCodeVal = null;
            while (iterator.hasNext()) {
                Element element = iterator.next();
                String text = element.text().trim();
                if (text.length() == 6 && TestUtil.isDigit(text)) {
                    cityCodeVal = text;
                    break;
                }
            }
            if (iterator.hasNext()) {
                String cityNameVal = iterator.next().text();
                if (StringUtil.isNotEmpty(cityCodeVal) && StringUtil.isNotEmpty(cityNameVal)) {
                    cities.add(KeyValue.of(cityCodeVal, cityNameVal));
                }
            } else {
                break;
            }
        }
        return cities;
    }

    @Test
    void testGetCitiesList() throws Exception {
        // printHtml("http://www.mca.gov.cn///article/sj/xzqh/2020/2020/2020072805001.html");
        // printHtml("http://www.mca.gov.cn/article/sj/xzqh/2020/202009/20200900029578.shtml");
        // printHtml("http://www.mca.gov.cn//article/sj/xzqh/2020/2020/2020092500801.html");
        // printHtml("http://www.mca.gov.cn/wap/article/sj/xzqh/2020/");
        // printHtml("http://www.mca.gov.cn/article/sj/xzqh/2018/201806/20180600009419.shtml");
        printHtml("http://www.mca.gov.cn/article/sj/xzqh/2018/201806/20180600009855.shtml");
    }

    void printHtml(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        System.out.println(IOUtil.toString(url));
        // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // InputStream stream = connection.getInputStream();
        // String content = IOUtil.toString(stream);
        // System.out.println(content);
    }

    @Test
    void testName() throws Exception {
        // String effectiveUrl = toRedirectedUrl("http://www.mca.gov.cn/article/sj/xzqh/2020/202009/20200900029578.shtml");
        // System.out.println(effectiveUrl);
        // boolean matches = "http://www.mca.gov.cn//article/sj/xzqh/2020/2020/2020092500801.html".matches(
        //     "(http(?:s)?:[\\w\\.\\-/]+)");
        // System.out.println(matches);

        Matcher matcher = YEARLY2MONTHLY_PATTERN.matcher(
            " window.location.href=\"/article/sj/xzqh/2018/201804-12/201803-06041621.html\";");
        System.out.println(matcher.find());
    }

    final static Pattern YEARLY2MONTHLY_PATTERN = Pattern.compile(
        "location.href\\s{0,5}=\\s{0,5}['\"](http(?:s)?:[\\w\\.\\-/]+)['\"]");

    private static String toRedirectedUrl(String srcUrl) {
        String content = toString(srcUrl);
        // System.out.println(content);
        int index = content.indexOf("location.href");
        System.out.println(content.substring(index, index+100));
        Matcher matcher = YEARLY2MONTHLY_PATTERN.matcher(content);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Test
    void testYearlyList() throws Exception {
        String content = toString("http://www.mca.gov.cn/wap/article/sj/xzqh/2020/");
        // extraYearlyUrlList(content).forEach(System.out::println);
    }

    private static List<String> extraYearlyUrlList(String content) {
        Set<String> urls = new TreeSet<>(Comparator.reverseOrder());
        String regexTemplate = "href=\"([/\\w\\d\\.]+)\"(?:.{1,30}){4}年\\d{1,2}月(?:[\u4E00-\u9FAF]{1,16})县以上行政区划代码";
        Pattern pattern = Pattern.compile(regexTemplate);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return new ArrayList<>(urls);
    }

    private static String toString(String url) {
        try {
            return IOUtil.toString(new URL(url));
        } catch (Exception e) {
            throw new IllegalArgumentException(url);
        }
    }
}