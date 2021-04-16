package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.controller.BaseController;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.TDepartment;
import com.qqkj.inspection.inspection.entity.TDepartmentUser;
import com.qqkj.inspection.inspection.service.ITDepartmentService;
import com.qqkj.inspection.inspection.service.ITDepartmentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-27
 */
@RestController
@RequestMapping("/inspection/t-department")
@Api(tags={"部门接口"})
public class TDepartmentController extends BaseController {
    @Autowired
    private ITDepartmentService departmentService;

    @Autowired
    private ITDepartmentUserService departmentUserService;

    @GetMapping
    @ApiOperation(value = "根据条件查询部门")
    public ResultVO list(@ApiParam(value = "需要查询的部门名")@RequestParam(required = false) String name,
                         @ApiParam(value = "每一页数量")@RequestParam(required = false) Integer size,
                         @ApiParam(value = "当前所在页")@RequestParam(required = false)Integer current){
        Page<TDepartment> departmentPage = new Page<>();
        departmentPage.setSize(size).setCurrent(current);
        if (name!=null && !name.equals("")){
            QueryWrapper<TDepartment> departmentQueryWrapper= new QueryWrapper<>();
            departmentQueryWrapper.like("name",name);
            return ResultVO.success(departmentService.page(departmentPage,departmentQueryWrapper));
        }else {
            return ResultVO.success(departmentService.page(departmentPage));
        }
    }

    @GetMapping("getById")
    @ApiOperation("根据id查询部门")
    public ResultVO getById(Integer id){
        return ResultVO.success(departmentService.getById(id));
    }

    @GetMapping("list")
    @ApiOperation("查询全部部门")
    public ResultVO list(){
        return ResultVO.success(departmentService.list());
    }

    @PostMapping
    @ApiOperation("添加部门")
    public ResultVO add( TDepartment department) {
        QueryWrapper<TDepartment> departmentQueryWrapper= new QueryWrapper<>();
        departmentQueryWrapper.like("name",department.getName());
        TDepartment department1=departmentService.getOne(departmentQueryWrapper);
        if (department1!=null){
            return ResultVO.error(50000, "增加部门失败");
        }else {
            departmentService.save(department);
            return ResultVO.success(department.getId());
        }
    }

    @DeleteMapping
    @ApiOperation("删除一个部门")
    public ResultVO delete(Integer id) {
        QueryWrapper<TDepartmentUser> departmentUserQueryWrapper = new QueryWrapper<>();
        //删除部门关系中的数据
        departmentUserQueryWrapper.eq("department_id",id);
        departmentUserService.remove(departmentUserQueryWrapper);
        return ResultVO.success(departmentService.removeById(id));
    }

    @PutMapping
    @ApiOperation("修改一个部门")
    public ResultVO put(TDepartment department) {
        return ResultVO.success(departmentService.updateById(department));
    }

}
