package com.jsite.common.utils;

import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形结构工具类
 *
 * @author jsite
 */
public class TreeUtils {

    /**
     * 构建树形结构
     *
     * @param list          数据列表
     * @param getId         获取ID的方法
     * @param getParentId   获取父ID的方法
     * @param setChildren   设置子节点的方法
     * @param rootParentId  根节点的父ID（通常为0或null）
     * @return 树形结构列表
     */
    public static <T, K> List<T> buildTree(
            List<T> list,
            Function<T, K> getId,
            Function<T, K> getParentId,
            BiConsumer<T, List<T>> setChildren,
            K rootParentId) {

        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }

        // 按父ID分组
        Map<K, List<T>> parentIdMap = list.stream()
                .collect(Collectors.groupingBy(getParentId));

        // 递归设置子节点
        list.forEach(node -> {
            K id = getId.apply(node);
            List<T> children = parentIdMap.get(id);
            if (children != null) {
                setChildren.accept(node, children);
            }
        });

        // 返回根节点
        return list.stream()
                .filter(node -> rootParentId == null
                        ? getParentId.apply(node) == null
                        : rootParentId.equals(getParentId.apply(node)))
                .collect(Collectors.toList());
    }

    /**
     * 构建树形结构（默认父ID为0）
     */
    public static <T> List<T> buildTree(
            List<T> list,
            Function<T, Long> getId,
            Function<T, Long> getParentId,
            BiConsumer<T, List<T>> setChildren) {
        return buildTree(list, getId, getParentId, setChildren, 0L);
    }

    /**
     * 获取所有子节点ID
     *
     * @param list          数据列表
     * @param getId         获取ID的方法
     * @param getParentId   获取父ID的方法
     * @param parentId      父节点ID
     * @return 所有子节点ID列表（包含传入的父节点ID）
     */
    public static <T, K> List<K> getChildIds(
            List<T> list,
            Function<T, K> getId,
            Function<T, K> getParentId,
            K parentId) {

        List<K> result = new ArrayList<>();
        if (CollUtil.isEmpty(list) || parentId == null) {
            return result;
        }

        result.add(parentId);

        // 按父ID分组
        Map<K, List<T>> parentIdMap = list.stream()
                .collect(Collectors.groupingBy(getParentId));

        // 递归获取子节点ID
        collectChildIds(parentIdMap, getId, parentId, result);

        return result;
    }

    /**
     * 递归收集子节点ID
     */
    private static <T, K> void collectChildIds(
            Map<K, List<T>> parentIdMap,
            Function<T, K> getId,
            K parentId,
            List<K> result) {

        List<T> children = parentIdMap.get(parentId);
        if (CollUtil.isEmpty(children)) {
            return;
        }

        for (T child : children) {
            K childId = getId.apply(child);
            result.add(childId);
            collectChildIds(parentIdMap, getId, childId, result);
        }
    }

    /**
     * 获取所有父节点ID
     *
     * @param list          数据列表
     * @param getId         获取ID的方法
     * @param getParentId   获取父ID的方法
     * @param nodeId        节点ID
     * @param rootParentId  根节点的父ID
     * @return 所有父节点ID列表（不包含传入的节点ID）
     */
    public static <T, K> List<K> getParentIds(
            List<T> list,
            Function<T, K> getId,
            Function<T, K> getParentId,
            K nodeId,
            K rootParentId) {

        List<K> result = new ArrayList<>();
        if (CollUtil.isEmpty(list) || nodeId == null) {
            return result;
        }

        // 按ID建立索引
        Map<K, T> idMap = list.stream()
                .collect(Collectors.toMap(getId, node -> node, (a, b) -> a));

        // 向上查找父节点
        T node = idMap.get(nodeId);
        while (node != null) {
            K parentId = getParentId.apply(node);
            if (parentId == null || parentId.equals(rootParentId)) {
                break;
            }
            result.add(parentId);
            node = idMap.get(parentId);
        }

        return result;
    }

    /**
     * 过滤树形结构
     *
     * @param list          数据列表
     * @param getId         获取ID的方法
     * @param getParentId   获取父ID的方法
     * @param getChildren   获取子节点的方法
     * @param setChildren   设置子节点的方法
     * @param predicate     过滤条件
     * @return 过滤后的树形结构
     */
    public static <T, K> List<T> filterTree(
            List<T> list,
            Function<T, K> getId,
            Function<T, K> getParentId,
            Function<T, List<T>> getChildren,
            BiConsumer<T, List<T>> setChildren,
            java.util.function.Predicate<T> predicate) {

        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<T> result = new ArrayList<>();
        for (T node : list) {
            // 递归过滤子节点
            List<T> children = getChildren.apply(node);
            if (CollUtil.isNotEmpty(children)) {
                List<T> filteredChildren = filterTree(
                        children, getId, getParentId, getChildren, setChildren, predicate);
                setChildren.accept(node, filteredChildren);
            }

            // 判断当前节点是否满足条件，或者有子节点
            List<T> currentChildren = getChildren.apply(node);
            if (predicate.test(node) || CollUtil.isNotEmpty(currentChildren)) {
                result.add(node);
            }
        }

        return result;
    }
}
