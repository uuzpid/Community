package com.pyx.community.controller;

import com.pyx.community.dto.CommentCreateDTO;
import com.pyx.community.dto.CommentDTO;
import com.pyx.community.dto.ResultDTO;
import com.pyx.community.enums.CommentTypeEnum;
import com.pyx.community.exception.CustomizeErrorCode;
import com.pyx.community.model.Comment;
import com.pyx.community.model.User;
import com.pyx.community.service.CommentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;
    /**
     * @RequestBody 自动的把json传过来的key和value赋值到CommentDTO中
     */
    @ResponseBody
    @RequestMapping(value = "/comment",method = RequestMethod.POST)
    public Object post(@RequestBody CommentCreateDTO commentCreateDTO,
                       HttpServletRequest request){
        /**
         * 用户没有登录，则返回json错误信息
         */
        User user = (User) request.getSession().getAttribute("user");
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        /**
         * 如果回复是空
         * commentCreateDTO.getContent()==null||commentCreateDTO.getContent()==""
         * 如果同一个东西判断两次可以用lang工具包代替
         * 需要导入lang依赖
         */
        if(commentCreateDTO==null|| StringUtils.isBlank(commentCreateDTO.getContent())){
            return ResultDTO.errorOf(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        /**
         * 用户成功登录，进行回复
         */
        Comment comment = new Comment();
        comment.setParentId(commentCreateDTO.getParentId());
        comment.setContent(commentCreateDTO.getContent());
        comment.setType(commentCreateDTO.getType());
        comment.setGmtModified(System.currentTimeMillis());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        commentService.insert(comment,user);
        //请求成功，返回json
        return ResultDTO.okOf();
    }

    @ResponseBody
    @RequestMapping(value = "/comment/{id}",method = RequestMethod.GET)
    public ResultDTO<List> comments(@PathVariable(name = "id")Long id){
        List<CommentDTO> commentDTOS = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTOS);
    }
}
