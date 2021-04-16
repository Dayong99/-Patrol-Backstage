package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qqkj.inspection.inspection.entity.UserDepartment;
import com.qqkj.inspection.inspection.entity.UserRoles;

import java.util.Set;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
public interface ITUserService extends IService<TUser> {
    IPage<UserDepartment> queryDepartmentUser(Page<UserDepartment> page, Integer id, String name);
    Set<String>rolename(String username);
    UserRoles getRoleId(String username, Integer id);
}
