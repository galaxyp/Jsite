package com.jsite.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.exception.ServiceException;
import com.jsite.system.domain.SysPost;
import com.jsite.system.mapper.SysPostMapper;
import com.jsite.system.mapper.SysUserPostMapper;
import com.jsite.system.service.ISysPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 岗位信息 服务层处理
 */
@Service
@RequiredArgsConstructor
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements ISysPostService {

    private final SysPostMapper postMapper;
    private final SysUserPostMapper userPostMapper;

    @Override
    public TableDataInfo<SysPost> selectPagePostList(SysPost post) {
        Page<SysPost> page = new Page<>(PageQuery.getPageNum(), PageQuery.getPageSize());
        Page<SysPost> result = postMapper.selectPostList(page, post);
        return TableDataInfo.build(result);
    }

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        Page<SysPost> page = new Page<>(1, Integer.MAX_VALUE);
        return postMapper.selectPostList(page, post).getRecords();
    }

    @Override
    public List<SysPost> selectPostAll() {
        return postMapper.selectPostAll();
    }

    @Override
    public SysPost selectPostById(Long postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    public List<Long> selectPostListByUserId(Long userId) {
        return postMapper.selectPostListByUserId(userId);
    }

    @Override
    public boolean checkPostNameUnique(SysPost post) {
        Long postId = ObjectUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostNameUnique(post.getPostName());
        if (ObjectUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkPostCodeUnique(SysPost post) {
        Long postId = ObjectUtil.isNull(post.getPostId()) ? -1L : post.getPostId();
        SysPost info = postMapper.checkPostCodeUnique(post.getPostCode());
        if (ObjectUtil.isNotNull(info) && info.getPostId().longValue() != postId.longValue()) {
            return false;
        }
        return true;
    }

    @Override
    public int countUserPostById(Long postId) {
        return userPostMapper.countUserPostById(postId);
    }

    @Override
    public int deletePostById(Long postId) {
        return postMapper.deleteById(postId);
    }

    @Override
    public int deletePostByIds(Long[] postIds) {
        for (Long postId : postIds) {
            SysPost post = selectPostById(postId);
            if (countUserPostById(postId) > 0) {
                throw new ServiceException(String.format("%1$s已分配，不能删除", post.getPostName()));
            }
        }
        return postMapper.deleteBatchIds(Arrays.asList(postIds));
    }

    @Override
    public int insertPost(SysPost post) {
        return postMapper.insert(post);
    }

    @Override
    public int updatePost(SysPost post) {
        return postMapper.updateById(post);
    }
}
