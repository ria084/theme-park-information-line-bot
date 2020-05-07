package com.ria084.themeparkinformation.linebot.util;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@AllArgsConstructor
public class UtilResources {

    public static String getOpeningHours(boolean isLand, boolean isSea) throws IOException {

        StringBuilder filePath = new StringBuilder().append("data/openinghours/");
        if (isSea) {
            filePath.append("SEA.json");
        } else {
            filePath.append("LAND.json");
        }

        String resource = new ClassPathResource(new String(filePath)).getFile().getPath();
        return Files.readString(Path.of(resource), StandardCharsets.UTF_8);

    }
}
