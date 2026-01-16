package com.jsite.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jsite.system.domain.SysDictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 字典数据表 数据层
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 分页查询字典数据列表
     */
    IPage<SysDictData> selectDictDataList(IPage<SysDictData> page, @Param("dictData") SysDictData dictData);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictData> selectDictDataByType(@Param("dictType") String dictType);

    /**
     * 根据字典类型和字典键值查询字典数据信息
     */
    String selectDictLabel(@Param("dictType") String dictType, @Param("dictValue") String dictValue);

    /**
     * 根据字典数据ID查询信息
     */
    SysDictData selectDictDataById(@Param("dictCode") Long dictCode);

    /**
     * 查询字典数据数量
     */
    int countDictDataByType(@Param("dictType") String dictType);

    /**
     * 同步修改字典类型
     */
    int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
