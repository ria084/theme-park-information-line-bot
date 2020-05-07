package com.ria084.themeparkinformation.linebot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
@AllArgsConstructor
@Slf4j
public class UtilResources {

    public static String getOpeningHours(boolean isLand, boolean isSea) throws IOException {

        StringBuilder filePath = new StringBuilder().append("classpath:data/openinghours/");

        if (isLand) {
            filePath.append("LAND.json");
        } else if (isSea) {
            filePath.append("SEA.json");
        } else {
            log.info("パークの指定がありません。陸の情報を返却します");
            filePath.append("LAND.json");
        }

        return Files.readString(ResourceUtils.getFile(filePath.toString()).toPath(), StandardCharsets.UTF_8);

    }
}
