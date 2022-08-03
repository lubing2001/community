package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {

    User selectById(int id);                    // 根据id查询

    User selectByName(String username);         // 根据name查询

    User selectByEmail(String email);           // 根据email查询

    int insertUser(User user);                  // 插入数据

    int updateStatus(int id, int status);       // 更新状态

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

}
