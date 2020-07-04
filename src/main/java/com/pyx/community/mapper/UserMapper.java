package com.pyx.community.mapper;

import com.pyx.community.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("insert into t_user(name,account_id,token,gmt_create,gmt_modified,avatar_url) " +
            "values(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    //查询数据库中token对应的user
    @Select("select * from t_user where token = #{token}")
    User findByToken(@Param("token") String token);
    //当传入参数不是一个类时需要加@Param注解

    @Select("select * from t_user where id = #{id}")
    User findById(@Param("id")Integer id);

    @Select("select * from t_user where account_id = #{accountId}")
    User findByAccountId(@Param("accountId")String accountId);

    @Update("update t_user set name=#{name},token=#{token}," +
            "gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} where id=#{id}")
    void update(User user);
}
