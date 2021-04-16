package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.TRole;
import com.qqkj.inspection.inspection.service.ITRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
@RestController
@RequestMapping("/inspection/t-role")
@Api(tags = {"角色表"})
public class TRoleController {

    @Autowired
    ITRoleService roleService;

    @GetMapping("getById")
    @ApiOperation(value = "根据id查询角色")
    public ResultVO getById(String id){
        return ResultVO.success(roleService.getById(id));
    }

    @GetMapping("list")
    @ApiOperation(value = "查询全部角色")
    public ResultVO list(){
        return ResultVO.success(roleService.list());
    }

    @GetMapping
    @ApiOperation(value = "根据条件查询角色")
    public ResultVO list(@ApiParam(value = "需要查询的角色名")@RequestParam(required = false) String name,
                         @ApiParam(value = "每一页数量")@RequestParam(required = false) Integer size,
                         @ApiParam(value = "当前所在页")@RequestParam(required = false)Integer current){
        Page<TRole> rolePage = new Page<>();
        rolePage.setSize(size).setCurrent(current);
        if (name!=null&&!name.equals("")){
            QueryWrapper<TRole> roleQueryWrapper= new QueryWrapper<>();
            roleQueryWrapper.like("role_name",name);
            return ResultVO.success(roleService.page(rolePage,roleQueryWrapper));
        }else {
            return ResultVO.success(roleService.page(rolePage));
        }
    }

    @PostMapping
    @ApiOperation(value = "添加一个角色")
    public ResultVO add( TRole role) {
        boolean flag = roleService.save(role);
        if (flag) {
            return ResultVO.success(role.getRoleId());
        }
        return ResultVO.error(50000, "增加角色失败");
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个角色")
    public ResultVO delete(String id) {
        return ResultVO.success(roleService.removeById(id));
    }

    @PutMapping
    @ApiOperation(value = "修改一个组的信息")
    public ResultVO put(TRole role) {
        return ResultVO.success(roleService.updateById(role));
    }


}
