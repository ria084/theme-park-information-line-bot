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
    private final RequestModel requestModel;

    public TextMessage generateResponse(String requestText) {
        boolean isLand;
        boolean isSea;

        // 必須要素が入っているか確認する(日付設定も含む)
        if (!validationRequest(requestText)) {
            // 指定がない場合は陸情報を返す
            isLand = true;
        }

        // 陸判定
        isLand = isContains(requestText, RequestTextConstants.LAND_KEY);

        // 海判定
        isSea = isContains(requestText, RequestTextConstants.SEA_KEY);

        String targetPark = "TDL";
        if (!isLand && isSea) {
            targetPark = "TDS";
        }

        // ファイル読み込み jsonをオブジェクト化
        Map<String, ResponseModel.Detail> response;
        try {
            String openingHours = UtilResources.getOpeningHours(isLand, isSea);
            ObjectMapper mapper = new ObjectMapper();

            response = mapper.readValue(openingHours, new TypeReference<HashMap<String, ResponseModel.Detail>>() {
            });
        } catch (IOException e) {
            log.error("設定ファイルの読み込みに失敗しました。 message: " + e.getMessage());
            return new TextMessage(RequestTextConstants.INNNER_ERROR);
        }

        ResponseModel.Detail timeDetail = response.get(requestModel.getTargetDate());

        if (timeDetail == null) {
            return new TextMessage(getStringTargetDate(requestModel.getTargetDate()) + "の運営情報はありません。別の日を指定してください");
        }

        // 備考欄が空なら通常通り開園/閉園時刻を通知
        if (timeDetail.getNote().isEmpty()) {
            return new TextMessage(String.format(RequestTextConstants.NORMAL_OPERATION, getStringTargetDate(requestModel.getTargetDate()), targetPark, timeDetail.getOpenTime(), timeDetail.getCloseTime()));
        }
        // 備考欄の記載があれば、注意事項を送付
        return new TextMessage(String.format(RequestTextConstants.SPECIAL_OPERATION, getStringTargetDate(requestModel.getTargetDate()), targetPark, timeDetail.getNote()));

    }

    /**
     * リクエストのテキスト内容のチェック・設定を行う
     * <p>
     * 対象日付をチェックし、取得、設定を行う
     *
     * @param requestText リクエストのテキスト内容
     * @return 日付設定がある場合はtrue, ない場合はfalse
     */
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
            throw new IllegalArgumentException("日付が正しくありません。MM/DD,MM月DD日どちらかの形式で指定してください。", e);
        }

        // ここに到達した場合は日付指定がないので、今日の日付を設定する
        requestModel.setTargetDate(LocalDate.now().format(RequestTextConstants.UUUUMMDD_FORMAT));

        return false;
    }

    /**
     * 引数をもとに、取得対象の日を算出する
     * <p>
     * 引数の月日が今年かつ未来なら今年の日付、
     * 今年かつ過去なら来年の日付を返す
     *
     * @param month 月
     * @param day   日
     * @return 取得対象日付
     */
    protected LocalDate getTargetDate(int month, int day) {
        // 一度現在日の年で生成
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

    /**
     * 第一引数の文字列に、第二引数のリストに定義された文字列が含まれるかチェックする
     *
     * @param requestText リクエストテキスト
     * @param keySet      キーワードになる文字列のリスト
     * @return リストの文字列が含まれていればtrue, なければfalse
     */
    private boolean isContains(String requestText, List<String> keySet) {
        // 海判定
        for (String text : keySet) {
            if (requestText.contains(text)) {
                return true;
            }
        }

        return false;
    }

}
