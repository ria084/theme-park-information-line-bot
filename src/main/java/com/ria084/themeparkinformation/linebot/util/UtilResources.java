package com.ria084.themeparkinformation.linebot.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@AllArgsConstructor
@Slf4j
public class UtilResources {

    private final ResourceLoader resourceLoader;

    public String getOpeningHours(boolean isLand, boolean isSea) throws IOException {
        StringBuilder filePath = new StringBuilder().append("data/openinghours/");

        if (isLand) {
            filePath.append("LAND.json");
        } else if (isSea) {
            filePath.append("SEA.json");
        } else {
            log.info("パークの指定がありません。陸の情報を返却します");
            filePath.append("LAND.json");
        }

        Resource resource = resourceLoader.getResource("classpath:" + filePath.toString());
        log.info("リソース取得");
        File file = resource.getFile();
        log.info("filepath:" + file.getPath());
        return new String(new FileInputStream(file).readAllBytes(), StandardCharsets.UTF_8);
    }
}
