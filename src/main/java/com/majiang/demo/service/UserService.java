package com.majiang.demo.service;

import com.majiang.demo.mapper.UserMapper;
import com.majiang.demo.model.User;
import com.majiang.demo.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {//user为session中的user
        //通过session中的user中的accountid去数据库中查找account_id
//        User dbUser = userMapper.findByAccountId(user.getAccountId());
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                        .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0){
            //说明不匹配，数据库中没有该用户
            //插入
            //插入时间
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else{
            //数据库中已经有了该用户，更新token
            User dbUser = users.get(0);
            //定义用作更新的updateUser
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());//时间变化
            updateUser.setAvatarUrl(user.getAvatarUrl());//头像可能变化
            updateUser.setName(user.getName());//名字可能变化
            //更新token
            updateUser.setToken(user.getToken());

            UserExample example = new UserExample();
            example.createCriteria()
                            .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
//            userMapper.update(dbUser);
        }
    }
}
