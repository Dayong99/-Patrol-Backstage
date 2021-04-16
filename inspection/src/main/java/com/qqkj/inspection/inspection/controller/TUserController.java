package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.controller.BaseController;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.utils.MD5Util;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.ITDepartmentUserService;
import com.qqkj.inspection.inspection.service.ITUserService;
import com.qqkj.inspection.inspection.service.IUserRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/inspection/t-user")
@Api(tags = {"成员接口"})
public class TUserController extends BaseController {
    @Autowired
    private ITUserService userService;
    @Autowired
    private ITDepartmentUserService departmentUserService;
    @Autowired
    private IUserRoleService userRoleService;

    @GetMapping
    @ApiOperation("查询全部成员")
    public ResultVO list() {
        List<TUser> list = userService.list();
        for (TUser user : list) {
            user.setPassword("");
        }
        return ResultVO.success(list);
    }

    @GetMapping("getById")
    @ApiOperation("根据id查询")
    public ResultVO getById(Long id) {
        TUser user = userService.getById(id).setPassword("");
        return ResultVO.success(user);
    }

    @PutMapping("userPutPassword")
    @ApiOperation("普通用户修改密码")
    public ResultVO userPutPassword(@ApiParam(value = "用户id") @RequestParam Long id,
                                    @ApiParam(value = "修改之后的密码") @RequestParam String password,
                                    @ApiParam(value = "修改之前的密码") @RequestParam String beforePassword) {
        TUser user = userService.getById(id);
        if (user.getPassword().equals(MD5Util.encrypt(beforePassword))) {
            user.setPassword(MD5Util.encrypt(password));
            return ResultVO.success(userService.updateById(user));
        }
        return ResultVO.error(50000, "原密码错误");
    }

    @PutMapping("adminPutPassword")
    @ApiOperation("管理员修改密码")
    public ResultVO adminPutPassword(@ApiParam(value = "用户id") @RequestParam Long id,
                                     @ApiParam(value = "修改之后的密码") @RequestParam String password) {
        TUser user = new TUser().setPassword(MD5Util.encrypt(password)).setId(id);
        return ResultVO.success(userService.updateById(user));
    }

    @PostMapping
    @ApiOperation("添加成员")
    public ResultVO add(TUser user,
                        @ApiParam(value = "部门id") @RequestParam(required = false) Integer departmentId,
                        @ApiParam(value = "角色id") @RequestParam(required = false) Long roleId) {
        QueryWrapper<TUser> tUserQueryWrapper = new QueryWrapper<>();
        tUserQueryWrapper.eq("username",user.getUsername()).eq("name",user.getName());
        TUser one = userService.getOne(tUserQueryWrapper);
        if (one==null){
            user.setPassword(MD5Util.encrypt(user.getPassword())).setDatatime(LocalDateTime.now()).setState(0);
            userService.save(user);
            QueryWrapper<TDepartmentUser> departmentUserQueryWrapper = new QueryWrapper<>();
            departmentUserQueryWrapper.eq("user_id", user.getId())
                    .eq("department_id", departmentId);
            TDepartmentUser tDepartmentUser = new TDepartmentUser();
            tDepartmentUser.setDepartmentId(departmentId).setUserId((int) user.getId());
            //添加部门成员关系
            if (departmentUserService.getOne(departmentUserQueryWrapper) == null) {
                departmentUserService.save(tDepartmentUser);
            }
            //添加角色关系
            QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
            userRoleQueryWrapper.eq("user_id", user.getId())
                    .eq("role_id", roleId);
            UserRole userRole = new UserRole();
            userRole.setRoleId(roleId).setUserId(user.getId());
            if (userRoleService.getOne(userRoleQueryWrapper) == null) {
                userRoleService.save(userRole);
            }
            return ResultVO.success(user.getId());
        }
        return ResultVO.error(50000, "增加失败");
    }


    @DeleteMapping
    @ApiOperation("删除一个成员")
    public ResultVO delete(Long id) {
        //删除成员所在部门信息
        QueryWrapper<TDepartmentUser> departmentUserQueryWrapper = new QueryWrapper<>();
        departmentUserQueryWrapper.eq("user_id", id);
        departmentUserService.remove(departmentUserQueryWrapper);
        //删除成员角色信息
        QueryWrapper<UserRole> userRoleQueryWrapper = new QueryWrapper<>();
        userRoleQueryWrapper.eq("user_id", id);
        userRoleService.remove(userRoleQueryWrapper);
        return ResultVO.success(userService.removeById(id));
    }

    @PutMapping
    @ApiOperation("修改一个成员")
    public ResultVO put(TUser user,
                        @ApiParam(value = "修改之前部门id") @RequestParam(required = false) Integer beforeDepartmentId,
                        @ApiParam(value = "修改之后部门id") @RequestParam(required = false) Integer departmentId,
                        @ApiParam(value = "修改之前角色id") @RequestParam(required = false) Long beforeRoleId,
                        @ApiParam(value = "修改之后角色id") @RequestParam(required = false) Long roleId) {
        //修改部门关系的
        UpdateWrapper<TDepartmentUser> departmentUserUpdateWrapper = new UpdateWrapper<>();
        departmentUserUpdateWrapper.eq("user_id", user.getId())
                .eq("department_id", beforeDepartmentId);
        TDepartmentUser departmentUser = new TDepartmentUser().setUserId((int) user.getId()).setDepartmentId(departmentId);
        departmentUserService.update(departmentUser, departmentUserUpdateWrapper);
        //修改角色的
        UpdateWrapper<UserRole> userRoleUpdateWrapper = new UpdateWrapper<>();
        userRoleUpdateWrapper.eq("user_id", user.getId())
                .eq("role_id", beforeRoleId);
        UserRole userRole = new UserRole().setUserId(user.getId()).setRoleId(roleId);
        userRoleService.update(userRole, userRoleUpdateWrapper);

        return ResultVO.success(userService.updateById(user));
    }

    @GetMapping("queryDepartmentUser")
    @ApiOperation(value = "查询成员信息，以及所在的部门和分组 如果id为空name为空则查询全部")
    public ResultVO getDepartmentUser(@ApiParam(value = "根据id查询单个成员的信息和对应的部门分组信息") @RequestParam(required = false) Integer id,
                                      @ApiParam(value = "模糊查询条件") @RequestParam(required = false) String name,
                                      @ApiParam(value = "当前页数") @RequestParam Integer current,
                                      @ApiParam(value = "一页多少条数据") @RequestParam Integer size) {
        Page<UserDepartment> userGroupDepartmentPage = new Page<>();
        userGroupDepartmentPage.setSize(size).setCurrent(current);
        return ResultVO.success(userService.queryDepartmentUser(userGroupDepartmentPage, id, name));
    }

    @GetMapping("getRole")
    @ApiOperation(value = "查询用户权限，两个条件满足一个即可")
    public ResultVO getRole(@ApiParam(value = "根据id查询单个成员的信息和对应的部门分组信息") @RequestParam(required = false) Integer id,
                                      @ApiParam(value = "模糊查询条件") @RequestParam(required = false) String name) {

        return ResultVO.success(userService.getRoleId(name, id));
    }
}
