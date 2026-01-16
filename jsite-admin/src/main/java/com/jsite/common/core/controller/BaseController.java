package com.jsite.common.core.controller;

import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsite.common.core.domain.R;
import com.jsite.common.core.page.PageQuery;
import com.jsite.common.core.page.TableDataInfo;
import com.jsite.common.utils.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 基础控制器
 *
 * @author jsite
 */
public class BaseController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取分页对象
     */
    protected <T> Page<T> getPage() {
        return new Page<>(getPageNum(), getPageSize());
    }

    /**
     * 获取当前页码
     */
    protected int getPageNum() {
        return Convert.toInt(ServletUtils.getParameter("pageNum"), 1);
    }

    /**
     * 获取每页大小
     */
    protected int getPageSize() {
        return Convert.toInt(ServletUtils.getParameter("pageSize"), 10);
    }

    /**
     * 获取分页数据
     *
     * @param page 分页对象
     * @return 分页数据
     */
    protected <T> TableDataInfo<T> getDataTable(IPage<T> page) {
        return TableDataInfo.build(page);
    }

    /**
     * 获取列表数据
     *
     * @param list 列表
     * @return 分页数据
     */
    protected <T> TableDataInfo<T> getDataTable(List<T> list) {
        return TableDataInfo.build(list);
    }

    /**
     * 获取列表数据（带总数）
     *
     * @param list  列表
     * @param total 总数
     * @return 分页数据
     */
    protected <T> TableDataInfo<T> getDataTable(List<T> list, long total) {
        return TableDataInfo.build(list, total);
    }

    /**
     * 成功响应
     */
    protected R<Void> success() {
        return R.ok();
    }

    /**
     * 成功响应（带数据）
     */
    protected <T> R<T> success(T data) {
        return R.ok(data);
    }

    /**
     * 成功响应（带消息）
     */
    protected R<Void> success(String msg) {
        return R.ok(msg, null);
    }

    /**
     * 失败响应
     */
    protected R<Void> error() {
        return R.fail();
    }

    /**
     * 失败响应（带消息）
     */
    protected R<Void> error(String msg) {
        return R.fail(msg);
    }

    /**
     * 失败响应（带状态码和消息）
     */
    protected R<Void> error(int code, String msg) {
        return R.fail(code, msg);
    }

    /**
     * 根据影响行数返回响应结果
     */
    protected R<Void> toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 根据布尔值返回响应结果
     */
    protected R<Void> toAjax(boolean result) {
        return result ? success() : error();
    }
}
