package com.pyx.community.controller;

import com.pyx.community.dto.AccessTokenDTO;
import com.pyx.community.dto.GithubUser;
import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.User;
import com.pyx.community.provider.GithubProvider;
import com.pyx.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code")String code,
                           @RequestParam(name = "state")String state,
                           HttpServletResponse response) throws Exception {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.githubUser(accessToken);
        if(githubUser!=null&&githubUser.getId()!=null){
            //登录成功 写cookie和session
            User user = new User();//如果得到了githubUser返回的用户信息，就创建一个用户
            String token = UUID.randomUUID().toString();
            user.setToken(token);//随机一个UUID转换成String格式给token
            user.setName(githubUser.getName());//将github用户的用户名写入到user
            user.setAccountId(String.valueOf(githubUser.getId()));//同理写入ID，github中为Long类型，而数据库中为String，则需要转换
            user.setAvatarUrl(githubUser.getAvatar_url());//头像
            userService.createOrUpdate(user);
            //userMapper.insert(user);//调用插入方法，把user放到数据库中
            //将token值存放到cookie中
            /**
             * 设置cookie过期时间。一定要先设置失效，再放入。
             * 这里设置3天
             */
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(4320 * 60);
            response.addCookie(cookie);

            //request.getSession().setAttribute("user",githubUser);
            //重定向跳转，可以只跳转到localhost:8887
            //不再转到http://localhost:8887/callback?code=50c1c742d78e0771f392&state=1
            return "redirect:/";
        }else {
            //登录失败，重新登录
            return "redirect:/";
        }
    }

    /**
     * 退出按钮功能实现
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        request.getSession().removeAttribute("user");//移除session
        /**
         * 删除cookie思路
         * 设置一个同名的cookie，将其失效时间设为0，瞬间失效
         */
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/";
    }
}
