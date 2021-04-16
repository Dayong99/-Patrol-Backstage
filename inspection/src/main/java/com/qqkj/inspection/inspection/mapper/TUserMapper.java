package com.qqkj.inspection.inspection.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qqkj.inspection.inspection.entity.UserDepartment;
import com.qqkj.inspection.inspection.entity.UserRoles;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
public interface TUserMapper extends BaseMapper<TUser> {
    Set<String> getRole(@Param("username")String username);

    UserRoles getRoleId(@Param("username")String username,@Param("id")Integer id);
    Integer setRole(@Param("userid") Long userid, @Param("roid") Integer roid);
    Integer addRole(@Param("userid")Long userid,@Param("roid")Integer roid);

    IPage<UserDepartment> queryUserDepartment(@Param("page") Page<UserDepartment> page, @Param("id") Integer id, @Param("name") String name);

}
