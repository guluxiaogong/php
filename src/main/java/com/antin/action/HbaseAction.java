package com.antin.action;

import com.antin.service.HbaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/20.
 */
@Controller
@RequestMapping("/hbase")
public class HbaseAction {

    private static final Logger log = LoggerFactory.getLogger(HbaseAction.class);

    @Autowired
    private HbaseService hbaseService;

    @ResponseBody
    @RequestMapping("get")
    public Map<String, Object> get() {
        Map<String, Object> map = hbaseService.get("jcj_test", "rk0001");
        map.forEach((k, v) -> {
            System.out.println(k + " : " + v);
        });
        return map;
    }

    @ResponseBody
    @RequestMapping("find")
    public List<Map<String, Object>> find() {
        List<Map<String, Object>> list = hbaseService.find("jcj_test", "rk0001", "rk0003");
        list.forEach(map -> {
            map.forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
        });
        return list;
    }

    @ResponseBody
    @RequestMapping("querySignalList")
    public List<Map<String, Object>> querySignalList() {
        List<Map<String, Object>> list = hbaseService.querySignalList("jcj_test", "", "2017-01-20", "2017-06-20", true);
        list.forEach(map -> {
            map.forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
        });
        return list;
    }

    /**
     * 根据info列族中name列过滤
     *
     * @param value
     * @return
     */
    @ResponseBody
    @RequestMapping("filterByName")
    public List<Map<String, Object>> filterByName(String value) {
        List<Map<String, Object>> list = hbaseService.filterByName("bd:php_web_demo", "info", "name", value);
        list.forEach(map -> {
            map.forEach((k, v) -> {
                System.out.println(k + " : " + v);
            });
        });
        return list;
    }

}
