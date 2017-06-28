package com.antin.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Administrator on 2017/6/21.
 */
@Controller
public class IndexAction {
    private static final Logger log = LoggerFactory.getLogger(IndexAction.class);

    @RequestMapping("/index")
    public String index() {
        log.info("request index page success...");
        return "/html/index";
    }

    @RequestMapping("/view")
    public String home() {
        log.info("request index page success...");
        return "/view/index";
    }
}

