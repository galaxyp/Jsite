package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 岗位信息 数据层
 */
@Mapper
public interface SysPostMapper extends BaseMapper<SysPost> {

    /**
     * 分页查询岗位列表
     */
    IPage<SysPost> selectPostList(IPage<SysPost> page, @Param("post") SysPost post);

    /**
     * 查询所有岗位
     */
    List<SysPost> selectPostAll();

    /**
     * 根据用户ID获取岗位选择框列表
     */
    List<Long> selectPostListByUserId(@Param("userId") Long userId);

    /**
     * 查询用户所属岗位组
     */
    List<SysPost> selectPostsByUserName(@Param("userName") String userName);

    /**
     * 根据岗位ID查询岗位信息
     */
    SysPost selectPostById(@Param("postId") Long postId);

    /**
     * 校验岗位名称是否唯一
     */
    SysPost checkPostNameUnique(@Param("postName") String postName);

    /**
     * 校验岗位编码是否唯一
     */
    SysPost checkPostCodeUnique(@Param("postCode") String postCode);
}
