package com.pyx.community.service;

import com.pyx.community.dto.CommentDTO;
import com.pyx.community.enums.CommentTypeEnum;
import com.pyx.community.enums.NotificationEnum;
import com.pyx.community.enums.NotificationStatusEnum;
import com.pyx.community.exception.CustomizeErrorCode;
import com.pyx.community.exception.CustomizeException;
import com.pyx.community.mapper.*;
import com.pyx.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional//为这个方法开启事务
    public void insert(Comment comment, User commentator) {
        /**
         * 当回复的问题或者回复 不存在或者id为0时，需要返回给controller一个提示信息
         * 如何才能返还给controller
         * 可使用异常抛出机制
         */
        if (comment.getParentId() == null || comment.getParentId() == 0) {//判断问题是否为空和0
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUNT);
        }

        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {//判断回复类型是否是空和1，2.
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //回复评论
            //在t_comment中查出父类评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null){
                //如果没有找到父评论，则抛出异常
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }

            //如果父类评论存在，则把这个评论添加进数据库
            commentMapper.insert(comment);
            dbComment.setCommentCount(1L);
            commentExtMapper.incCommentCount(dbComment);

            //创建通知
            createNotifiy(comment,dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationEnum.REPLY_COMMENT, question.getId());

        } else {
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if(question==null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            //这里使用insertSelective而不用insert避免在第一次回复时设置一个null值。导致最后永远是null4
            //也可以改成question.setCommentCount(0)；
            commentMapper.insertSelective(comment);//评论成功
            //评论数+1
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);

            //创建通知
            createNotifiy(comment,question.getCreator(),commentator.getName(),question.getTitle(),NotificationEnum.REPLY_QUESTION, question.getId());
        }
    }

    //创建通知
    private void createNotifiy(Comment comment, Long receiver, String notifyName, String outerTitle, NotificationEnum notificationType, Long outerid) {
        //如果接收通知和发布通知的是同一个人，就不需要通知
        if(receiver==comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerid);//回复的人id
        notification.setNotifier(comment.getCommentator());//通知的人id
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifyName);
        notification.setOutertitle(outerTitle);
        notificationMapper.insert(notification);
    }

    /**
     * 查询出当前问题下所有回复的方法
     */
    public List<CommentDTO> listByTargetId(Long id, CommentTypeEnum type) {
        /**
         * id可能是回复评论或者问题，因此需要多个条件
         * 根据回复目标的id（parentId），并且要求这个id==CommentTypeEnum.QUESTION.getType()
         * 可以100%查出这个问题下的所有回复
         * */
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).
                andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");//将回复按照创建时间，倒叙排序
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if(comments.size()==0){
            /**
             * 如果查出来的回复数是0，返回空数组
             */
            return new ArrayList<>();
        }
        /**
         * 遍历查出来的comments回复集合，用map映射，通过每一个回复，找出每一个回复人的ID
         * 并收集返回成一个Set集合
         */
        List<Long> commentators = comments.stream()
                                         .map(comment -> comment.getCommentator())
                                         .distinct()
                                         .collect(Collectors.toList());
/*        *//**
         * 将上面的Set集合，添加到list集合中
         * 因为下面的andIdIn()方法需要的元素是List集合
         *//*
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators);*/

        /**
         * 再根据回复者的id找到对应的用户id
         */
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(commentators);
        List<User> users = userMapper.selectByExample(userExample);

        /**
         * 将用户id作为key，用户作为value放入map中
         */
        Map<Long, User> userMap = users.stream()
                                       .collect(Collectors.toMap(user -> user.getId(), user -> user));

        /**
         * 根据查询出来的回复集合list，每有一个回复，就new一个评论对象出来。
         * 将comment封装成commentDTO
         * 最后放入集合中
         */
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
