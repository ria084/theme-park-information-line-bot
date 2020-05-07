package com.ria084.themeparkinformation.linebot.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.model.message.TextMessage;
import com.ria084.themeparkinformation.linebot.constants.RequestTextConstants;
import com.ria084.themeparkinformation.linebot.domain.model.RequestModel;
import com.ria084.themeparkinformation.linebot.domain.model.ResponseModel;
import com.ria084.themeparkinformation.linebot.util.UtilResources;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 送信されたテキストに応じた情報の返却を行うクラス
 */
@Service
@AllArgsConstructor
@Slf4j
public class ThemeParkInformationResponseService {
    private final ResponseModel responseModel;
    private final RequestModel requestModel;

    public TextMessage generateResponse(String requestText) {
        boolean isLand = false;
        boolean isSea = false;

        // 必須要素が入っているか確認する(日付設定も含む)
        if (!validationRequest(requestText)) {
            // 指定がない場合、今日の陸情報を返す
            return new TextMessage("今日の情報です");
        }

        // 陸判定
        for (String text : RequestTextConstants.LAND_KEY) {
            if (requestText.contains(text)) {
                isLand = true;
                break;
            }
        }

        // 海判定
        for (String text : RequestTextConstants.SEA_KEY) {
            if (requestText.contains(text)) {
                isSea = true;
                break;
            }
        }

        String responseText = "";
        try {
            String openingHours = UtilResources.getOpeningHours(false, true);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, ResponseModel.Detail> response = mapper.readValue(openingHours, new TypeReference<HashMap<String, ResponseModel.Detail>>(){});
            ResponseModel.Detail timeDetail = response.get(requestModel.getTargetDate());

            // 備考欄が空なら通常通り開園/閉園時刻を通知
            if (timeDetail.getNote().isEmpty()){
                responseText = getStringTargetDate(requestModel.getTargetDate()) + "の運営時間は" + timeDetail.getOpenTime() + "～" + timeDetail.getCloseTime() + "です。";
            } else {
                // 備考欄の記載があれば、注意事項を送付
                responseText = getStringTargetDate(requestModel.getTargetDate()) + "は" + timeDetail.getNote() + "です。詳細な情報については公式サイトを確認してください。";
            }
        } catch (IOException e) {
            log.info("err");
            // TODO 例外処理
        }

        return new TextMessage(responseText);
    }

    private boolean validationRequest(String requestText) {

        // 日付を示す文字列が入っていれば有効
        if (requestText.contains("今日") || requestText.contains("きょう")) {
            // 取得対象日付を今日に設定
            requestModel.setTargetDate(LocalDate.now().format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        if (requestText.contains("明日") || requestText.contains("あした")) {
            // 取得対象日付を明日に設定
            requestModel.setTargetDate(LocalDate.now().plusDays(1).format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        if (requestText.contains("明後日") || requestText.contains("あさって")) {
            // 取得対象日付を明後日に設定
            requestModel.setTargetDate(LocalDate.now().plusDays(2).format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        // フリーテキストから正規表現で日付を抜き出し
        try {
            // 日付形式チェック(MM/dd)
            Pattern withSlashPattern = Pattern.compile(RequestTextConstants.MMDD_WITHSLASH_PATTERN);
            Matcher withSlash = withSlashPattern.matcher(requestText);
            if (withSlash.find()) {
                int month = Integer.parseInt(withSlash.group(1));
                int day = Integer.parseInt(withSlash.group(2));

                requestModel.setTargetDate(getTargetDate(month, day).format(RequestTextConstants.UUUUMMDD_FORMAT));
                return true;
            }

            // 日付形式チェック(m月n日)
            Pattern jpPattern = Pattern.compile(RequestTextConstants.MMDD_JP_PATTERN);
            Matcher jp = jpPattern.matcher(requestText);
            if (jp.find()) {
                int month = Integer.parseInt(jp.group(1));
                int day = Integer.parseInt(jp.group(2));

                requestModel.setTargetDate(getTargetDate(month, day).format(RequestTextConstants.UUUUMMDD_FORMAT));
                return true;
            }

            // TODO 日付形式チェック 年指定の追加
            // 日付形式チェック(x年y月z日)
            // 日付形式チェック(uuuu/MM/dd)
        } catch (IllegalStateException e) {
            log.warn("日付フォーマットエラーです。");
            throw new IllegalArgumentException("日付フォーマットが正しくありません。MM/DD,MMDD,MM月DD日のいづれかの形式で指定してください。", e);
        }

        return false;
    }

    protected LocalDate getTargetDate(int month, int day) {
        // いったん現在日の年で生成
        LocalDate tmpDate = LocalDate.of(YearMonth.now().getYear(), month, day);

        // 生成した日付が現在より過去の場合、1年後の日付を返却
        if (tmpDate.isBefore(LocalDate.now())) {
            return tmpDate.plusYears(1);
        }

        // 現在と同じもしくは未来の場合、生成した日付で返却
        return tmpDate;

    }

    /**
     * uuuuMMdd形式の日付文字列をuuuu年MM月dd日の形式で返却する
     *
     * @param date 対象の日付。フォーマットはuuuuMMdd
     * @return 対象の日付。uuuu年MM月dd日の形式。
     */
    private String getStringTargetDate(String date) {

        LocalDate targetDate = LocalDate.parse(date, RequestTextConstants.UUUUMMDD_FORMAT);

        return targetDate.getYear() + "年" + targetDate.getMonthValue() + "月" + targetDate.getDayOfMonth() + "日";

    }
}
