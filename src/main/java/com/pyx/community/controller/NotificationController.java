package com.pyx.community.controller;

import com.pyx.community.dto.NotificationDTO;
import com.pyx.community.dto.PaginationDTO;
import com.pyx.community.enums.NotificationEnum;
import com.pyx.community.model.Notification;
import com.pyx.community.model.User;
import com.pyx.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(@PathVariable(name = "id") Long id,
                          Model model,
                          HttpServletRequest request) {
        /**
         * 从session中获取到user对象，user对象在拦截器中被放入session
         */
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO = notificationService.read(id, user);
        if(NotificationEnum.REPLY_COMMENT.getType()==notificationDTO.getType()
            ||NotificationEnum.REPLY_QUESTION.getType()==notificationDTO.getType()
        ) {
            return "redirect:/question/" + notificationDTO.getOuterid();
        } else
            return "redirect:/";
    }
}
