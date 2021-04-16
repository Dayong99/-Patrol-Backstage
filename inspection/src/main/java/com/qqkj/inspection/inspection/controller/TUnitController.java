package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.*;
import com.qqkj.inspection.inspection.service.impl.TPatrolServiceImpl;
import com.qqkj.inspection.inspection.service.impl.TUnitServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡察对象表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/inspection/unit")
@Api(tags = {"巡察对象接口"})
public class TUnitController {
    @Autowired
    TUnitServiceImpl service;
    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    private ITProblemService problemService;
    @Autowired
    private ITMaterialService materialService;
    @Autowired
    private ITSpecialService specialService;
    @Autowired
    private ITLixingService lixingService;
    @Autowired
    private ITClueService clueService;
    @Autowired
    private ITProposalService proposalService;
    @Autowired
    private ITReportService reportService;
    @Autowired
    private ITAttachService attachService;
    @Autowired
    private ITPatrolUnitService patrolUnitService;

    //添加巡察对象
    @PostMapping
    @ApiOperation(value = "添加一个巡察对象")
    public ResultVO add(TUnit tUnit) {
        QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", tUnit.getName());
        TUnit unit = service.getOne(queryWrapper);
        if (unit == null) {
            service.save(tUnit);
            return ResultVO.success(tUnit.getId());
        } else {
            return ResultVO.error(50000, "该名称已存在");
        }
    }

    //删除巡察对象
    @DeleteMapping
    @ApiOperation(value = "删除巡察对象")
    public ResultVO del(@ApiParam(value = "巡察对象id") @RequestParam String id) {
        boolean b = service.removeById(id);
        QueryWrapper<TPatrolUnit> patrolUnitQueryWrapper = new QueryWrapper<>();
        patrolUnitQueryWrapper.eq("unit_id",id);
        List<TPatrolUnit> list = patrolUnitService.list(patrolUnitQueryWrapper);
        patrolUnitService.remove(patrolUnitQueryWrapper);
        for (TPatrolUnit patrolUnit:list){
            patrolService.removeById(patrolUnit.getPatrolId());
            //删除report表中的数据
            QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
            reportQueryWrapper.eq("patrol_id", patrolUnit.getId());
            TReport report = reportService.getOne(reportQueryWrapper);
            //查询material
            if (report!=null){
                QueryWrapper<TMaterial> materialQueryWrapper = new QueryWrapper<>();
                materialQueryWrapper.eq("parent_id",report.getId()).and(i->i.eq("filetype",1).or().eq("filetype",2).or().eq("filetype",3).or().eq("filetype",10).or().eq("filetype",11));
                List<TMaterial> list1 = materialService.list(materialQueryWrapper);
                for (TMaterial material:list1){
                    //删除附件
                    attachService.removeById(material.getAttachId());
                }
                //删除专题报告相关的数据
                materialService.remove(materialQueryWrapper);
            }
            reportService.remove(reportQueryWrapper);

            //删除problem表中数据
            QueryWrapper<TProblem> problemQueryWrapper = new QueryWrapper<>();
            problemQueryWrapper.eq("patrol_id", patrolUnit.getId());
            problemService.remove(problemQueryWrapper);

            //删除special表中数据
            QueryWrapper<TSpecial> specialQueryWrapper = new QueryWrapper<>();
            specialQueryWrapper.eq("patrol_id", patrolUnit.getId());
            TSpecial tSpecial = specialService.getOne(specialQueryWrapper);
            if (tSpecial!=null){
                QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
                tMaterialQueryWrapper.eq("parent_id", tSpecial.getId()).and(i->i.eq("filetype",4).or().eq("filetype",5).or().eq("filetype",6));
                //根据parent_id获取TMaterial数据
                List<TMaterial> materials = materialService.list(tMaterialQueryWrapper);
                for (TMaterial tMaterial : materials) {
                    //遍历，并删除附件
                    attachService.removeById(tMaterial.getAttachId());
                }
                //删除t_material表
                materialService.remove(tMaterialQueryWrapper);
            }
            specialService.remove(specialQueryWrapper);


            //删除Lixing表中的数据
            QueryWrapper<TLixing> lixingQueryWrapper = new QueryWrapper<>();
            lixingQueryWrapper.eq("patrol_id", patrolUnit.getId());
            TLixing tLixing = lixingService.getOne(lixingQueryWrapper);
            if (tLixing!=null){
                QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
                tMaterialQueryWrapper.eq("parent_id", tLixing.getId()).and(i->i.eq("filetype",7).or().eq("filetype",8));
                //根据parent_id获取TMaterial数据
                List<TMaterial> materials = materialService.list(tMaterialQueryWrapper);
                for (TMaterial tMaterial : materials) {
                    //遍历，并删除附件
                    attachService.removeById(tMaterial.getAttachId());
                }
                //删除t_material表
                materialService.remove(tMaterialQueryWrapper);
            }
            lixingService.remove(lixingQueryWrapper);

            //删除clue表中的数据
            QueryWrapper<TClue> tClueQueryWrapper = new QueryWrapper<>();
            tClueQueryWrapper.eq("patrol_id", patrolUnit.getId());
            clueService.remove(tClueQueryWrapper);

            //删除Proposal表中的数据
            QueryWrapper<TProposal> proposalQueryWrapper = new QueryWrapper<>();
            proposalQueryWrapper.eq("patrol_id", patrolUnit.getId());
            TProposal proposal = proposalService.getOne(proposalQueryWrapper);
            if (proposal!=null){
                QueryWrapper<TMaterial> tMaterialQueryWrapper1 = new QueryWrapper<>();
                tMaterialQueryWrapper1.eq("parent_id",proposal.getId()).eq("filetype",9);
                List<TMaterial> list1 = materialService.list(tMaterialQueryWrapper1);
                for(TMaterial material:list1){
                    attachService.removeById(material.getAttachId());
                }
                materialService.remove(tMaterialQueryWrapper1);
            }
            proposalService.remove(proposalQueryWrapper);


        }
        return ResultVO.success(b);
    }

