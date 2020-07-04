package com.pyx.community.service;

import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public void createOrUpdate(User user) {
        /**
         * 通过AccountId查询用户表中是否有该用户
         */
        User dbUser = userMapper.findByAccountId(user.getAccountId());
        if(dbUser==null){
            /**插入user
             * 当user第一次插入时，需要写入用户创建时间
             */
            user.setGmtCreate(System.currentTimeMillis());//写入创建用户的时间
            user.setGmtModified(user.getGmtCreate());//得到刚才写入的时间
            userMapper.insert(user);
        }else {
            /**更新user
             * 不需要更新用户创建时间，但是头像，名字，token都需要更新
             */
            dbUser.setGmtModified(System.currentTimeMillis());
            dbUser.setAvatarUrl(user.getAvatarUrl());
            dbUser.setName(user.getName());
            dbUser.setToken(user.getToken());
            userMapper.update(dbUser);
        }
    }
}
