package com.nikh.cth.dao;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.Optional;

@Mapper
public interface UserDao {

    String GET_USER_BY_USERNAME_SQL = """
            SELECT CONCAT(username, ' ', password) from users where username = #{username} LIMIT 1;
            """;

    @Select(GET_USER_BY_USERNAME_SQL)
    Optional<String>  getUserByUserName(@Param("username") String username);
}
