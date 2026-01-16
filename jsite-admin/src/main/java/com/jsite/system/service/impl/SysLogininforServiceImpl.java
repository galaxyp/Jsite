package com.jsite.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.system.domain.SysLogininfor;
import com.jsite.system.mapper.SysLogininforMapper;
import com.jsite.system.service.ISysLogininforService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 */
@Service
@RequiredArgsConstructor
public class SysLogininforServiceImpl implements ISysLogininforService {

    private final SysLogininforMapper logininforMapper;

    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininfor logininfor) {
        logininforMapper.insert(logininfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @param page       分页对象
     * @return 登录记录集合
     */
    @Override
    public Page<SysLogininfor> selectLogininforPage(SysLogininfor logininfor, Page<SysLogininfor> page) {
        return logininforMapper.selectPage(page, buildQueryWrapper(logininfor));
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininfor> selectLogininforList(SysLogininfor logininfor) {
        return logininforMapper.selectList(buildQueryWrapper(logininfor));
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysLogininfor> buildQueryWrapper(SysLogininfor logininfor) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(logininfor.getIpaddr()), SysLogininfor::getIpaddr, logininfor.getIpaddr())
                .like(StrUtil.isNotBlank(logininfor.getUserName()), SysLogininfor::getUserName, logininfor.getUserName())
                .eq(StrUtil.isNotBlank(logininfor.getStatus()), SysLogininfor::getStatus, logininfor.getStatus())
                .orderByDesc(SysLogininfor::getInfoId);
        return wrapper;
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLogininforByIds(Long[] infoIds) {
        return logininforMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor() {
        logininforMapper.cleanLogininfor();
    }
}
