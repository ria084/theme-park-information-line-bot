package com.ria084.themeparkinformation.linebot.constants;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * 送信されたテキストに関連する定数を定義するクラス
 */
public class RequestTextConstants {

    public static final List<String> LAND_KEY = new ArrayList<>() {{
        add("ランド");
        add("陸");
        add("land");
    }};

    public static final List<String> SEA_KEY = new ArrayList<>() {{
        add("シー");
        add("海");
        add("sea");
    }};

    public static final DateTimeFormatter UUUUMMDD_FORMAT = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

    public static final String MMDD_WITHSLASH_PATTERN = "([0-9]{1,2}|1[0-2])/(3[01]|[12][0-9]|[0-9]{1,2})";

    public static final String MMDD_JP_PATTERN = "([0-9]{1,2}|1[0-2])月(3[01]|[12][0-9]|[0-9]{1,2})日";
}
