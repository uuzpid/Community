package com.pyx.community.service;

import com.pyx.community.mapper.UserMapper;
import com.pyx.community.model.User;
import com.pyx.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;


    public void createOrUpdate(User user) {
        /**
         * 通过AccountId查询用户表中是否有该用户
         * mybatis generate
         */
        UserExample userExample = new UserExample();
        userExample.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if(users.size()==0){
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
            User dbUser = users.get(0);
            User updateUser = new User();
            /**
             * 需要更新的项目放入新的user对象中
             * userMapper.updateByExample() 不使用这个因为这个是更新所有的元素
             * 这里创建时间不需要更新，因此使用updateByExampleSelective
             */
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria().andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
        }
    }
}
