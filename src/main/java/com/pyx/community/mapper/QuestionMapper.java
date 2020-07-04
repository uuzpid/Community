package com.pyx.community.mapper;

import com.pyx.community.dto.QuestionDTO;
import com.pyx.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into t_question (title,description,gmt_create,gmt_modified,creator,tag) " +
            "values (#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);

    @Select("select * from t_question limit #{offset},#{size}")
    List<Question> list(@Param(value = "offset") Integer offset, @Param(value = "size") Integer size);//分页查询 offset偏移量，size每页显示多少个

    @Select("select count(1) from t_question")
    Integer count();//查询有多少条数据count（1） count1比count*效率高

    @Select("select * from t_question where creator = #{userId} limit #{offset},#{size}")
    List<Question> listByUserId(@Param(value = "userId")Integer userId, @Param(value = "offset") Integer offset, @Param(value = "size") Integer size);

    @Select("select count(1) from t_question where creator = #{userId}")
    Integer countByUserId(@Param(value = "userId")Integer userId);

    @Select("select * from t_question where id = #{id}")
    Question getById(@Param(value = "id") Integer id);
}
