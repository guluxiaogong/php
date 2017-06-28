package com.antin.service;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/20.
 */
public interface HbaseService {

    Map<String, Object> get(String tableName, String rowKey);

    List<Map<String, Object>> find(String tableName, String startRow, String stopRow);

    List<Map<String, Object>> querySignalList(String table, String called, String startTime, String endTime,
                                              boolean fromWeb);

    List<Map<String, Object>> filterByName(String table, String columnFamily, String column, String value);
}
