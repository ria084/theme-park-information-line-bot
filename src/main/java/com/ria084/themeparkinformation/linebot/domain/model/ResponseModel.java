package com.ria084.themeparkinformation.linebot.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * レスポンスにかかわる情報を格納するためのクラス
 */
@Component
@Data
public class ResponseModel {
    private Detail timeDetail;

    /**
     * 運営時間情報
     */
    @NoArgsConstructor
    @Component
    @Data
    public static class Detail {
        /**
         * 開園時間
         */
        @JsonProperty("openTime")
        private String openTime;

        /**
         * 閉園時間
         */
        @JsonProperty("closeTime")
        private String closeTime;

        /**
         * 備考
         */
        @JsonProperty("note")
        private String note;
    }
}