    //查询巡察对象
    @GetMapping("list")
    @ApiOperation(value = "查询巡察对象")
    public ResultVO list(@ApiParam(value = "模糊查询对象名") @RequestParam(required = false) String name,
                         @ApiParam(value = "sort='村级'或者sort='区级'") @RequestParam(required = false) String sort,
                         int page,
                         int num) {
        Map<String, Object> map = new HashMap<>();
        IPage<TUnit> iPage = new Page(page, num);
        QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.equals("") ) {
            queryWrapper.like("name", name);
        }
        if (sort != null && !sort.equals("")) {
            queryWrapper.eq("sort", sort);
        }
        IPage<TUnit> page1 = service.page(iPage, queryWrapper);
        List<TUnit> list = page1.getRecords();
        map.put("list", list);
        long pages = page1.getPages();
        map.put("pages", pages);
        long nums = page1.getTotal();
        map.put("nums", nums);
        return ResultVO.success(map);
    }

    //修改巡察对象
    @PutMapping
    @ApiOperation(value = "更新巡察对象")
    public ResultVO updateTUnit(TUnit tUnit) {
        if (tUnit.getName()!=null&&!(tUnit.getName().equals(""))){
            QueryWrapper<TUnit> tUnitQueryWrapper = new QueryWrapper<>();
            tUnitQueryWrapper.eq("name", tUnit.getName());
            TUnit unit = service.getOne(tUnitQueryWrapper);
            if (unit == null) {
                service.updateById(tUnit);
                return ResultVO.success(tUnit.getId());
            } else {
                return ResultVO.error(50000, "该名称已存在");
            }
        }else{
            boolean b = service.updateById(tUnit);
            return ResultVO.success(b);
        }


    }
}
