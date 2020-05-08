package com.ria084.themeparkinformation.linebot.constants;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

/**
 * 送信されたテキストに関連する定数を定義するクラス
 */
public class RequestTextConstants {

    /**
     * LAND情報を返却するキーワード
     * (このワードが含まれていればLAND情報を返却する)
     */
    public static final List<String> LAND_KEY = new ArrayList<>() {{
        add("ランド");
        add("陸");
        add("land");
    }};

    /**
     * SEA情報を返却するキーワード
     * (このワードが含まれていればSEA情報を返却する)
     */
    public static final List<String> SEA_KEY = new ArrayList<>() {{
        add("シー");
        add("海");
        add("sea");
    }};

    /**
     * 日付のフォーマッター
     * <p>
     * ex) 20200507
     */
    public static final DateTimeFormatter UUUUMMDD_FORMAT = DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

    /**
     * 日付の正規表現。MM/ddに対応
     * <p>
     * ex) 1/13
     */
    public static final String MMDD_WITHSLASH_PATTERN = "([0-9]{1,2}|1[0-2])/(3[01]|[12][0-9]|[0-9]{1,2})";

    /**
     * 日付の正規表現。MM月dd日に対応
     * <p>
     * ex) 1月13日
     */
    public static final String MMDD_JP_PATTERN = "([0-9]{1,2}|1[0-2])月(3[01]|[12][0-9]|[0-9]{1,2})日";

    /**
     * 返却するテキスト: 通常運営パターン
     * <p>
     * ex) 2020年5月7日のTDL運営時間は9:00～22:00です。
     */
    public static final String NORMAL_OPERATION = "%sの%s運営時間は%s～%sです。";

    /**
     * 返却するテキスト: 特殊運営パターン
     * <p>
     * ex) 2020年5月7日のTDSは休園です。詳細な情報については公式サイトを確認してください。
     */
    public static final String SPECIAL_OPERATION = "%sの%sは%sです。詳細な情報については公式サイトを確認してください。";

    /**
     * 返却するテキスト: 内部エラー
     */
    public static final String INNNER_ERROR = "内部エラーが発生しました。時間をおいて再度試してください。";

}
