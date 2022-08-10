package com.imooc.bilibili.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 用户硬币账户操作dao
 *
 * @author huangqiang
 * @date 2022/4/15 19:54
 * @see
 * @since
 */
@Mapper
public interface UserCoinDao {

    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoinAmount(@Param("userId") Long userId,
                                 @Param("amount") Integer amount,
                                 @Param("updateTime") Date updateTime);
}
