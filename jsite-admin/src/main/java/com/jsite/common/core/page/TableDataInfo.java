package com.jsite.common.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页数据封装
 *
 * @author jsite
 */
@Data
@NoArgsConstructor
@Schema(description = "分页数据")
public class TableDataInfo<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private long total;

    /**
     * 列表数据
     */
    @Schema(description = "列表数据")
    private List<T> rows;

    /**
     * 消息状态码
     */
    @Schema(description = "状态码")
    private int code;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String msg;

    /**
     * 构造函数
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
        this.code = 200;
        this.msg = "查询成功";
    }

    /**
     * 从分页对象构建
     *
     * @param page 分页对象
     */
    public static <T> TableDataInfo<T> build(IPage<T> page) {
        TableDataInfo<T> info = new TableDataInfo<>();
        info.setCode(200);
        info.setMsg("查询成功");
        info.setRows(page.getRecords());
        info.setTotal(page.getTotal());
        return info;
    }

    /**
     * 从列表构建
     *
     * @param list 列表数据
     */
    public static <T> TableDataInfo<T> build(List<T> list) {
        TableDataInfo<T> info = new TableDataInfo<>();
        info.setCode(200);
        info.setMsg("查询成功");
        info.setRows(list);
        info.setTotal(list != null ? list.size() : 0);
        return info;
    }

    /**
     * 从列表和总数构建
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public static <T> TableDataInfo<T> build(List<T> list, long total) {
        TableDataInfo<T> info = new TableDataInfo<>();
        info.setCode(200);
        info.setMsg("查询成功");
        info.setRows(list);
        info.setTotal(total);
        return info;
    }
}
