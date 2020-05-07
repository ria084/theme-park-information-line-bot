package com.ria084.themeparkinformation.linebot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@AllArgsConstructor
@Slf4j
public class UtilResources {

    public static String getOpeningHours(boolean isLand, boolean isSea) throws IOException {

        StringBuilder filePath = new StringBuilder().append("data/openinghours/");

        if (isLand) {
            filePath.append("LAND.json");
        } else if (isSea) {
            filePath.append("SEA.json");
        } else {
            log.info("パークの指定がありません。陸の情報を返却します");
            filePath.append("LAND.json");
        }

        String resource = new ClassPathResource(new String(filePath)).getFile().getPath();
        return Files.readString(Path.of(resource), StandardCharsets.UTF_8);

    }
}
