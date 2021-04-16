package com.qqkj.inspection.common.utils;

import com.qqkj.inspection.common.service.WebSocketServer;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebSocketUtils
{
    private static final AtomicInteger onlineCount = new AtomicInteger(0);
    private static CopyOnWriteArraySet<WebSocketServer> sessionSet = new CopyOnWriteArraySet<WebSocketServer>();

    public static void addSession(WebSocketServer webSocketServer)
    {
        sessionSet.add(webSocketServer);
        int cnt = onlineCount.incrementAndGet();
        log.info("有连接加入，当前连接数为：{}", cnt);
    }

    public static void removeSession(WebSocketServer webSocketServer)
    {
        sessionSet.remove(webSocketServer);
        int cnt = onlineCount.decrementAndGet();
        log.info("有连接关闭，当前连接数为：{}", cnt);
    }

    public static CopyOnWriteArraySet<WebSocketServer> getSessions()
    {
        return sessionSet;
    }

    public static void SendMessage(Session session, String message)
    {
        try
        {
            session.getBasicRemote().sendText(message);
        }
        catch (IOException e)
        {
            log.error("发送消息出错：{}", e.getMessage());
        }
    }

    public static void BroadCastInfo(String message) throws IOException
    {
        for (WebSocketServer webSocketServer : WebSocketUtils.getSessions())
        {
            if(webSocketServer.getSession().isOpen())
            {
                SendMessage(webSocketServer.getSession(), message);
            }
        }
    }

    public static void SendMessage(String message, String id) throws IOException
    {
        Session session = null;
        for (WebSocketServer webSocketServer : WebSocketUtils.getSessions())
        {
            if (webSocketServer.getId().equals(id))
            {
                session = webSocketServer.getSession();
                break;
            }
        }
        if (session != null)
        {
            SendMessage(session, message);
        }
        else
        {
            log.warn("没有找到你指定ID的会话：{}", id);
        }
    }
}
