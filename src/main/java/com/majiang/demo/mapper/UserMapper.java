package com.majiang.demo.mapper;

import com.majiang.demo.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into users (account_id,name,token,gmt_create,gmt_modified,avatar_url) values (#{accountId},#{name},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);//形参是一个类#{token}可以直接放

    @Select("select * from users where token = #{token}")
    User findByToken(@Param("token") String token);//形参不是一个类，加注解

    @Select("select * from users where id = #{id}")
    User findById(@Param("id") Integer id);

    @Select("select * from users where account_id = #{accountId}")
    User findByAccountId(@Param("accountId") String accountId);

    @Update("update users set name = #{name},token = #{token},gmt_modified = #{gmtModified},avatar_url = #{avatarUrl} where id = #{id}")
    void update(User user);
}
