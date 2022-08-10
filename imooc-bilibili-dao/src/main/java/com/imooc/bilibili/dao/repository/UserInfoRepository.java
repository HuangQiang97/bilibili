package com.imooc.bilibili.dao.repository;


import com.imooc.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 用户信息Repository，用于向es添加用户信息
 *
 * @author huangqiang
 * @date 2022/4/23 11:13
 * @see
 * @since
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}
