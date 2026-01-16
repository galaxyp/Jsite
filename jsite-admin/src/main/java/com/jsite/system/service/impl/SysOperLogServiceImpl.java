package com.jsite.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.system.domain.SysOperLog;
import com.jsite.system.mapper.SysOperLogMapper;
import com.jsite.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 操作日志 服务层处理
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {
        operLogMapper.insert(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @param page    分页对象
     * @return 操作日志集合
     */
    @Override
    public Page<SysOperLog> selectOperLogPage(SysOperLog operLog, Page<SysOperLog> page) {
        return operLogMapper.selectPage(page, buildQueryWrapper(operLog));
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        return operLogMapper.selectList(buildQueryWrapper(operLog));
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<SysOperLog> buildQueryWrapper(SysOperLog operLog) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(operLog.getTitle()), SysOperLog::getTitle, operLog.getTitle())
                .eq(ObjectUtil.isNotNull(operLog.getBusinessType()), SysOperLog::getBusinessType, operLog.getBusinessType())
                .like(StrUtil.isNotBlank(operLog.getOperName()), SysOperLog::getOperName, operLog.getOperName())
                .eq(ObjectUtil.isNotNull(operLog.getStatus()), SysOperLog::getStatus, operLog.getStatus())
                .orderByDesc(SysOperLog::getOperId);
        return wrapper;
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    @Override
    public int deleteOperLogByIds(Long[] operIds) {
        return operLogMapper.deleteBatchIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return operLogMapper.selectById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
