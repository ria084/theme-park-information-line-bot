package com.ria084.themeparkinformation.linebot.domain.model;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * レスポンスにかかわる情報を格納するためのクラス
 */
@Component
@Data
public class ResponseModel {

    private String targetDate;

    private Detail openingHoursDetail;

    @Component
    @Data
    public class Detail {
        private String openTime;

        private String closeTime;

        private String note;
    }
}
