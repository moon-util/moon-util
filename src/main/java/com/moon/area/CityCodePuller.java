package com.moon.area;

import com.moon.core.io.IOUtil;
import com.moon.core.net.URLUtil;
import com.moon.core.time.DateTime;
import com.moon.core.util.LocalStorage;
import com.moon.core.util.MapUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.ValidateUtil;

import java.net.URL;
import java.time.Month;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author moonsky
 */
public class CityCodePuller {

    private final static String DOMAIN = "http://www.mca.gov.cn";
    private final static String yearlyUrl = "http://www.mca.gov.cn/wap/article/sj/xzqh/%d/";

    private final static int MIN_YEAR = 2017;

    private final LocalStorage localStorage = LocalStorage.of(getClass().getPackage().getName());
    private final DateTime datetime;

    public CityCodePuller() { this(Calendar.getInstance().get(Calendar.YEAR)); }

    public CityCodePuller(int year) { this(year, 1); }

    public CityCodePuller(int year, int month) {
        assertYear(year);
        this.datetime = DateTime.ofFields(year, month);
    }

    private static String getYearlyKey(int year) {
        // CCP = CityCodePuller
        return String.format(".YEARLY-%d", year);
    }

    private static String getMonthlyKey(int year, int month) {
        // CCP = CityCodePuller
        return String.format(month < 10 ? ".MONTHLY-%d-0%d" : ".MONTHLY-%s-%s", year, month);
    }

    public static CityCodePuller of() {
        Calendar calendar = Calendar.getInstance();
        return of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    }

    public static CityCodePuller of(int year) { return of(year, 1); }

    public static CityCodePuller of(int year, int month) {
        return new CityCodePuller(year, month);
    }

    public void pull() {
        pull(datetime.getYearValue(), datetime.getMonthValue());
    }

    public void pull(int month) {
        pull(datetime.getYearValue(), month);
    }

    public void pull(int year, int month) {
        assertYear(year);
        // 验证 month 值是否正确
        Month.of(month);
        datetime.withYear(year).withMonth(month);
        localStorage.getOrPull(getMonthlyKey(year, month), () -> {

            return null;
        });
    }

    private static void getMonthlyUrlList(int year, int month) {

    }

    private final static Pattern YEAR_MONTH_PATTERN = Pattern.compile(
        "(\\d{4})[\u4E00-\u9FAF]{1,3}(\\d{1,2})[\u4E00-\u9FAF]+");

    private static String toMonthlyKey(YearlyUrlModel yearlyUrlModel) {
        return toMonthlyKey(yearlyUrlModel.getName());
    }

    private static String toMonthlyKey(String monthlyName) {
        Matcher matcher = YEAR_MONTH_PATTERN.matcher(monthlyName);
        if (matcher.find()) {
            return toMonthlyKey(Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2)));
        }
        return null;
    }

    private static String toMonthlyKey(int year, int month) {
        return getMonthlyKey(year, month);
    }

    static Map<String, YearlyUrlModel> getYearlyUrlList(int year) {
        Map<String, YearlyUrlModel> yearlyUrlModelMap = new TreeMap<>(Comparator.reverseOrder());
        for (int page = 1; page < 3; page++) {
            String content = toString(toYearlyUrl(year, page));
            SetUtil.reduce(extraYearlyUrlList(content), (yearlyMap, model, i) ->//
                MapUtil.put(yearlyMap, toMonthlyKey(model), model), yearlyUrlModelMap);
        }
        return yearlyUrlModelMap;
    }

    private final static Pattern YEARLY_PATTERN = Pattern.compile(
        "href=\"([/\\w\\d\\.]+)\"(?:.{1,30})(\\d{4}年\\d{1,2}月(?:[\u4E00-\u9FAF]{1,16})县以上行政区划代码)");

    private static SortedSet<YearlyUrlModel> extraYearlyUrlList(String content) {
        SortedSet<YearlyUrlModel> urls = new TreeSet<>();
        Matcher matcher = YEARLY_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = URLUtil.concatUrls(DOMAIN, matcher.group(1));
            urls.add(new YearlyUrlModel(url, toRedirectedUrl(url), matcher.group(2)));
        }
        return urls;
    }

    private final static Pattern YEARLY2MONTHLY_HTTP_PATTERN = Pattern.compile(
        "location.href\\s{0,5}=\\s{0,5}['\\\\\"](http(?:s)?:[\\w\\.\\-/]+)['\\\\\"]");

    private final static Pattern YEARLY2MONTHLY_PATH_PATTERN = Pattern.compile(
        "location.href\\s{0,5}=\\s{0,5}['\\\\\"]([\\w\\.\\-/]+)['\\\\\"]");

    private static String toRedirectedUrl(String srcUrl) {
        String content = toString(srcUrl);
        Matcher matcherHttp = YEARLY2MONTHLY_HTTP_PATTERN.matcher(content);
        if (matcherHttp.find()) {
            return matcherHttp.group(1);
        }
        Matcher matcher = YEARLY2MONTHLY_PATH_PATTERN.matcher(content);
        return matcher.find() ? URLUtil.concatUrls(DOMAIN, matcher.group(1)) : null;
    }

    private static String toYearlyUrl(int year, int page) {
        String url = String.format(yearlyUrl, year);
        return page > 1 ? (url + "?" + page) : url;
    }

    private static String toString(String url) {
        try {
            return IOUtil.toString(new URL(url));
        } catch (Exception e) {
            throw new IllegalArgumentException(url);
        }
    }

    private static void assertYear(int year) {
        ValidateUtil.requireGtOf(year, MIN_YEAR, "CityCodePuller 暂不支持" + (MIN_YEAR + 1) + "年前年份");
    }
}
