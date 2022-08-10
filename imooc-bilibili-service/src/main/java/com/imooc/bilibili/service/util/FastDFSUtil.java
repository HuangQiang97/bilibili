package com.imooc.bilibili.service.util;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.imooc.bilibili.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.imooc.bilibili.service.constant.FastDFSConstant.*;

/**
 * FastDFS相关文件操作工具类
 *
 * @author huangqiang
 * @date 2022/4/14 21:53
 * @see
 * @since
 */

@Component
public class FastDFSUtil {

    // 面向普通应用的文件操作接口封装
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    // 支持断点续传的文件服务接口,适合处理大文件，分段传输
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    // nginx路径
    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    // 获得文件类型
    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("文件不能为空！");
        }
        // Return the original filename in the client's filesystem
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    //普通文件上传
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    //上传可以断点续传的文件时的第一个分片，会创建一个新的文件，并访问路径
    // 需要指定所在分组，以便后续负载均衡的处理
    private String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    //上传可以断点续传的文件时的后续分片，根据传入文件分组、路径和偏移量，直接在原始文件的后面添加

    private void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }


    /**
     * 保存分片文件，在所有分片上传完成后返回路径
     *
     * @param file:分片文件, fileMD5:整个文件摘要, sliceNo:分片编号, totalSliceNo:总分片数]
     * @return java.lang.String
     * @throws
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {

        if (file == null || sliceNo == null || totalSliceNo == null) {
            throw new ConditionException("参数异常！");
        }
        // 保存分片文件上传信息，用户后续分片文件的上传
        // 初始分片路径
        String pathKey = PATH_KEY + fileMd5;
        // 已上传大小
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        // 已上传分片数
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
        // 已上传大小，初始分片为0
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if (!StringUtil.isNullOrEmpty(uploadedSizeStr)) {
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }
        //上传的是第一个分片，需要在存储服务器上创建文件
        if (sliceNo == 1) {
            String path = this.uploadAppenderFile(file);
            if (StringUtil.isNullOrEmpty(path)) {
                throw new ConditionException("上传失败！");
            }
            // 保存文件路径
            redisTemplate.opsForValue().set(pathKey, path);
            // 保存已上传分片数
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        } else {
            // 上传的是后续分片，直接存储服务器上文件后面添加数据
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) {
                throw new ConditionException("上传失败！");
            }
            this.modifyAppenderFile(file, filePath, uploadedSize);
            // 保存已上传分片数
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        // 修改历史上传分片文件大小
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));

        //如果所有分片全部上传完毕，则清空redis里面不再用到的相关的key和value
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath = "";
        if (uploadedNo.equals(totalSliceNo)) {
            resultPath = redisTemplate.opsForValue().get(pathKey);
            // 删除所有key
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            redisTemplate.delete(keyList);
        }
        // 所有分片上传完成后返回路径，否则返回空字符串
        return resultPath;
    }


    //删除文件
    public void deleteFile(String filePath) {
        fastFileStorageClient.deleteFile(filePath);
    }

    /**
     * 分片返回视频比特流数据
     *
     * @param request, response, path
     * @return void
     * @throws
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        // 获得文件基本信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        // path为相对路径，格式为: M00/00/00/rBEABGJZI3GAeeQOAA-itrfn0m4.tar.gz
        // 需要补全存储服务器的ip,端口,以及文件所在group，最终获得完整地址
        String url = httpFdfsStorageAddr + path;
        //获取请求头信息
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        // 获得请求的比特范围
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtil.isNullOrEmpty(rangeStr)) {
            // 未指定直接返回完整比特
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        // [,start],[,start,end]
        // 获取起始比特
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if (range.length >= 2) {
            begin = Long.parseLong(range[1]);
        }
        // 获取终止比特
        long end = totalFileSize - 1;
        if (range.length >= 3) {
            end = Long.parseLong(range[2]);
        }
        long len = (end - begin) + 1;
        // 分片传输时设置响应头
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int) len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        // 读取文件并写入http响应
        HttpUtil.get(url, headers, response);
    }
}
