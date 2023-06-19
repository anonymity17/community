package com.majiang.demo.mapper;

import com.majiang.demo.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into users (account_id,name,token,gmt_create,gmt_modified) values (#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);//形参是一个类#{token}可以直接放

    @Select("select * from users where token = #{token}")
    User findByToken(@Param("token") String token);//形参不是一个类，加注解
}
