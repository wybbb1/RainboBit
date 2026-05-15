package com.wybbb.rainbobit.service;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        streamingChatModel = "openAiStreamingChatModel"
)
public interface Assistant extends ChatMemoryAccess {

    String chat(String userMessage);

    TokenStream tokenStreamChat(String userMessage);

    Flux<String> fluxStreamChat(String userMessage);
}
