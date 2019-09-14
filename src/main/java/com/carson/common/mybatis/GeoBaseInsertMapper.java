package com.carson.common.mybatis;

import org.apache.ibatis.annotations.InsertProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

@RegisterMapper
public interface GeoBaseInsertMapper<T> {
    /**
     * 保存一个实体，null的属性也会保存，不会使用数据库默认值
     *
     * @param record
     * @return
     */
    @InsertProvider(type = GeoBaseInsertProvider.class, method = "dynamicSQL")
    int insert(T record);

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     *
     * @param record
     * @return
     */
    @InsertProvider(type = GeoBaseInsertProvider.class, method = "dynamicSQL")
    int insertSelective(T record);
}
