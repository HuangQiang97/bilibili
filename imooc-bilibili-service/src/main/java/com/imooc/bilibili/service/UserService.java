package com.imooc.bilibili.service;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.dao.UserDao;
import com.imooc.bilibili.domain.PageResult;
import com.imooc.bilibili.domain.RefreshTokenDetail;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.util.MD5Util;
import com.imooc.bilibili.service.util.RSAUtil;
import com.imooc.bilibili.service.util.TokenUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户相关的服务
 *
 * @author huangqiang
 * @date 2022/3/20 0:38
 * @see
 * @since
 */
@Service
public class UserService {
    @Autowired
    UserDao userDao;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    ElasticSearchService elasticSearchService;

    // 添加用户：phone、加密后密码
    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        // 判断手机号是否已经注册
        User dbUser = this.getUserByPhone(phone);
        if (dbUser != null) {
            throw new ConditionException("该手机号已经注册！");
        }
        // 生成盐
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        // 解密获得原始密码
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 解密后原始密码+盐生成签名
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        //添加用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_MALE);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
        //添加用户默认权限角色
        userAuthService.addUserDefaultRole(user.getId());
        //同步用户信息数据到es
        elasticSearchService.addUserInfo(userInfo);
    }

    // 根据手机号获取用户
    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }

    // 登录：手机后、加密后密码
    public String login(User user) throws Exception {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("手机号不能为空！");
        }
        // 手机号对应用户是否存在
        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }
        // 对接收到user密码解码
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 将解密后密码与盐构建签名
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        // 将签名与数据库中用户签名比较
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        return TokenUtil.generateToken(dbUser.getId());
    }

    // 根据userId获得用户信息
    public User getUserInfo(Long userId) {
        User user = userDao.getUserById(userId);
        UserInfo userInfo = userDao.getUserInfoByUserId(userId);
        user.setUserInfo(userInfo);
        return user;
    }

    // 更新用户信息
    public void updateUsers(User user) throws Exception {
        // 判空
        Long id = user.getId();
        User dbUser = userDao.getUserById(id);
        // 更新密码签名
        if (dbUser == null) {
            throw new ConditionException("用户不存在！");
        }
        if (!StringUtils.isNullOrEmpty(user.getPassword())) {
            String rawPassword = RSAUtil.decrypt(user.getPassword());
            String md5Password = MD5Util.sign(rawPassword, dbUser.getSalt(), "UTF-8");
            user.setPassword(md5Password);
        }
        // 设置更新时间
        user.setUpdateTime(new Date());
        // mybatis下判断user下各个字段，非空下才更新，防止数据丢失
        userDao.updateUsers(user);
    }

    // 更新用户信息
    public void updateUserInfos(UserInfo userInfo) {
        // 更新时间
        userInfo.setUpdateTime(new Date());
        // mybatis下判断userInfo下各个字段，非空下才更新，防止数据丢失
        userDao.updateUserInfos(userInfo);
        elasticSearchService.deleteUserInfo(userDao.getUserInfoIdByUserId(userInfo.getUserId()));
        elasticSearchService.addUserInfo(userInfo);
    }

    //    根据ID获得用户信息
    public User getUserById(Long followingId) {
        return userDao.getUserById(followingId);
    }

    //    根据ID集合获得全部用户信息
    public List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.getUserInfoByUserIds(userIdList);
    }

    // 分页模糊查询用户
    public PageResult<UserInfo> pageListUserInfos(JSONObject params) {
        Integer no = params.getInteger("no");
        Integer size = params.getInteger("size");
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        // 获得匹配用户数
        Integer total = userDao.pageCountUserInfos(params);
        List<UserInfo> list = new ArrayList<>();
        if (total > 0) {
            list = userDao.pageListUserInfos(params);
        }
        return new PageResult<>(total, list);
    }

    // 登录以获得access-token、refresh-token
    public Map<String, Object> loginForDts(User user) throws Exception {
        // 手机号判空
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("参数异常！");
        }
        // 查询用户是否存在
        User dbUser = userDao.getUserByPhone(phone);
        if (dbUser == null) {
            throw new ConditionException("当前用户不存在！");
        }
        // 解密获得原始密码
        String password = user.getPassword();
        String rawPassword;
        try {
            rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("密码解密失败！");
        }
        // 解密密码和盐生成签名与数据库中用户比对
        String salt = dbUser.getSalt();
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        if (!md5Password.equals(dbUser.getPassword())) {
            throw new ConditionException("密码错误！");
        }
        // 生成access-token、refresh-token
        Long userId = dbUser.getId();
        String accessToken = TokenUtil.generateToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        //保存refresh token到数据库
        userDao.deleteRefreshToken(refreshToken, userId);
        userDao.addRefreshToken(refreshToken, userId, new Date());
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        return result;
    }

    // 登出，客户端删除access-token、refresh-token，服务器端删除refresh-token
    public void logout(String refreshToken, Long userId) {
        userDao.deleteRefreshToken(refreshToken, userId);
    }

    // 当access-token过期后，依据用户的refresh-token重新生成access-token
    public String refreshAccessToken(String refreshToken) throws Exception {
        // 验证refresh-token是否过期
        RefreshTokenDetail refreshTokenDetail = userDao.getRefreshTokenDetail(refreshToken);
        if (refreshTokenDetail == null) {
            throw new ConditionException("555", "token过期！");
        }
        // 依据用户的refresh-token重新生成access-token
        Long userId = refreshTokenDetail.getUserId();
        return TokenUtil.generateToken(userId);
    }

    /**
     * 根据用户ID信息集合，批量查询用户信息
     *
     * @param userIdList
     * @return
     */
    public List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList) {
        return userDao.batchGetUserInfoByUserIds(userIdList);
    }
}