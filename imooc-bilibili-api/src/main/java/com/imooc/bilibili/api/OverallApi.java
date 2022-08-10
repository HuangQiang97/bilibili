package com.imooc.bilibili.api;

import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 根据关键字分页查询视频和用户
 *
 * @author huangqiang
 * @date 2022/4/23 11:38
 * @see
 * @since
 */
@RestController
public class OverallApi {

    @Autowired
    ElasticSearchService elasticSearchService;

    /**
     * 根据关键字分页查询视频和用户
     *
     * @param keyword:关键字
     * @param pageNo:分码
     * @param pageSize:每页大小
     * @return
     * @throws IOException
     */
    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> search(@RequestParam String keyword, @RequestParam Integer pageNo, @RequestParam Integer pageSize) throws IOException {
        return new JsonResponse<>(elasticSearchService.getContents(keyword, pageNo, pageSize));
    }
}