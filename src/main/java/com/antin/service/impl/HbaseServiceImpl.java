package com.antin.service.impl;

/**
 * Created by Administrator on 2017/6/20.
 */

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HbaseServiceImpl implements com.antin.service.HbaseService {

    private static final Logger log = Logger.getLogger(HbaseServiceImpl.class);
    private static int FETCH_HBASE_SIZE = 15000;
    @Autowired
    HbaseTemplate hbaseTemplate;

    /**
     * 通过表名和key获取一行数据
     *
     * @param tableName
     * @param rowKey
     * @return
     */
    @Override
    public Map<String, Object> get(String tableName, String rowKey) {
        return hbaseTemplate.get(tableName, rowKey, new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(Result result, int rowNum) throws Exception {
                List<Cell> ceList = result.listCells();
                Map<String, Object> map = new HashMap<String, Object>();
                if (ceList != null && ceList.size() > 0) {
                    for (Cell cell : ceList) {
                        map.put(Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength())
                                        + "_"
                                        + Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                                cell.getQualifierLength()),
                                Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 通过表名和key获取数据,key采取最前端字符匹配方式
     *
     * @param tableName
     * @param startRow
     * @param stopRow
     * @return
     */
    @Override
    public List<Map<String, Object>> find(String tableName, String startRow, String stopRow) {
        log.info("----------------------------------------------------------------------------------------------------------");
        log.info("hbaseTemplate.getConfiguration().iterator start-----------------------------------------------------------");
        Iterator<Map.Entry<String, String>> iterator = hbaseTemplate.getConfiguration().iterator();
        while (null != iterator && iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            log.info("key=" + entry.getKey() + ",value=" + entry.getValue());
        }
        log.info("hbaseTemplate.getConfiguration().iterator end  -----------------------------------------------------------");
        log.info("----------------------------------------------------------------------------------------------------------");

        if (startRow == null) {
            startRow = "";
        }
        if (stopRow == null) {
            stopRow = "";
        }
        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
        PageFilter filter = new PageFilter(5000);
        scan.setFilter(filter);
        return hbaseTemplate.find(tableName, scan, new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(Result result, int rowNum) throws Exception {
                List<Cell> ceList = result.listCells();
                Map<String, Object> map = new HashMap<String, Object>();
                String row = "";
                if (ceList != null && ceList.size() > 0) {
                    for (Cell cell : ceList) {
                        row = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                                cell.getValueLength());
                        // String family = Bytes.toString(cell.getFamilyArray(),
                        // cell.getFamilyOffset(),cell.getFamilyLength());
                        String quali = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                                cell.getQualifierLength());
                        // map.put(family + ":" + quali, value);
                        map.put(quali, value);
                    }
                    map.put("rowKey", row);
                }
                return map;
            }
        });
    }

//    public boolean batchExcuteInsert(final TableData tableData) {
//        return hbaseTemplate.execute(tableData.getTable(), new TableCallback<Boolean>() {
//            public Boolean doInTable(HTableInterface table) throws Throwable {
//                logger.info("into batchExcuteInsert");
//                // table.setAutoFlushTo(false);
//                // 缓存在服务器上/opt/hbase-1.1.2/conf/hbase-site.xml统一配置为10M，对所有HTable都生效，这里无须再设置
//                // table.setWriteBufferSize(10*1024*1024);//设置缓存到达10M才提交一次
//                boolean flag = false;
//                if (null != tableData && null != tableData.getRows() && 0 < tableData.getRows().size()) {
//                    List<Put> putList = new ArrayList<Put>();
//                    for (RowData row : tableData.getRows()) {
//                        if (null == row.getColumns() || 0 == row.getColumns().size())
//                            continue;
//                        Put put = new Put(row.getRowKey());
//                        for (TableDataProcessor.ColumnData column : row.getColumns()) {
//                            put.add(column.getFamily(), column.getQualifier(), column.getValue());
//                        }
//                        put.setDurability(Durability.SKIP_WAL);
//                        putList.add(put);
//                    }
//                    logger.info("batchExcuteInsert size=" + putList.size());
//                    table.put(putList);
//                    // table.flushCommits();
//                    flag = true;
//                }
//                logger.info("out batchExcuteInsert");
//                return flag;
//            }
//        });
//    }

    private String fillZero(String src, int length) {
        StringBuilder sb = new StringBuilder();
        if (src.length() < length) {
            for (int count = 0; count < (length - src.length()); count++) {
                sb.append("0");
            }
        }
        sb.append(src);
        return sb.toString();
    }

    /**
     * @param table
     * @param called
     * @param startTime
     * @param endTime
     * @param fromWeb   来自web查询为true，否则为false
     * @return
     */
    @Override
    public List<Map<String, Object>> querySignalList(String table, String called, String startTime, String endTime,
                                                     boolean fromWeb) {
        String tableName = table;
        String startRow = "";
        String stopRow = "";
        String timeFormat = fromWeb ? webQueryTimeFormat : interfaceTimeFormat;
        if (null == called || called.equals("")) {
            startRow = "";
            stopRow = "";
        } else {
            if (null == startTime || startTime.equals("")) {
                startRow = new StringBuffer(fillZero(called, 16)).reverse().toString();
            } else {
                String timeKey = fromTimeStr2TimeStr(timeFormat, startTime, hbaseTimeFormat_signal);
                startRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
            }
            if (null == endTime || endTime.equals("")) {
                String timeKey = date2Str(hbaseTimeFormat_signal, new Date());
                stopRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
            } else {
                String timeKey = fromTimeStr2TimeStr(timeFormat, endTime, hbaseTimeFormat_signal);
                stopRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
            }
        }
        return this.find(tableName, startRow, stopRow);
    }

    String hbaseTimeFormat_signal = "yyyyMMddHHmmssSSS";
    String hbaseTimeFormat_sms = "yyyyMMddHHmmss";
    String webQueryTimeFormat = "yyyy-MM-dd HH:mm:ss";
    String interfaceTimeFormat = "yyyyMMddHHmmss";

    private String date2Str(String timeFormatStr, Date date) {
        DateFormat sdf = new SimpleDateFormat(timeFormatStr);
        return sdf.format(date);
    }

    private Date str2Date(String timeFormatStr, String dateStr) {
        DateFormat sdf = new SimpleDateFormat(timeFormatStr);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private String fromTimeStr2TimeStr(String srcTimeFormat, String srcDate, String desTimeFormat) {
        return date2Str(desTimeFormat, str2Date(srcTimeFormat, srcDate));
    }

//    /**
//     * @param table     查询哪张表
//     * @param called    查询的被叫号码
//     * @param startTime 查询的起始时间
//     * @param endTime   查询的结束时间
//     * @param page      查询的分页信息
//     * @param fromWeb   是否来自管理端页面查询，管理端页面时间格式和接口中时间格式不同
//     * @return
//     */
//    public Page querySignalByPage(String table, String called, String startTime, String endTime, Page page,
//                                           boolean fromWeb) {
//        String tableName = table;
//        String startRow = "";
//        String stopRow = "";
//        String timeFormat = fromWeb ? webQueryTimeFormat : interfaceTimeFormat;
//        if (null == called || called.equals("")) {
//            startRow = "";
//            stopRow = "";
//        } else {
//            if (null == startTime || startTime.equals("")) {
//                startRow = new StringBuffer(fillZero(called, 16)).reverse().toString();
//            } else {
//                String timeKey = fromTimeStr2TimeStr(timeFormat, startTime, hbaseTimeFormat_signal);
//                startRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
//            }
//            if (null == endTime || endTime.equals("")) {
//                String timeKey = date2Str(hbaseTimeFormat_signal, new Date());
//                stopRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
//            } else {
//                String timeKey = fromTimeStr2TimeStr(timeFormat, endTime, hbaseTimeFormat_signal);
//                stopRow = new StringBuffer(fillZero(called, 16)).reverse().toString() + timeKey;
//            }
//        }
//        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
//        PageFilter filter = new PageFilter(FETCH_HBASE_SIZE);
//        scan.setFilter(filter);
//        PageRowMapper pageRowMapper = new PageRowMapper(page);
//        hbaseTemplate.find(tableName, scan, pageRowMapper);
//        if (null != pageRowMapper && pageRowMapper.getPage().getTotal() >= FETCH_HBASE_SIZE) {
//            PageFilter filter2 = new PageFilter(FETCH_HBASE_SIZE * 2);
//            scan.setFilter(filter2);
//            PageRowMapper pageRowMapper2 = new PageRowMapper(page);
//            hbaseTemplate.find(tableName, scan, pageRowMapper2);
//            return pageRowMapper2.getPage();
//        }
//        return pageRowMapper.getPage();
//    }

//    public Page querySmsSendResultByPage(String table, String sender, String startTime, String endTime, Page page,
//                                         boolean fromWeb) {
//        String tableName = table;
//        String startRow = "";
//        String stopRow = "";
//        String timeFormat = fromWeb ? webQueryTimeFormat : interfaceTimeFormat;
//        if (null == sender || sender.equals("")) {
//            startRow = "";
//            stopRow = "";
//        } else {
//            if (null == startTime || startTime.equals("")) {
//                startRow = new StringBuffer(fillZero(sender, 25)).reverse().toString();
//            } else {
//                String timeKey = fromTimeStr2TimeStr(timeFormat, startTime, hbaseTimeFormat_sms);
//                startRow = new StringBuffer(fillZero(sender, 25)).reverse().toString() + timeKey;
//            }
//            if (null == endTime || endTime.equals("")) {
//                String timeKey = date2Str(hbaseTimeFormat_sms, new Date());
//                stopRow = new StringBuffer(fillZero(sender, 25)).reverse().toString() + timeKey;
//            } else {
//                String timeKey = fromTimeStr2TimeStr(timeFormat, endTime, hbaseTimeFormat_sms);
//                stopRow = new StringBuffer(fillZero(sender, 25)).reverse().toString() + timeKey;
//            }
//        }
//        Scan scan = new Scan(Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
//        PageFilter filter = new PageFilter(10000);
//        scan.setFilter(filter);
//        PageRowMapper pageRowMapper = new PageRowMapper(page);
//        hbaseTemplate.find(tableName, scan, pageRowMapper);
//        System.out.println("------------------------------------------------------------");
//        System.out.println("tableName:" + tableName);
//        System.out.println("startRow:" + startRow);
//        System.out.println("stopRow:" + stopRow);
//        System.out.println("sssss:" + JSON.toJSONString(pageRowMapper.getPage()));
//        System.out.println("------------------------------------------------------------");
//        return pageRowMapper.getPage();
//    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<Map<String, Object>> filterByName(String table, String columnFamily, String column, String value) {
        log.info("Entering testSingleColumnValueFilter.");
        try {
            value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            log.info("==============> " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"));
        // Set the filter criteria.
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes(columnFamily), Bytes.toBytes(column), CompareFilter.CompareOp.EQUAL,
                Bytes.toBytes(value));
        scan.setFilter(filter);

        List<Map<String, Object>> list = hbaseTemplate.find(table, scan, new RowMapper<Map<String, Object>>() {
            public Map<String, Object> mapRow(Result result, int rowNum) throws Exception {

                List<Cell> ceList = result.listCells();
                Map<String, Object> map = new HashMap<String, Object>();
                String row = "";
                if (ceList != null && ceList.size() > 0) {
                    for (Cell cell : ceList) {
                        row = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                        String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(),
                                cell.getValueLength());
                        String quali = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(),
                                cell.getQualifierLength());
                        map.put(quali, value);
                    }
                    map.put("rowKey", row);
                }
                return map;
            }
        });

        //得到rowKey后再
        List<Map<String, Object>> resultList = new ArrayList<>();
        list.forEach(map -> {
            String rowKey = (String) map.get("rowKey");
            // Map<String, Object> resultMap = get(table, rowKey);
            //Map<String, Object> resultMap= new HashMap<String, Object>();
            Map<String, Object> resultMap = hbaseTemplate.get(table, rowKey, columnFamily, new RowMapper<Map<String, Object>>() {
                @Override
                public Map<String, Object> mapRow(Result result, int i) throws Exception {
                    List<Map<String, Object>> columnFamilyMap = new ArrayList<Map<String, Object>>();
                    for (Cell cell : result.rawCells()) {
                        Map<String, Object> cellMap = new HashMap<String, Object>();
                        cellMap.put("rowKey", Bytes.toString(CellUtil.cloneRow(cell)));
                        cellMap.put("columnFamily", Bytes.toString(CellUtil.cloneFamily(cell)));
                        cellMap.put("qualifier", Bytes.toString(CellUtil.cloneQualifier(cell)));
                        cellMap.put("value", Bytes.toString(CellUtil.cloneValue(cell)));
                        //resultMap
//                        log.info(Bytes.toString(CellUtil.cloneRow(cell)) + ":"
//                                + Bytes.toString(CellUtil.cloneFamily(cell)) + ","
//                                + Bytes.toString(CellUtil.cloneQualifier(cell)) + ","
//                                + Bytes.toString(CellUtil.cloneValue(cell)));
                        columnFamilyMap.add(cellMap);
                    }
                    Map<String, Object> rowMap = new HashMap<String, Object>();
                    //rowMap.put(Bytes.toString(result.getRow()), columnFamilyMap);
                    rowMap.put("row", columnFamilyMap);
                    return rowMap;
                }
            });
            resultList.add(resultMap);
        });

        return resultList;
    }
}