package com.word.game.word_game.handler;

import com.word.game.word_game.constants.InitialLetter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

import static com.word.game.word_game.utility.CheckLogic.checkLetter;

@Component
@Log4j2
public class ChatHandler extends TextWebSocketHandler {
    private static List<WebSocketSession> list = new ArrayList<>();
    private static InitialLetter initialLetter = new InitialLetter();
    private static String letter= initialLetter.getLetter();
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        payload = payload.substring(payload.lastIndexOf(':') + 1);
        log.info("payload : " + payload);
        log.info("letter : " + letter);

        if(checkLetter(payload, letter)){
            log.info("좋아 좋아 정답");
        }
        else{
            log.info("틀림");
        }

        for(WebSocketSession webSocketSession : list){
            webSocketSession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
        list.add(session);
        log.info("클라이언트 "+session);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        log.info(session + " 접속 해제");
        list.remove(session);
    }

}
