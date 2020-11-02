package com.moon.area;

import com.moon.core.io.IOUtil;
import com.moon.core.net.URLUtil;
import com.moon.core.time.DateTime;
import com.moon.core.util.LocalStorage;
import com.moon.core.util.MapUtil;
import com.moon.core.util.SetUtil;
import com.moon.core.util.ValidateUtil;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author moonsky
 */
public class CityCodePuller {

    private final static String DOMAIN = "http://www.mca.gov.cn";
    private final static String yearlyUrl = "http://www.mca.gov.cn/wap/article/sj/xzqh/%d/";

    private final static int MIN_YEAR = 2018;
    private final DateTime datetime;

    private final LocalStorage localStorage = LocalStorage.of(getClass().getPackage().getName());

    private CityCodePuller(DateTime datetime) {
        DateTime mutable = datetime.asMutable();
        this.datetime = mutable == datetime ? mutable.clone() : mutable;
    }

    public static CityCodePuller of(int year) {
        return new CityCodePuller(DateTime.ofFields(year));
    }

    public Object pull(int month) {
        this.datetime.withMonth(month);
        return pull();
    }

    public Object pull(int year, int month) {
        this.datetime.withYear(year).withMonth(month);
        return pull();
    }

    private final Object pull() {
        requireEffectiveYearMonth(datetime);
        throw new UnsupportedOperationException();
    }

    private static void requireEffectiveYearMonth(DateTime datetime) {
        boolean isBefore = DateTime.ofFields(MIN_YEAR).isDatetimeBefore(datetime);
        ValidateUtil.requireTrue(isBefore, "不能获取早于" + MIN_YEAR + "的区域行政代码信息");
    }

    private final static Pattern YEAR_MONTH_PATTERN = Pattern
        .compile("(\\d{4})[\u4E00-\u9FAF]{1,3}(\\d{1,2})[\u4E00-\u9FAF]+");

    @SuppressWarnings("all")
    Map<String, MonthlyUrlModel> getYearlyUrlList(int year) {
        Map<String, MonthlyUrlModel> yearlyUrlModelMap = new TreeMap<>(Comparator.reverseOrder());
        for (int page = 1; page < 3; page++) {
            String content = toString(toYearlyUrl(year, page));
            // 将月份信息按 yyyy-MM: monthInfo，组成，并按月份倒序
            SetUtil.reduce(extraYearlyUrlList(content), (yearlyMap, model, i) ->//
                MapUtil.put(yearlyMap, toMonthlyUrlKey(model), model), yearlyUrlModelMap);
        }
        return yearlyUrlModelMap;
    }

    private final Pattern YEARLY_PATTERN = Pattern
        .compile("href=\"([/\\w\\d\\.]+)\"(?:.{1,30})(\\d{4}年\\d{1,2}月(?:[\u4E00-\u9FAF]{1,16})县以上行政区划代码)");

    private SortedSet<MonthlyUrlModel> extraYearlyUrlList(String content) {
        SortedSet<MonthlyUrlModel> urls = new TreeSet<>();
        Matcher matcher = YEARLY_PATTERN.matcher(content);
        while (matcher.find()) {
            String url = URLUtil.concatUrls(DOMAIN, matcher.group(1));
            urls.add(new MonthlyUrlModel(url, toRedirectedUrl(url), matcher.group(2)));
        }
        return urls;
    }

    private final static Pattern YEARLY2MONTHLY_HTTP_PATTERN = Pattern
        .compile("location.href\\s{0,5}=\\s{0,5}['\\\\\"](http(?:s)?:[\\w\\.\\-/]+)['\\\\\"]");

    private final static Pattern YEARLY2MONTHLY_PATH_PATTERN = Pattern
        .compile("location.href\\s{0,5}=\\s{0,5}['\\\\\"]([\\w\\.\\-/]+)['\\\\\"]");

    private String toRedirectedUrl(String srcUrl) {
        String content = toString(srcUrl);
        Matcher matcherHttp = YEARLY2MONTHLY_HTTP_PATTERN.matcher(content);
        if (matcherHttp.find()) {
            return matcherHttp.group(1);
        }
        Matcher matcher = YEARLY2MONTHLY_PATH_PATTERN.matcher(content);
        return matcher.find() ? URLUtil.concatUrls(DOMAIN, matcher.group(1)) : null;
    }

    private String toString(String url) {
        try {
            return IOUtil.toString(new URL(url));
        } catch (Exception e) {
            throw new IllegalArgumentException(url);
        }
    }

    private static String toYearlyUrl(int year, int page) {
        String url = String.format(yearlyUrl, year);
        return page > 1 ? (url + "?" + page) : url;
    }

    /**
     * 这个 key 在 localStorage 中是指向每个月份对应的 url 信息
     *
     * @param urlModel
     *
     * @return
     */
    private static String toMonthlyUrlKey(MonthlyUrlModel urlModel) {
        return toMonthlyUrlKey(urlModel.getName());
    }

    /**
     * 提取年月返回 key
     *
     * @param monthlyName 形如 "yyyy年MM月xxxx县以上行政代码"
     *
     * @return
     */
    private static String toMonthlyUrlKey(String monthlyName) {
        Matcher matcher = YEAR_MONTH_PATTERN.matcher(monthlyName);
        if (matcher.find()) {
            return toMonthlyUrlKey(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        }
        return null;
    }

    /**
     * 使用年月返回 url key
     *
     * @param year  不小于 2018
     * @param month 1 ~ 12
     *
     * @return
     */
    private static String toMonthlyUrlKey(int year, int month) {
        // CCP = CityCodePuller
        return String.format(month < 10 ? ".MONTHLY-URL-%d-0%d" : ".MONTHLY-URL-%s-%s", year, month);
    }


    /**
     * 使用年月返回 detail key
     *
     * @param year  不小于 2018
     * @param month 1 ~ 12
     *
     * @return
     */
    private static String toMonthlyDetailKey(int year, int month) {
        return String.format(month < 10 ? ".MONTHLY-DETAIL-%d-0%d" : ".MONTHLY-DETAIL-%s-%s", year, month);
    }

    private static void assertYear(int year) {
        ValidateUtil.requireGtOf(year, MIN_YEAR, "CityCodePuller 暂不支持" + (MIN_YEAR + 1) + "年前年份");
    }
}
