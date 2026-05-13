package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxin.training.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}