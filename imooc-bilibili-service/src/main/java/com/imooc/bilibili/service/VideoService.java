package com.imooc.bilibili.service;


import com.imooc.bilibili.dao.VideoDao;
import com.imooc.bilibili.domain.*;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 视频service
 *
 * @author huangqiang
 * @date 2022/4/15 15:21
 * @see
 * @since
 */
@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UserCoinService userCoinService;

    @Autowired
    ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    // 上传视频
    @Transactional
    public void addVideos(Video video) {
        // 保存视频，回填视频ID
        Date now = new Date();
        video.setCreateTime(new Date());
        videoDao.addVideos(video);
        // 保存视频标签信息
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoDao.batchAddVideoTags(tagList);

        //在es中添加一条视频数据
        elasticSearchService.addVideo(video);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null) {
            throw new ConditionException("参数异常！");
        }
        // 分页查询信息
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        List<Video> list = new ArrayList<>();
        // 查询复合条件的视频数
        Integer total = videoDao.pageCountVideos(params);
        if (total > 0) {
            // 根据调差分页查询用户
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total, list);
    }

    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }

    // 添加点赞记录
    public void addVideoLike(Long videoId, Long userId) {
        // 视频ID验证
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        // 该用户是否已经点赞过该视频
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        if (videoLike != null) {
            throw new ConditionException("已经赞过！");
        }
        // 添加点赞记录
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    public void deleteVideoLike(Long videoId, Long userId) {
        videoDao.deleteVideoLike(videoId, userId);
    }

    // 查询视频被点赞次数
    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        // 查询点赞数
        Long count = videoDao.getVideoLikes(videoId);
        // 查询该用户是否点赞过该视频
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(videoId, userId);
        boolean like = videoLike != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    // 查询该用户点赞过的视频
    public List<Video> getLikesVideo(Long userId) {
        List<Video> ans = videoDao.getLikesVideo(userId);
        return ans;
    }

    // 收藏视频
    @Transactional
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        // 参数校验
        Long videoId = videoCollection.getVideoId();
        Long groupId = videoCollection.getGroupId();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }
        // 验证视频是否合法
        Video video = videoDao.getVideoById(videoId);
        if (video == null || groupId == null) {
            throw new ConditionException("非法视频！");
        }

        //删除原有视频收藏
        videoDao.deleteVideoCollection(videoId, userId);
        //添加新的视频收藏
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        videoDao.addVideoCollection(videoCollection);
    }

    // 取消收藏
    public void deleteVideoCollection(Long videoId, Long userId) {
        videoDao.deleteVideoCollection(videoId, userId);
    }

    // 查询视频收藏数
    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        // 查询视频收藏数
        Long count = videoDao.getVideoCollections(videoId);
        // 查询当前用户是否已经收藏过该视频
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean collect = videoCollection != null;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("collect", collect);
        return result;
    }

    // 查询收藏过视频
    public List<VideoCollection> getCollectedVideo(Long userId) {
        return videoDao.getCollectedVideo(userId);
    }

    // 视频投币
    @Transactional
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        // 参数检验
        Long videoId = videoCoin.getVideoId();
        Integer amount = videoCoin.getAmount();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        //查询当前登录用户是否拥有足够的硬币
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        if (amount > userCoinsAmount) {
            throw new ConditionException("硬币数量不足！");
        }
        //查询当前登录用户对该视频已经投了多少硬币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        //新增视频投币
        if (dbVideoCoin == null) {
            if (amount > 2) {
                throw new ConditionException("最多只能投两枚硬币");
            }
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        } else {
            Integer dbAmount = dbVideoCoin.getAmount();
            dbAmount += amount;
            if (dbAmount > 2) {
                throw new ConditionException("总共可以投两枚硬币");
            }
            //更新视频投币
            // 此处采取直接更新策略，而不是先删除后插入，算然该操作省区判空
            // 每次操作将对应两次数据库操作，如果每个视频对应有大量投币操作，数据库负担大
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }
        //更新用户当前硬币总数
        userCoinService.updateUserCoinsAmount(userId, (userCoinsAmount - amount));
    }

    // 获得视频被投币总数，如果当前用户已登录还要返回当前用户的投币额
    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        // 获得投币总额
        Long count = videoDao.getVideoCoinsAmount(videoId);
        // 如果当前用户已登录,判断当前用户是否已经投币过
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        Integer coined = videoCoin != null ? videoCoin.getAmount() : 0;
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("coined", coined);
        return result;
    }

    // 查询投币过视频
    public List<VideoCoin> getCoinedVideo(Long userId) {
        return videoDao.getCoinedVideo(userId);
    }

    /**
     * 添加评论
     *
     * @param videoComment
     * @param userId
     */
    public void addVideoComment(VideoComment videoComment, Long userId) {
        // 参数检查
        Long videoId = videoComment.getVideoId();
        if (videoId == null) {
            throw new ConditionException("参数异常！");
        }
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        // 保存评论
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    /**
     * 分页查询视频评论，返回一个根评论的列表，每个根评论保存其对应子评论的信息。
     *
     * @param size:分页大小
     * @param no:第几页
     * @param videoId:视频ID
     * @return
     */
    public PageResult<VideoComment> pageListVideoComments(Integer size, Integer no, Long videoId) {
        // 参数校验
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("非法视频！");
        }
        // 分页信息
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        // 当前视频根评论数(root=null)
        Integer rootCommitTotal = videoDao.pageCountVideoComments(params);
        // 当前视频根评论(root=null)
        List<VideoComment> rootCommitList = new ArrayList<>();
        if (rootCommitTotal > 0) {
            rootCommitList = videoDao.pageListVideoComments(params);
            // 根评论ID
            List<Long> rootCommitIdList = rootCommitList.stream().map(VideoComment::getId).collect(Collectors.toList());
            // 查询各个根评论下子评论
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootIds(rootCommitIdList);
            // 批量查询根评论对应用户信息
            Set<Long> commitUserIdList = rootCommitList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            // 获得子评论回复的对象的ID
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            // 初始commitUserIdList和replyUserIdList存在重复，使用set去重
            // 获得全部评论对应的用户ID
            commitUserIdList.addAll(replyUserIdList);
            // 获得全部评论对应的用户信息
            List<UserInfo> userInfoList = userService.batchGetUserInfoByUserIds(commitUserIdList);
            // 构建<userId,userInfo>键值对，方便后续匹配
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            rootCommitList.forEach(comment -> {
                // 根评论ID
                Long id = comment.getId();
                // 当前根评论对应的子评论集合
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child -> {
                    // 找到当前根评论对应的子评论
                    if (id.equals(child.getRootId())) {
                        // 设置子评论对应的用户信息
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        // 设置子评论所回复用户对应的用户信息
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserInfo()));
                        childList.add(child);
                    }
                });
                // 设定根评论对应的子评论列表
                comment.setChildList(childList);
                // 设置根评论自身对应的用户信息
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }
        return new PageResult<>(rootCommitTotal, rootCommitList);
    }

    /**
     * 获取视频详情，包括视频信息、上传用户信息、点赞、收藏、投币
     *
     * @param videoId
     * @return
     */
    public Map<String, Object> getVideoDetails(Long videoId, Long userId) {
        // 获得视频信息
        Video video = videoDao.getVideoDetails(videoId);
        // 获得上传者信息
        Long upUserId = video.getUserId();
        User user = userService.getUserInfo(upUserId);
        UserInfo userInfo = user.getUserInfo();
        // 获得点赞信息
        Map<String, Object> likeResult = getVideoLikes(videoId, userId);
        // 获得投币信息
        Map<String, Object> coinResult = getVideoCoins(videoId, userId);
        // 获得收藏信息
        Map<String, Object> collectionResult = getVideoCollections(videoId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("upUserInfo", userInfo);
        // 点赞总数
        result.put("likeCount", likeResult.get("count"));
        // 当前用户是否点赞
        result.put("isLike", likeResult.get("like"));

        // 投币总数
        result.put("coinCount", coinResult.get("count"));
        // 当前用户投币数
        result.put("isCoin", coinResult.get("coined"));
        // 收藏总数
        result.put("collectCount", collectionResult.get("count"));
        // 当前用户是否收藏
        result.put("isCollect", collectionResult.get("collect"));

        return result;
    }
}
