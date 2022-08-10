package com.imooc.bilibili.service;


import com.imooc.bilibili.dao.DFSFileDao;
import com.imooc.bilibili.domain.DFSFile;
import com.imooc.bilibili.service.util.FastDFSUtil;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * DFS文件service
 *
 * @author huangqiang
 * @date 2022/4/14 22:48
 * @see
 * @since
 */

@Service
public class FileService {

    @Autowired
    private DFSFileDao DFSFileDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    /**
     * 分片上传文件
     *
     * @param [slice:分片文件, fileMD5:整个文件摘要, sliceNo:分片编号, totalSliceNo:总分片数]
     * @return java.lang.String
     * @throws
     */
    public String uploadFileBySlices(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {

        // 根据文件数值摘要查找完整文件是否已经存在
        DFSFile dbDFSFileMD5 = DFSFileDao.getFileByMD5(fileMD5);
        if (dbDFSFileMD5 != null) {
            // 文件已存在直接返回
            return dbDFSFileMD5.getUrl();
        }
        // 分片上传文件
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);


        // 判断当前分片是否是最后一片
        if (!StringUtil.isNullOrEmpty(url)) {
            // 构建文件信息保存
            dbDFSFileMD5 = new DFSFile();
            dbDFSFileMD5.setCreateTime(new Date());
            dbDFSFileMD5.setMd5(fileMD5);
            dbDFSFileMD5.setUrl(url);
            dbDFSFileMD5.setType(fastDFSUtil.getFileType(slice));
            DFSFileDao.addFile(dbDFSFileMD5);
        }
        return url;
    }

}
