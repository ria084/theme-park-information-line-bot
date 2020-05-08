package com.ria084.themeparkinformation.linebot.service

import com.linecorp.bot.model.message.TextMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.time.LocalDate

@SpringBootTest
class ThemeParkInformationResponseServiceTest extends spock.lang.Specification {

    @Autowired
    private ThemeParkInformationResponseService service

    def "GenerateResponse_today"() {
        setup:
        String requestText = "今日の陸"
        LocalDate expectDate = LocalDate.now()
        String expectDateText = String.format("%d年%d月%d日", expectDate.getYear(), expectDate.getMonthValue(), expectDate.getDayOfMonth())

        when:
        TextMessage response = service.generateResponse(requestText)

        then:
        response.text.contains(expectDateText)
    }

    def "generateResponse_tomorrow"() {
        setup:
        String requestText = "明日の海"
        LocalDate expectDate = LocalDate.now().plusDays(1)
        String expectDateText = String.format("%d年%d月%d日", expectDate.getYear(), expectDate.getMonthValue(), expectDate.getDayOfMonth())

        when:
        TextMessage response = service.generateResponse(requestText)

        then:
        response.text.contains(expectDateText)
    }

    def "generateResponse_after_tomorrow"() {
        setup:
        String requestText = "あさっての海"
        LocalDate expectDate = LocalDate.now().plusDays(2)
        String expectDateText = String.format("%d年%d月%d日", expectDate.getYear(), expectDate.getMonthValue(), expectDate.getDayOfMonth())

        when:
        TextMessage response = service.generateResponse(requestText)

        then:
        response.text.contains(expectDateText)
    }

    def "generateResponse_MMDD_with_slash"() {
        setup:
        String requestText = "2/15の陸"
        LocalDate expectDate = service.getTargetDate(2, 5)
        String expectDateText = String.format("%d年%d月%d日", expectDate.getYear(), expectDate.getMonthValue(), expectDate.getDayOfMonth())

        when:
        TextMessage response = service.generateResponse(requestText)

        then:
        response.text.contains(expectDateText)
    }

    def "generateResponse_MM月DD日"() {
        setup:
        String requestText = "2月4日の陸"
        LocalDate expectDate = service.getTargetDate(2, 4)
        String expectDateText = String.format("%d年%d月%d日", expectDate.getYear(), expectDate.getMonthValue(), expectDate.getDayOfMonth())

        when:
        TextMessage response = service.generateResponse(requestText)

        then:
        response.text.contains(expectDateText)
    }
}
