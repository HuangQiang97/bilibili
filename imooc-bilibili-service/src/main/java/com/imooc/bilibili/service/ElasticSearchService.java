package com.imooc.bilibili.service;


import com.imooc.bilibili.dao.repository.UserInfoRepository;
import com.imooc.bilibili.dao.repository.VideoRepository;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * es服务，添加信息和查找信息
 *
 * @author huangqiang
 * @date 2022/4/23 11:09
 * @see
 * @since
 */
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 向es添加用户信息，用于后续用户搜索
     *
     * @param userInfo
     */
    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    /**
     * 从es删除用户信息
     *
     * @param id
     */
    public void deleteUserInfo(Long id) {
        userInfoRepository.deleteById(id);
    }

    /**
     * 向es添加视频信息，用于后续视频搜索
     *
     * @param video
     */
    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    /**
     * 根据关键词，分页查询视频和用户
     *
     * @param keyword
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> getContents(String keyword,
                                                 Integer pageNo,
                                                 Integer pageSize) throws IOException {
        // 要查询的索引，类似于mysql 中的表，要与Video,UserInfo上定义的索引对应
        String[] indices = {"videos", "user-infos"};
        SearchRequest searchRequest = new SearchRequest(indices);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //分页
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        // 要查询的字段，用于在指定的索引中查询字段，此处查询的字段为用户昵称、视频标题、视频简介
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        // 查询结果
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            arrayList.add(sourceMap);
        }
        return arrayList;
    }

}
