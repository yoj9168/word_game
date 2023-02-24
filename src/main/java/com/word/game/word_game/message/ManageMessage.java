package com.word.game.word_game.message;

import com.word.game.word_game.constants.InitialLetter;
import com.word.game.word_game.handler.ChatHandler;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.word.game.word_game.utility.CheckLogic.checkLetter;
@Log4j2
public class ManageMessage {
    private static List<WebSocketSession> list = new ArrayList<>();
    private static InitialLetter initialLetter = new InitialLetter();
    private static String letter = initialLetter.getLetter();
    private static TextMessage correct = new TextMessage("correct");
    private static TextMessage wrong = new TextMessage("wrong");
    private static TextMessage letterMessage = new TextMessage(letter);
    private static int idx = 1;

    //client에게 값이 왔을 떄
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception{
        String payload = message.getPayload();
        payload = payload.substring(payload.lastIndexOf(':') + 1);
        //받은 메시지를 브로드캐스트로 모든 클라이언트에게 전송
        for(WebSocketSession webSocketSession : list){
            webSocketSession.sendMessage(message);
            webSocketSession.sendMessage(letterMessage);
        }
        log.info("payload : " + payload);
        log.info("letter : " + letter);
        //보낸 메시지와 랜덤으로 받아온 초성인지 판단
        if(checkLetter(payload, letter)){
            log.info("정답");
            for(WebSocketSession webSocketSession : list) {
                webSocketSession.sendMessage(correct);
            }
        }
        else{
            log.info("틀림");
            for(WebSocketSession webSocketSession : list) {
                webSocketSession.sendMessage(wrong);
            }
        }
    }
    //client와 서버가 connection되자마자
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage("ID:user"+idx));
        idx++;
        if (list.size() > 2) {
            session.sendMessage(new TextMessage("error"));
            session.close();
        }
        else {
            list.add(session);
            Timer timer = new Timer();
            final int[] second = {5};
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        session.sendMessage(new TextMessage(String.valueOf(second[0])));
                        second[0]--;
                        if(second[0] <= 0){
                            session.sendMessage(new TextMessage("time_finish"));
                            letter = initialLetter.getLetter();
                            log.info(letter);
                            for(WebSocketSession webSocketSession : list){
                                webSocketSession.sendMessage(new TextMessage(letter));
                            }
                            second[0] = 5;
                        }
                    } catch (IOException e) {
                        timer.cancel();
                    }
                }
            }, 1000, 1000);
        }
    }
    //client와 server가 connection이 끊겼을 때
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
        log.info(session + " 접속 해제");
        list.remove(session);
    }

}
