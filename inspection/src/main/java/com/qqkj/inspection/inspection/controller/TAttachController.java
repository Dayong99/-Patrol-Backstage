package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.TAttach;
import com.qqkj.inspection.inspection.service.ITAttachService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 附件表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/inspection/t-attach")
@Api(tags = {"附件接口"})
public class TAttachController {
    @Autowired
    private ITAttachService attachService;

    @GetMapping
    @ApiOperation(value = "根据条件查询附件")
    public ResultVO list(@ApiParam(value = "需要查询的附件名")@RequestParam(required = false) String name,
                         @ApiParam(value = "每一页数量")@RequestParam(required = false) Integer size,
                         @ApiParam(value = "当前所在页")@RequestParam(required = false)Integer current){
        Page<TAttach> attachPage = new Page<>();
        attachPage.setSize(size).setCurrent(current);
        if (name!=null&&name!=""){
            QueryWrapper<TAttach> attachQueryWrapper= new QueryWrapper<>();
            attachQueryWrapper.like("filename",name);
            return ResultVO.success(attachService.page(attachPage,attachQueryWrapper));
        }else {
            return ResultVO.success(attachService.page(attachPage));
        }
    }

    @GetMapping("getById")
    @ApiOperation(value = "根据id查询附件")
    public ResultVO getById(String id){
        return ResultVO.success(attachService.getById(id));
    }

    @PostMapping
    @ApiOperation(value = "添加一个附件")
    public ResultVO add( TAttach attach) {

        boolean flag = attachService.save(attach);
        if (flag) {
            return ResultVO.success(attach.getId());
        }
        return ResultVO.error(50000, "增加附件失败");
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个附件")
    public ResultVO delete(String id) {
        return ResultVO.success(attachService.removeById(id));
    }

    @PutMapping
    @ApiOperation(value = "修改一个附件的信息")
    public ResultVO put(TAttach attach) {
        return ResultVO.success(attachService.updateById(attach));
    }

    @GetMapping("list")
    @ApiOperation(value = "查询全部附件")
    public ResultVO list(){
        return ResultVO.success(attachService.list());
    }

}
