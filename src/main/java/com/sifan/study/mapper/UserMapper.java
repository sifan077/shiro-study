package com.sifan.study.mapper;

import com.sifan.study.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 思凡
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2022-10-12 16:04:33
* @Entity com.sifan.study.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




