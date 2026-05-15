package com.wybbb.rainbobit.common.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rainbobit.llm")
@Data
public class AIProperties {
    private String apiKey;
    private String model;
}
