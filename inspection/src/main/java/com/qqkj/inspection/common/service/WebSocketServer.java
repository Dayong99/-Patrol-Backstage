package com.qqkj.inspection.common.service;
import com.qqkj.inspection.common.utils.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/ws/depot/{id}")
@Component
@Slf4j
public class WebSocketServer
{
    @PostConstruct
    public void init()
    {
    }

    private String id;
    private Session session;

    @OnOpen
    public void onOpen(Session session,@PathParam("id") String id) throws IOException
    {
        this.id=id;
        this.session = session;
        WebSocketUtils.addSession(this);
        if(id.equals("190"))
        {
            WebSocketUtils.SendMessage(session, "连接成功");

            WebSocketUtils.SendMessage("","190");
        }
        else
        {
            WebSocketUtils.SendMessage(session, "连接成功");
        }
    }

    @OnClose
    public void onClose(Session session,@PathParam("id") String id)
    {
        WebSocketUtils.removeSession(this);
    }

    @OnMessage
    public void onMessage(String message, Session session)
    {
        log.info("来自客户端的消息：{}",message);
    }

    @OnError
    public void onError(Session session, Throwable error)
    {
        log.error("WebSocket发生错误：{}，Session ID： {}",error.getMessage(),session.getId());
    }

    public String getId()
    {
        return id;
    }

    public Session getSession()
    {
        return session;
    }
}
