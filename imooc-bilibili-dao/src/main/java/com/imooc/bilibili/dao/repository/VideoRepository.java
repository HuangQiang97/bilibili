package com.imooc.bilibili.dao.repository;


import com.imooc.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 视频Repository，用于向es添加视频信息
 *
 * @author huangqiang
 * @date 2022/4/23 11:14
 * @see
 * @since
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {

    Video findByTitleLike(String keyword);
}
