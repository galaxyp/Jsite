package com.jsite.generator.service.impl;

import com.jsite.generator.domain.GenTableColumn;
import com.jsite.generator.mapper.GenTableColumnMapper;
import com.jsite.generator.service.IGenTableColumnService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 代码生成字段 服务层实现
 */
@Service
@RequiredArgsConstructor
public class GenTableColumnServiceImpl implements IGenTableColumnService {

    private final GenTableColumnMapper genTableColumnMapper;

    /**
     * 查询业务字段列表
     *
     * @param tableId 业务字段编号
     * @return 业务字段集合
     */
    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(Long tableId) {
        return genTableColumnMapper.selectGenTableColumnListByTableId(tableId);
    }

    /**
     * 新增业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int insertGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnMapper.insert(genTableColumn);
    }

    /**
     * 修改业务字段
     *
     * @param genTableColumn 业务字段信息
     * @return 结果
     */
    @Override
    public int updateGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnMapper.updateById(genTableColumn);
    }

    /**
     * 删除业务字段信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteGenTableColumnByIds(Long[] ids) {
        return genTableColumnMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
