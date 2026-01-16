package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsite.system.domain.SysUserPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户与岗位关联表 数据层
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {

    /**
     * 通过用户ID删除用户和岗位关联
     */
    int deleteUserPostByUserId(@Param("userId") Long userId);

    /**
     * 通过岗位ID查询岗位使用数量
     */
    int countUserPostById(@Param("postId") Long postId);

    /**
     * 批量删除用户和岗位关联
     */
    int deleteUserPost(@Param("ids") Long[] ids);

    /**
     * 批量新增用户岗位信息
     */
    int batchUserPost(@Param("list") List<SysUserPost> userPostList);
}
