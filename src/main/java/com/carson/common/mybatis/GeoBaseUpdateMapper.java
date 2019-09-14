package com.carson.common.mybatis;

import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

@RegisterMapper
public interface GeoBaseUpdateMapper<T> {
    /**
     * 根据主键更新实体全部字段，null值会被更新
     *
     * @param record
     * @return
     */
    @UpdateProvider(type = GeoBaseUpdateProvider.class, method = "dynamicSQL")
    int updateByPrimaryKey(T record);

    /**
     * 根据主键更新属性不为null的值
     *
     * @param record
     * @return
     */
    @UpdateProvider(type = GeoBaseUpdateProvider.class, method = "dynamicSQL")
    int updateByPrimaryKeySelective(T record);
}
