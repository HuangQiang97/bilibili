package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 弹幕dao，保存、获取弹幕
 *
 * @author huangqiang
 * @date 2022/4/20 22:29
 * @see
 * @since
 */

@Mapper
public interface DanmuDao {

    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> params);
}
