package com.ria084.themeparkinformation.linebot.constants;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    public static final Pattern MMDD_PATTERN = Pattern.compile("([0-9]{1,2}|1[0-2])([1-9]|[12][0-9]|3[01])");

    public static final Pattern MMDD_WITHSLASH_PATTERN = Pattern.compile("([0-9]{1,2}|1[0-2])/([1-9]|[12][0-9]|3[01])");

    public static final Pattern MMDD_JP_PATTERN = Pattern.compile("([0-9]{1,2}|1[0-2])月([1-9]|[12][0-9]|3[01])日");
}
