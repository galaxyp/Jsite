package com.jsite.common.core.page;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询参数
 *
 * @author jsite
 */
@Data
@Schema(description = "分页查询参数")
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页码
     */
    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String orderByColumn;

    /**
     * 排序方式 asc/desc
     */
    @Schema(description = "排序方式", defaultValue = "asc")
    private String isAsc = "asc";

    /**
     * 构建分页对象
     */
    public <T> Page<T> build() {
        Integer pageNum = this.pageNum != null && this.pageNum > 0 ? this.pageNum : 1;
        Integer pageSize = this.pageSize != null && this.pageSize > 0 ? this.pageSize : 10;
        // 限制最大每页数量
        if (pageSize > 500) {
            pageSize = 500;
        }
        Page<T> page = new Page<>(pageNum, pageSize);
        // 排序
        if (StrUtil.isNotBlank(orderByColumn)) {
            // 驼峰转下划线
            String columnName = StrUtil.toUnderlineCase(orderByColumn);
            if ("asc".equalsIgnoreCase(isAsc)) {
                page.addOrder(OrderItem.asc(columnName));
            } else {
                page.addOrder(OrderItem.desc(columnName));
            }
        }
        return page;
    }
}
