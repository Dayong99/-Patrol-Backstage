package com.qqkj.inspection.inspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import javax.validation.constraints.Null;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 巡察表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/inspection/patrol")
@Api(tags = {"巡察表接口"})
public class TPatrolController {
    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    ITPatrolUnitService patrolUnitService;
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

    @PostMapping
    @ApiOperation(value = "创建巡察任务")
    public ResultVO add(TPatrol patrol,
                        @ApiParam(value = "巡察对象名称") @RequestParam(required = false) String unitName,
                        @ApiParam(value = "1、巡察报告2、巡察问题3、专题报告4、立行5、线索办理表6、移交问题建议表") @RequestParam(required = false) Integer type) {
        QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
        patrolQueryWrapper
                .eq("session", patrol.getSession())
                .eq("year", patrol.getYear())
                .eq("round", patrol.getRound())
                .eq("group_name", patrol.getGroupName());
        //查询是否有重复
        TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
        TPatrolUnit one1 = new TPatrolUnit();
        //判断是否需要修改patrolUnit关系表
        HashMap<String, String> map = new HashMap<>();
        if (patrol1 == null) {
            String id = UUID.randomUUID().toString().replace("-", "");
            patrol.setId(id);
            Boolean b = patrolService.save(patrol);
        }
        patrol1=patrolService.getOne(patrolQueryWrapper);

        if (unitName != null) {
            QueryWrapper<TUnit> tUnitQueryWrapper = new QueryWrapper<>();
            tUnitQueryWrapper.eq("name", unitName);
            TUnit one = unitService.getOne(tUnitQueryWrapper);
            //判断unit中是否有该对象
            if (one == null) {
                TUnit tUnit = new TUnit().setName(unitName);
                unitService.save(tUnit);
            } else {
                one.setName(unitName);
                unitService.updateById(one);
            }
            one = unitService.getOne(tUnitQueryWrapper);
            //没有unit对象就先创建
            QueryWrapper<TPatrolUnit> patrolUnitQueryWrapper = new QueryWrapper<>();
            patrolUnitQueryWrapper.eq("unit_id", one.getId())
                    .eq("patrol_id", patrol1.getId());
            one1=patrolUnitService.getOne(patrolUnitQueryWrapper);
            if (one1==null){
                TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol1.getId()).setUnitId(one.getId());
                patrolUnitService.save(patrolUnit);
            }
            one1=patrolUnitService.getOne(patrolUnitQueryWrapper);
            map.put("unitId", one.getId());
            map.put("patrolId", patrol1.getId());
            map.put("patrolUnitId", one1.getId());
        }
        if (type == null) {
            return ResultVO.success(map);
        }


        //查询报告表中的数据
        QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("patrol_id", one1.getId());
        TReport report = reportService.getOne(reportQueryWrapper);
        if (report != null) {
            map.put("reportId", report.getId());
        } else {
            if (type == 1) {
                TReport report1 = new TReport().setPatrolId(one1.getId()).setCreatTime(LocalDateTime.now());
                reportService.save(report1);
                map.put("reportId", report1.getId());
            }
        }
        //查询问题表中的数据
//                        QueryWrapper<TProblem> problemQueryWrapper = new QueryWrapper<>();
//                        problemQueryWrapper.eq("patrol_id",one1.getId())
//                        ;
//                        TProblem problem = problemService.getOne(problemQueryWrapper);
//                        if (problem!=null){
//                            map.put("problemId",problem.getId());
//                        }else{
        if (type == 2) {
            TProblem problem1 = new TProblem().setPatrolId(one1.getId()).setCreatTime(LocalDateTime.now());
            problemService.save(problem1);
            map.put("problemId", problem1.getId());
        }
//                        }

