package com.pyx.community.service;

import com.pyx.community.dto.PaginationDTO;
import com.pyx.community.dto.QuestionDTO;
import com.pyx.community.dto.QuestionQueryDTO;
import com.pyx.community.exception.CustomizeErrorCode;
import com.pyx.community.exception.CustomizeException;
import com.pyx.community.mapper.QuestionExtMapper;
import com.pyx.community.mapper.QuestionMapper;
import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.Question;
import com.pyx.community.model.QuestionExample;
import com.pyx.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

//当一个请求既要用到User又要用到Question时
//可以使用service中间层进行组装
@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     * 查出问题列表
     */
    public PaginationDTO list(String search,Integer page, Integer size) {
        //如果tag为空，这种情况实际不存在
        if(StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO paginationDTO = new PaginationDTO();

        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);

        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);//查询出所有表中数据总数


        Integer totalPage;
        /**
         * 简单处理一下当用户访问的请求携带的page是小于1或者大于totalPage的情况
         */
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        /**
         * 分页查询时sql语句需要用到两个参数 select * from t_question limit a,b
         * a是偏移量，即从哪些数据开始显示，初始为0
         * b是一页多少个 这里设置为5个
         */
        paginationDTO.setPagination(totalPage,page);
        //这里的page是当前页数，size是每页显示多少个
        //size*(page-1)
        Integer offset = size*(page-1);//offset偏移量
        QuestionExample questionExample = new QuestionExample();
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> questions =
                questionExtMapper.selectBySearch(questionQueryDTO);

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

        paginationDTO.setData(questionDTOList);//将查询出来的questionDTOList放入新建的paginationDTO类中
        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        /**
         * 查询出表中的所有数据
         */
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        Integer totalPage;

        /**
         * 简单处理一下当用户访问的请求携带的page是小于1或者大于totalPage的情况
         */
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }

        /**
         * 分页查询时sql语句需要用到两个参数 select * from t_question limit a,b
         * a是偏移量，即从哪些数据开始显示，初始为0
         * b是一页多少个 这里设置为5个
         */
        if(totalCount==0){
            return new PaginationDTO();
        }
        //这里的page是当前页数，size是每页显示多少个
        //size*(page-1)
        paginationDTO.setPagination(totalPage,page);
        Integer offset = size*(page-1);//offset偏移量
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions =
                questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

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

        paginationDTO.setData(questionDTOList);//将查询出来的questionDTOList放入新建的paginationDTO类中
        return paginationDTO;
    }

    /**
     * 把用户信息包装到QuestionDTO中
     * @param id
     */
    public QuestionDTO getById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question==null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());

        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     * 当问题被提交时，这个方法用于判断问题是更新还是首次创建
     */
    public void createOrUpdate(Question question) {
        if(question.getId()==null){
            /**
             * 说明是第一次创建问题
             */
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            questionMapper.insert(question);
        }else {
            /**
             * 更新问题
             */
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updateResult = questionMapper.updateByExampleSelective(updateQuestion, example);
            if(updateResult != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 累加阅读数功能
     * 仿照mybatis generate重写一个QuestionExtMapper.xml和QuestionExtMapper.java
     * 在里面定义一个方法 ，让数据库累加时做到view_count=view_count+1
     * 而不是view_count=xxx+1
     * 解决高并发问题
     * @param id
     */
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);//每阅读一次+1
        questionExtMapper.incView(question);
    }

    /**
     * 查询出标签相关的问题
     */
    public List<QuestionDTO> selectRelated(QuestionDTO querryDTO) {
        //如果tag为空，这种情况实际不存在
        if(StringUtils.isBlank(querryDTO.getTag())){
            return new ArrayList<>();
        }
        /**
         * 查出所有tag，并且以|连接
         */
        String[] tags = StringUtils.split(querryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(querryDTO.getId());
        question.setTag(regexpTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map((q) -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
