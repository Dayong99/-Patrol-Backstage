package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.controller.BaseController;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.TDepartmentUser;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.service.ITDepartmentUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 部门成员表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-27
 */
@RestController
@RequestMapping("/inspection/t-department-user")
@Api(tags={"成员部门关系接口"})
public class TDepartmentUserController extends BaseController {
    @Autowired
    private ITDepartmentUserService departmentUserService;

    @PostMapping
    @ApiOperation(value ="添加一个部门成员关系信息")
    public ResultVO add(TDepartmentUser departmentUser){
        QueryWrapper<TDepartmentUser> departmentUserQueryWrapper = new QueryWrapper<>();
        departmentUserQueryWrapper.eq("user_id",departmentUser.getUserId());
        if (departmentUserService.getOne(departmentUserQueryWrapper)==null) {
            return ResultVO.success(departmentUserService.save(departmentUser));
        }else {
            return ResultVO.error(50000, "数据重复，添加数据失败");
        }
    }




    @PutMapping()
    @ApiOperation(value ="修改一个成员部门关系")
    public ResultVO put(TDepartmentUser departmentUser,String before) {

        UpdateWrapper<TDepartmentUser> departmentUserUpdateWrapper = new UpdateWrapper<>();
        departmentUserUpdateWrapper.eq("user_id", departmentUser.getUserId())
                .eq("department_id", before);
        departmentUserService.update(departmentUser,departmentUserUpdateWrapper);
        return ResultVO.success("ok");

    }

}