        //查询专题报告的数据
        QueryWrapper<TSpecial> specialQueryWrapper = new QueryWrapper<>();
        specialQueryWrapper.eq("patrol_id", one1.getId());
        TSpecial special = specialService.getOne(specialQueryWrapper);
        if (special != null) {
            map.put("specialId", special.getId());
        } else {
            if (type == 3) {
                TSpecial special1 = new TSpecial().setPatrolId(one1.getId());
                specialService.save(special1);
                map.put("specialId", special1.getId());
            }
        }
        //查询立行的数据
        QueryWrapper<TLixing> lixingQueryWrapper = new QueryWrapper<>();
        lixingQueryWrapper.eq("patrol_id", one1.getId());
        TLixing lixing = lixingService.getOne(lixingQueryWrapper);
        if (lixing != null) {
            map.put("lixingId", lixing.getId());
        } else {
            if (type == 4) {
                TLixing lixing1 = new TLixing().setPatrolId(one1.getId());
                lixingService.save(lixing1);
                map.put("lixingId", lixing1.getId());
            }
        }
        //查询线索办理
        QueryWrapper<TClue> clueQueryWrapper = new QueryWrapper<>();
        clueQueryWrapper.eq("patrol_id", one1.getId());
        TClue clue = clueService.getOne(clueQueryWrapper);
        if (clue != null) {
            map.put("clueId", clue.getId());
        } else {
            if (type == 5) {
                TClue clue1 = new TClue().setPatrolId(one1.getId())
                        .setEnd(0);
                clueService.save(clue1);
                map.put("clueId", clue1.getId());
            }
        }
        //查询移交问题建议数据
        QueryWrapper<TProposal> proposalQueryWrapper = new QueryWrapper<>();
        proposalQueryWrapper.eq("patrol_id", one1.getId());
        TProposal proposal = proposalService.getOne(proposalQueryWrapper);
        if (proposal != null) {
            map.put("proposalId", proposal.getId());
        } else {
            if (type == 6) {
                TProposal proposal1 = new TProposal().setPatrolId(one1.getId());
                proposalService.save(proposal1);
                map.put("proposalId", proposal1.getId());
            }
        }
            return ResultVO.success(map);

    }

    //删除巡察任务
    @DeleteMapping
    @ApiOperation(value = "删除巡察任务")
    public ResultVO del(@ApiParam(value = "删除主键") @RequestParam String id) {
        QueryWrapper<TPatrolUnit> tPatrolUnitQueryWrapper = new QueryWrapper<>();
        tPatrolUnitQueryWrapper.eq("patrol_id", id);
        List<TPatrolUnit> patrolUnitList = patrolUnitService.list(tPatrolUnitQueryWrapper);
        for (TPatrolUnit patrolUnit : patrolUnitList) {
            //删除report表中的数据
            QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
            reportQueryWrapper.eq("patrol_id", patrolUnit.getId());
            TReport report = reportService.getOne(reportQueryWrapper);
            //查询material
            if (report != null) {
                QueryWrapper<TMaterial> materialQueryWrapper = new QueryWrapper<>();
                materialQueryWrapper.eq("parent_id", report.getId()).and(i -> i.eq("filetype", 1).or().eq("filetype", 2).or().eq("filetype", 3).or().eq("filetype", 10).or().eq("filetype", 11));
                List<TMaterial> list = materialService.list(materialQueryWrapper);
                for (TMaterial material : list) {
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
            if (tSpecial != null) {
                QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
                tMaterialQueryWrapper.eq("parent_id", tSpecial.getId()).and(i -> i.eq("filetype", 4).or().eq("filetype", 5).or().eq("filetype", 6));
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
            if (tLixing != null) {
                QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
                tMaterialQueryWrapper.eq("parent_id", tLixing.getId()).and(i -> i.eq("filetype", 7).or().eq("filetype", 8));
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
            if (proposal != null) {
                QueryWrapper<TMaterial> tMaterialQueryWrapper1 = new QueryWrapper<>();
                tMaterialQueryWrapper1.eq("parent_id", proposal.getId()).eq("filetype", 9);
                List<TMaterial> list1 = materialService.list(tMaterialQueryWrapper1);
                for (TMaterial material : list1) {
                    attachService.removeById(material.getAttachId());
                }
                materialService.remove(tMaterialQueryWrapper1);
            }
            proposalService.remove(proposalQueryWrapper);

        }
        patrolUnitService.remove(tPatrolUnitQueryWrapper);
        boolean b = patrolService.removeById(id);
        return ResultVO.success(b);
    }

    //修改巡察任务
    @PutMapping
    @ApiOperation(value = "修改巡察任务")
    public ResultVO upd(TPatrol patrol) {
        if (patrol.getSession() != null || patrol.getYear() != null || patrol.getRound() != null || patrol.getGroupName() != null) {
            QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
            TPatrol tPatrol = patrolService.getById(patrol.getId());
            patrolQueryWrapper
                    .eq("session", patrol.getSession() == null ? tPatrol.getSession() : patrol.getSession())
                    .eq("year", patrol.getYear() == null ? tPatrol.getYear() : patrol.getYear())
                    .eq("round", patrol.getRound() == null ? tPatrol.getRound() : patrol.getRound())
                    .eq("group_name", patrol.getGroupName() == null ? tPatrol.getGroupName() : patrol.getGroupName());
            //查询是否有重复
            TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
            if (patrol1 == null) {
                Boolean b = patrolService.updateById(patrol);
                return ResultVO.success(b);
            } else {
                return ResultVO.error(50000, "数据关系重复");
            }
        } else {
            return ResultVO.success(patrolService.updateById(patrol));
        }

    }

    @PutMapping("updateGroup")
    @ApiOperation(value = "修改巡察组和巡察单位")
    public ResultVO update(TPatrol patrol,
                           @ApiParam(value = "巡察对象名称") @RequestParam(required = false) String unitName,
                           @ApiParam(value = "巡察对象关系表id") @RequestParam(required = false) String patrolUnitId) {
        QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
        TPatrol tPatrol = patrolService.getById(patrol.getId());
        patrolQueryWrapper
                .eq("session", patrol.getSession() == null ? tPatrol.getSession() : patrol.getSession())
                .eq("year", patrol.getYear() == null ? tPatrol.getYear() : patrol.getYear())
                .eq("round", patrol.getRound() == null ? tPatrol.getRound() : patrol.getRound())
                .eq("group_name", patrol.getGroupName() == null ? tPatrol.getGroupName() : patrol.getGroupName());
        //查询是否有重复
        TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
        if (patrol1 == null) {
            patrol.setId("");
            patrolService.save(patrol);
        }
        QueryWrapper<TUnit> tUnitQueryWrapper = new QueryWrapper<>();
        tUnitQueryWrapper.eq("name", unitName);
        TUnit unit = unitService.getOne(tUnitQueryWrapper);
        TPatrolUnit patrolUnit = new TPatrolUnit().setId(patrolUnitId).setPatrolId(patrol1 == null ? patrol.getId() : patrol1.getId()).setUnitId(unit.getId());
        Boolean b = patrolUnitService.updateById(patrolUnit);
        return ResultVO.success(b);
    }


    @GetMapping("getById")
    @ApiOperation(value = "根据id查询巡察任务")
    public ResultVO getById(@ApiParam(value = "巡察id") @RequestParam String id) {
        return ResultVO.success(patrolService.getById(id));
    }

    @GetMapping("getList")
    @ApiOperation(value = "查询全部巡察任务")
    public ResultVO getList() {
        return ResultVO.success(patrolService.list());
    }


    @GetMapping("getPatrolUnit")
    @ApiOperation(value = "查询全部巡察任务和对象")
    public ResultVO getPatrolUnit(@ApiParam(value = "当前页数") @RequestParam Integer current,
                                  @ApiParam(value = "一页多少条数据") @RequestParam Integer size,
                                  @ApiParam(value = "年度") @RequestParam(required = false) Integer year,
                                  @ApiParam(value = "届次") @RequestParam(required = false) Integer session,
                                  @ApiParam(value = "轮次") @RequestParam(required = false) Integer round,
                                  @ApiParam(value = "巡察单位") @RequestParam(required = false) String name,
                                  @ApiParam(value = "巡察组") @RequestParam(required = false) Integer groupName,
                                  @ApiParam(value = "村级/区级") @RequestParam(required = false) String sort) {
        Page<Patrol> patrolPage = new Page<>();
        patrolPage.setCurrent(current).setSize(size);
        QueryWrapper<Patrol> patrolQueryWrapper = new QueryWrapper<>();
        if (session != null) {
            patrolQueryWrapper.eq("session", session);
        }
        if (year != null) {
            patrolQueryWrapper.eq("year", year);
        }
        if (round != null) {
            patrolQueryWrapper.eq("round", round);
        }
        if (name != null && !name.equals("")) {
            patrolQueryWrapper.like("u.name", name);
        }
        if (sort != null && !sort.equals("")) {
            patrolQueryWrapper.eq("sort", sort);
        }
        if (groupName != null) {
            patrolQueryWrapper.eq("group_name", groupName);
        }
        return ResultVO.success(patrolService.queryPatrol(patrolPage, patrolQueryWrapper));
    }

    @GetMapping("getSession")
    @ApiOperation(value = "获取届次")
    public ResultVO getSession() {
        QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("session")
                .groupBy("session");
        List<TPatrol> list = patrolService.list(queryWrapper);
        return ResultVO.success(list);
    }

    @GetMapping("getYear")
    @ApiOperation(value = "获取年份")
    public ResultVO getYear() {
        QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("year")
                .groupBy("year");
        List<TPatrol> list = patrolService.list(queryWrapper);
        return ResultVO.success(list);
    }

    @GetMapping("getRound")
    @ApiOperation(value = "获取轮次")
    public ResultVO getRound(@ApiParam(value = "当前届次") @RequestParam(required = false) Integer session) {
        QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("round")
                .groupBy("round");
        if (session != null) {
            queryWrapper.eq("session", session);
        }
        List<TPatrol> list = patrolService.list(queryWrapper);
        return ResultVO.success(list);
    }

    @GetMapping("getUnit")
    @ApiOperation(value = "获取巡察对象")
    public ResultVO getUnit(@ApiParam(value = "村级/区级") @RequestParam(required = false) String sort) {
        QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,name,sort")

                .groupBy("name");
        if (sort != null && !sort.equals("")) {
            queryWrapper.eq("sort", sort);
        }
        List<TUnit> list = unitService.list(queryWrapper);
        return ResultVO.success(list);
    }

    @GetMapping("getGroupName")
    @ApiOperation(value = "获取组名")
    public ResultVO getGroupName(Integer session, Integer round) {
        QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "group_name")
                .groupBy("group_name");
        if (session != null && round != null) {
            queryWrapper.eq("session", session).eq("round", round);
        }
        List<TPatrol> list = patrolService.list(queryWrapper);
        return ResultVO.success(list);
    }

    public HashMap addAll(HashMap map, String id) {
        map.put("patrolUnitId", id);
        //初始化report表
        TReport tReport = new TReport().setPatrolId(id).setCreatTime(LocalDateTime.now());
        reportService.save(tReport);
        map.put("reportId", tReport.getId());
        //初始化problem表
        TProblem problem = new TProblem().setPatrolId(id).setCreatTime(LocalDateTime.now());
        problemService.save(problem);
        map.put("problemId", problem.getId());
        //初始化special表
        TSpecial special = new TSpecial().setPatrolId(id);
        specialService.save(special);
        map.put("specialId", special.getId());
        //初始化Lixing表
        TLixing lixing = new TLixing().setPatrolId(id);
        lixingService.save(lixing);
        map.put("lixingId", lixing.getId());
        //初始化tClue表
        TClue tClue = new TClue().setPatrolId(id);
        clueService.save(tClue);
        map.put("tCluelId", tClue.getId());
        //初始化tProposal表
        TProposal tProposal = new TProposal().setPatrolId(id);
        proposalService.save(tProposal);
        map.put("tProposalId", tProposal.getId());
        return map;
    }
}
