package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.entity.UserDepartment;
import com.qqkj.inspection.inspection.entity.UserRoles;
import com.qqkj.inspection.inspection.mapper.TUserMapper;
import com.qqkj.inspection.inspection.service.ITUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser> implements ITUserService {
    @Autowired
    TUserMapper userMapper;

    @Override
    public IPage<UserDepartment> queryDepartmentUser(Page<UserDepartment> page, Integer id, String name) {

        return userMapper.queryUserDepartment(page,id,name);
    }

    @Override
    public Set<String> rolename(String username) {
        return userMapper.getRole(username);
    }

    public UserRoles getRoleId(String username, Integer id) {
        return userMapper.getRoleId(username,id);
    }
}
