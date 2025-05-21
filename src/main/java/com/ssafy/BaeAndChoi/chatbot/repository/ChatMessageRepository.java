package com.ssafy.BaeAndChoi.chatbot.repository;

import com.ssafy.BaeAndChoi.chatbot.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByUserIdOrderByTimestampAsc(String userId);
}
