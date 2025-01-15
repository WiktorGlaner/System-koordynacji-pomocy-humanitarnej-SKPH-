package org.ioad.spring.communication.chatroom;

import org.ioad.spring.communication.models.ChatMessage;
import org.ioad.spring.communication.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
}
