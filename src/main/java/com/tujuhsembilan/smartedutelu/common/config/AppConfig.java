package com.tujuhsembilan.smartedutelu.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua_parser.Parser;

@Configuration
public class AppConfig {

    /**
     * J13: Singleton Parser dari uap-java untuk proper User-Agent parsing.
     * Parser memuat bundled regexes.yaml saat pertama kali diinisialisasi.
     */
    @Bean
    public Parser uaParser() {
        return new Parser();
    }
}
