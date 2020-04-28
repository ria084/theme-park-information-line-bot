package com.ria084.themeparkinformation.linebot.service;

import com.linecorp.bot.model.message.TextMessage;
import com.ria084.themeparkinformation.linebot.constants.RequestTextConstants;
import com.ria084.themeparkinformation.linebot.domain.model.ResponseModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * 送信されたテキストに応じた情報の返却を行うクラス
 */
@Service
@AllArgsConstructor
public class ThemeParkInformationResponseService {
    private final ResponseModel responseModel;

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

        // TODO テキストの取得


        return new TextMessage(responseModel.getTargetDate() + "の情報です");
    }

    private boolean validationRequest(String requestText) {

        // 日付を示す文字列が入っていれば有効
        if (requestText.contains("今日") || requestText.contains("きょう")) {
            // 取得対象日付を今日に設定
            responseModel.setTargetDate(LocalDate.now().format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        if (requestText.contains("明日") || requestText.contains("あした")) {
            // 取得対象日付を明日に設定
            responseModel.setTargetDate(LocalDate.now().plusDays(1).format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        if (requestText.contains("明後日") || requestText.contains("あさって")) {
            // 取得対象日付を明後日に設定
            responseModel.setTargetDate(LocalDate.now().plusDays(2).format(RequestTextConstants.UUUUMMDD_FORMAT));

            return true;
        }

        // 日付形式チェック(MM/dd)
        if (RequestTextConstants.MMDD_WITHSLASH_PATTERN.matcher(requestText).find()) {
            int month = Integer.parseInt(RequestTextConstants.MMDD_WITHSLASH_PATTERN.matcher(requestText).group(1));
            int day = Integer.parseInt(RequestTextConstants.MMDD_WITHSLASH_PATTERN.matcher(requestText).group(2));

            responseModel.setTargetDate(getTargetDate(month, day).format(RequestTextConstants.UUUUMMDD_FORMAT));
            return true;
        }

        // 日付形式チェック(MMdd)
        if (RequestTextConstants.MMDD_WITHSLASH_PATTERN.matcher(requestText).find()) {
            int month = Integer.parseInt(RequestTextConstants.MMDD_PATTERN.matcher(requestText).group(1));
            int day = Integer.parseInt(RequestTextConstants.MMDD_PATTERN.matcher(requestText).group(2));

            responseModel.setTargetDate(getTargetDate(month, day).format(RequestTextConstants.UUUUMMDD_FORMAT));
            return true;
        }

        // 日付形式チェック(m月n日)
        if (RequestTextConstants.MMDD_WITHSLASH_PATTERN.matcher(requestText).find()) {
            int month = Integer.parseInt(RequestTextConstants.MMDD_JP_PATTERN.matcher(requestText).group(1));
            int day = Integer.parseInt(RequestTextConstants.MMDD_JP_PATTERN.matcher(requestText).group(2));

            responseModel.setTargetDate(getTargetDate(month, day).format(RequestTextConstants.UUUUMMDD_FORMAT));
            return true;
        }

        // TODO 日付形式チェック 年指定の追加, 書式パターンの追加
        // 日付形式チェック(x年y月z日)
        // 日付形式チェック(uuuu/MM/dd)
        // 日付形式チェック(uuuuMMdd)

        return false;
    }

    private LocalDate getTargetDate(int month, int day) {
        // いったん現在日の年で生成
        LocalDate tmpDate = LocalDate.of(YearMonth.now().getYear(), month, day);

        // 生成した日付が現在より過去の場合、1年後の日付を返却
        if (tmpDate.isBefore(LocalDate.now())) {
            return tmpDate.plusYears(1);
        }

        // 現在と同じもしくは未来の場合、生成した日付で返却
        return tmpDate;

    }
}
