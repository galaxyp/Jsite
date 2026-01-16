package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysDictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典类型表 数据层
 */
@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {

    /**
     * 分页查询字典类型列表
     */
    IPage<SysDictType> selectDictTypeList(IPage<SysDictType> page, @Param("dictType") SysDictType dictType);

    /**
     * 查询所有字典类型
     */
    List<SysDictType> selectDictTypeAll();

    /**
     * 根据字典类型ID查询信息
     */
    SysDictType selectDictTypeById(@Param("dictId") Long dictId);

    /**
     * 根据字典类型查询信息
     */
    SysDictType selectDictTypeByType(@Param("dictType") String dictType);

    /**
     * 校验字典类型称是否唯一
     */
    SysDictType checkDictTypeUnique(@Param("dictType") String dictType);
}
