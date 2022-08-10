package com.imooc.bilibili.dao;


import com.imooc.bilibili.domain.DFSFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * DFSFile 数据库操作
 *
 * @author huangqiang
 * @date 2022/4/14 22:49
 * @see
 * @since
 */
@Mapper
public interface DFSFileDao {

    Integer addFile(DFSFile DFSFile);

    // 根据文件摘要查找文件
    DFSFile getFileByMD5(String md5);
}
