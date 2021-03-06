package com.carson.mapper;

import com.carson.common.mybatis.GeoBaseInsertMapper;
import com.carson.common.mybatis.GeoBaseUpdateMapper;
import com.carson.pojo.User;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.base.BaseSelectMapper;

@Repository
public interface UserMapper extends GeoBaseInsertMapper<User>, GeoBaseUpdateMapper<User>, BaseSelectMapper<User> {
}
