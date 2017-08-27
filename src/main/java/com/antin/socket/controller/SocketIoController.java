package com.antin.socket.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by win7 on 2017/8/27.
 */
@Controller
public class SocketIoController {
    @Autowired
    private SocketIoService service;

    //启动socket 服务
    @RequestMapping("startServer")
    public void startServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Map params = ReflectUtil.transToMAP(request.getParameterMap());
        try {
            if (service.getServer() == null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            service.startServer();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //停止socket服务
    @RequestMapping("stopServer")
    public void stopServer(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Map params = ReflectUtil.transToMAP(request.getParameterMap());
        try {
            if (service.getServer() == null) {
                service.stopServer();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //给指定的客户端推送消息
    @RequestMapping("sendAdvertInfoMsg")
    public void sendAdvertInfoMsg(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //Map params = ReflectUtil.transToMAP(request.getParameterMap());
        //String uuid = ParamsUtil.nullDeal(params, "uuid", "");
        String uuid = request.getParameter("uuid");
        try {
            if (!"".equals(uuid) && service.getServer() != null) {
                service.sendMessageToOneClient(uuid, "advert_info", "推送的内容");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}