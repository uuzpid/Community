package com.pyx.community.controller;

import com.pyx.community.dto.PaginationDTO;
import com.pyx.community.dto.QuestionDTO;
import com.pyx.community.mapper.QuestionMapper;
import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.Question;
import com.pyx.community.model.User;
import com.pyx.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionService questionService;

    //需要接受两个参数 page表示页数，size表示一页多少个
    @GetMapping("/")
    public String hello( Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size) {

        //List<QuestionDTO> questionList = questionMapper.list();无法直接返还QuestionDTO
        PaginationDTO paginationDTO = questionService.list(page,size);
        model.addAttribute("paginationDTO", paginationDTO);
        return "index";
    }


}
