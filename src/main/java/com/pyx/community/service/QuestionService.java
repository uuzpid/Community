package com.pyx.community.service;

import com.pyx.community.dto.PaginationDTO;
import com.pyx.community.dto.QuestionDTO;
import com.pyx.community.mapper.QuestionMapper;
import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.Question;
import com.pyx.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//当一个请求既要用到User又要用到Question时
//可以使用service中间层进行组装
@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.count();//查询出所有表中数据总数
        paginationDTO.setPagination(totalCount,page,size);

        /**
         * 简单处理一下当用户访问的请求携带的page是小于1或者大于totalPage的情况
         */
        if(page<1){
            page=1;
        }
        if(page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        /**
         * 分页查询时sql语句需要用到两个参数 select * from t_question limit a,b
         * a是偏移量，即从哪些数据开始显示，初始为0
         * b是一页多少个 这里设置为5个
         */
        //这里的page是当前页数，size是每页显示多少个
        //size*(page-1)
        Integer offset = size*(page-1);//offset偏移量
        List<Question> questions = questionMapper.list(offset,size);

        /**
         * for循环遍历所有的question对象，并且把question对象和user对象一起组装到questionDTO中
         */
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            //通过question对象获取到发起者的id。再通过findById查询到user的全部信息
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question对象的所有属性放到questionDTO中。
            //questionDTO.setCreator(question.getCreator()); 比较老的方法
            //BeanUtils工具类，可以快速把question对象的所有属性给questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);//将查询出来的questionDTOList放入新建的paginationDTO类中
        return paginationDTO;
    }

    public PaginationDTO list(Integer userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.countByUserId(userId);//查询出所有表中数据总数
        paginationDTO.setPagination(totalCount,page,size);

        /**
         * 简单处理一下当用户访问的请求携带的page是小于1或者大于totalPage的情况
         */
        if(page<1){
            page=1;
        }
        if(page>paginationDTO.getTotalPage()){
            page=paginationDTO.getTotalPage();
        }

        /**
         * 分页查询时sql语句需要用到两个参数 select * from t_question limit a,b
         * a是偏移量，即从哪些数据开始显示，初始为0
         * b是一页多少个 这里设置为5个
         */
        //这里的page是当前页数，size是每页显示多少个
        //size*(page-1)
        Integer offset = size*(page-1);//offset偏移量
        List<Question> questions = questionMapper.listByUserId(userId,offset,size);

        /**
         * for循环遍历所有的question对象，并且把question对象和user对象一起组装到questionDTO中
         */
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questions) {
            //通过question对象获取到发起者的id。再通过findById查询到user的全部信息
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            //将question对象的所有属性放到questionDTO中。
            //questionDTO.setCreator(question.getCreator()); 比较老的方法
            //BeanUtils工具类，可以快速把question对象的所有属性给questionDTO
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }

        paginationDTO.setQuestions(questionDTOList);//将查询出来的questionDTOList放入新建的paginationDTO类中
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            /**
             * 说明是第一次创建问题
             */
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        }else {
            /**
             * 更新问题
             */
            question.setGmtModified(question.getGmtCreate());
            questionMapper.update(question);
        }
    }
}
