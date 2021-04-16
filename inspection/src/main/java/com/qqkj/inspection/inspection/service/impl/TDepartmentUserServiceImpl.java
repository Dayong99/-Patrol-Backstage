package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TDepartmentUser;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.mapper.TDepartmentUserMapper;
import com.qqkj.inspection.inspection.service.ITDepartmentUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部门成员表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-27
 */
@Service
public class TDepartmentUserServiceImpl extends ServiceImpl<TDepartmentUserMapper, TDepartmentUser> implements ITDepartmentUserService {

}
