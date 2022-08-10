package com.imooc.bilibili.api;

import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件Api用于上传、删除文件
 *
 * @author huangqiang
 * @date 2022/4/14 22:47
 * @see
 * @since
 */
@RestController
public class FileApi {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 分片上传文件
     *
     * @param slice:分片文件, fileMD5:整个文件摘要, sliceNo:分片编号, totalSliceNo:总分片数
     * @return java.lang.String
     * @throws
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        // 返回格式 ：M00/00/00/rBEABGJZI3GAeeQOAA-itrfn0m4.tar.gz
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return new JsonResponse<>(filePath);
    }

}
