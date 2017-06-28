package com.antin.util;

/**
 * Created by Administrator on 2017/6/20.
 */

import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileSystemUtil {
    private static FileSystem fs = (FileSystem) SpringContextHolder.getBean("hadoop-cluster");

    public void mkdirs() throws Exception { // create HDFS folder 创建一个文件夹
        Path path = new Path("/test");
        fs.mkdirs(path);
    }

    public void create() throws Exception { // create a file 创建一个文件
        Path path = new Path("/test/a.txt");
        FSDataOutputStream out = fs.create(path);
        out.write("hello hadoop".getBytes());
    }

    public void rename() throws Exception { // rename a file 重命名
        Path path = new Path("/test/a.txt");
        Path newPath = new Path("/test/b.txt");
        System.out.println(fs.rename(path, newPath));
    }

    public void copyFromLocalFile() throws Exception { // upload a local file
        // 上传文件
        Path src = new Path("/home/hadoop/hadoop-1.2.1/bin/rcc");
        Path dst = new Path("/test");
        fs.copyFromLocalFile(src, dst);
    }

    // upload a local file
    // 上传文件
    public void uploadLocalFile2() throws Exception {
        Path src = new Path("/home/hadoop/hadoop-1.2.1/bin/rcc");
        Path dst = new Path("/test");
        InputStream in = new BufferedInputStream(new FileInputStream(new File(
                "/home/hadoop/hadoop-1.2.1/bin/rcc")));
        FSDataOutputStream out = fs.create(new Path("/test/rcc1"));
        IOUtils.copyBytes(in, out, 4096);
    }

    public void listFiles() throws Exception { // list files under folder
        // 列出文件
        Path dst = new Path("/test");
        FileStatus[] files = fs.listStatus(dst);
        for (FileStatus file : files) {
            System.out.println(file.getPath().toString());
        }
    }

    public void getBlockInfo() throws Exception { // list block info of file
        // 查找文件所在的数据块
        Path dst = new Path("/test/rcc");
        FileStatus fileStatus = fs.getFileStatus(dst);
        BlockLocation[] blkloc = fs.getFileBlockLocations(fileStatus, 0,
                fileStatus.getLen()); // 查找文件所在数据块
        for (BlockLocation loc : blkloc) {
            for (int i = 0; i < loc.getHosts().length; i++)
                System.out.println(loc.getHosts()[i]);
        }
    }
}