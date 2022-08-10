package com.imooc.bilibili.api;


import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.JsonResponse;
import com.imooc.bilibili.domain.PageResult;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.service.UserFollowingService;
import com.imooc.bilibili.service.UserService;
import com.imooc.bilibili.service.util.RSAUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 与用户相关的请求处理
 *
 * @author huangqiang
 * @date 2022/3/20 0:31
 * @see
 * @since
 */
@Api
@RestController
public class UserApi {

    @Autowired
    UserService userService;

    @Autowired
    UserSupport userSupport;

    @Autowired
    private UserFollowingService userFollowingService;

    // 根据用户传过来的token获取到userId，再获取到用户信息
    @ApiOperation("登陆后根据token获取用户信息")
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    // 提供公钥
    @ApiOperation("提供公钥")
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPulicKey() {
        String pk = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(pk);
    }

    // 添加用户：phone，加密后密码
    @ApiOperation("添加用户")
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    // 用户登录：phone，加密后密码
    @ApiOperation("用户登录")
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }


    // 更新用户
    @ApiOperation("更新用户基本信息")
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    // 更新用户信息
    @ApiOperation("更新用户个性化信息")
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        return JsonResponse.success();
    }

    /**
     * 分页查询用户
     *
     * @param [no:页数, size:每页大小, nick:被查询用户昵称（非必须）]
     * @return com.imooc.bilibili.domain.JsonResponse<com.imooc.bilibili.domain.PageResult < com.imooc.bilibili.domain.UserInfo>>
     * @throws
     */
    @ApiOperation("分页查询用户")
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {

        Long userId = userSupport.getCurrentUserId();
        JSONObject params = new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        // 分页模糊查询
        PageResult<UserInfo> result = userService.pageListUserInfos(params);
        // 检查当前用户是否关注查询到的用户
        if (result.getTotal() > 0) {
            List<UserInfo> checkedUserInfoList = userFollowingService.checkFollowingStatus(result.getList(), userId);
            result.setList(checkedUserInfoList);
        }
        return new JsonResponse<>(result);
    }

    // 登录以获得access-token、refresh-token
    @ApiOperation("添加用户，返回双token")
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    // 登出，客户端删除access-token、refresh-token，服务器端删除refresh-token
    @ApiOperation("登录退出")
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request) {
        // 从请求头中获得refresh-token
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserId();
        // 服务器端删除refresh-token
        userService.logout(refreshToken, userId);
        return JsonResponse.success();
    }

    // 当access-token过期后，依据用户的refresh-token重新生成access-token
    @ApiOperation("根据refresh-token刷新access-token")
    @PostMapping("/access-tokens")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        // 从请求头中获得refresh-token
        String refreshToken = request.getHeader("refreshToken");
        // 依据用户的refresh-token重新生成access-token
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new JsonResponse<>(accessToken);
    }
}