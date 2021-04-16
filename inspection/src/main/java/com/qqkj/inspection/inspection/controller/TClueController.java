package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.mapper.TClueMapper;
import com.qqkj.inspection.inspection.service.impl.TClueServiceImpl;
import com.qqkj.inspection.inspection.service.impl.TPatrolServiceImpl;
import com.qqkj.inspection.inspection.until.Bzi2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * <p>
 * 线索办理表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@RestController
@RequestMapping("/inspection/clue")
@Api("线索")
@Slf4j
public class TClueController {

    @Autowired
    TClueMapper mapper;
    public static void main(String[] args) {



    }



    @Autowired
    TClueServiceImpl service;
    @Autowired
    TPatrolServiceImpl patrolService;



//    @RequestMapping("test")
//    public ResultVO test(){
//        return  ResultVO.success(mapper.getMap());
//    }


    //创建线索
    @GetMapping("add")
    @ApiOperation("添加线索")
    public ResultVO add(TClue clue){
        if (clue.getPatrolId()==null){
            return ResultVO.error(800,"缺少巡察任务id,无法添加");
        }
       boolean b= service.save(clue);
        return ResultVO.success(b);
    }

    @GetMapping("upd")
    @ApiOperation("修改线索")
    public ResultVO upd(TClue clue){
        if (clue.getId()==null){
            return ResultVO.error(800,"确实线索id,无法修改");
        }
        boolean b=service.updateById(clue);
        return ResultVO.success(b);
    }

    @GetMapping("list")
    @ApiOperation("查询线索")
    public ResultVO list(int num, int page, @ApiParam("年") Integer year, @ApiParam("届次")Integer session
                         ,@ApiParam("轮次") Integer round,@ApiParam("组名") String groupName, @ApiParam("巡察对象名")String unitName
                         , @ApiParam("反应人职务")String reactionPost,@ApiParam("反应人级别") String reactionLevel,
                         @ApiParam("六项纪律")String discipline,@ApiParam("transferingUnit")String transferingUnit,
                         @ApiParam("办理情况")String situation, @ApiParam("是否结束:0未结束 1结束") Integer end
                         ,@ApiParam("二级分类")Integer twoCategory,@ApiParam("一级分类") Integer firstcategory,@ApiParam("具体问题模糊查询") String problem){
        Page<PatrolClue>cluePage=new Page<>(page,num);
        IPage<PatrolClue> iPage=service.getList(cluePage,session,year,round,groupName,unitName,discipline,end,problem);
        Map<String,Object>map=new HashMap<>();
        List<PatrolClue>list=iPage.getRecords();
        for (PatrolClue patrolClue:list){
            log.info(patrolClue.toString());
        }

        for (PatrolClue patrolClue:list){
           String patrolid= patrolClue.getPatrolunitid();
            List<TClue>clues=service.getClues(patrolid,reactionPost,reactionLevel,discipline,transferingUnit,
                                             situation,end,twoCategory,firstcategory);
            patrolClue.setClues(clues);
        }
        map.put("list",list);
        map.put("pages",iPage.getPages());
        map.put("total",iPage.getTotal());
        return ResultVO.success(map);
    }

    /**
     * 导出 Excel
     */
    @GetMapping("export")
    @ApiOperation("批量导出Excel")
    public void export(HttpServletResponse response, @ApiParam("年") Integer year, @ApiParam("届次")Integer session
            ,@ApiParam("轮次") Integer round,@ApiParam("组名") String groupName, @ApiParam("巡察对象名")String unitName
            , @ApiParam("反应人职务")String reactionPost,@ApiParam("反应人级别") String reactionLevel,
                       @ApiParam("六项纪律")String discipline,@ApiParam("transferingUnit")String transferingUnit,
                       @ApiParam("办理情况")String situation, @ApiParam("是否结束:0未结束 1结束") Integer end
            ,@ApiParam("二级分类")Integer twoCategory,@ApiParam("一级分类") Integer firstcategory)
            throws OilException {


        List<String> fileAll = new ArrayList<>();
        try {
            List<PatrolClue> list=service.getList2(session,year,round,groupName,unitName,discipline,end);

            Map<String,Object>map=new HashMap<>();

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet0");
            HSSFRow rows;
            HSSFCell cells;
            //设置表格第一行的列名
            // 获得表格第一行
            rows = sheet.createRow(0);
            // 根据需要给第一行每一列设置标题
            cells = rows.createCell(0);
            cells.setCellValue("届次");

            cells = rows.createCell(1);
            cells.setCellValue("年度");

            cells = rows.createCell(2);
            cells.setCellValue("轮次");

            cells = rows.createCell(3);
            cells.setCellValue("巡察组");

            cells = rows.createCell(4);
            cells.setCellValue("被巡察单位");

            cells = rows.createCell(5);
            cells.setCellValue("被反映人姓名");

            cells = rows.createCell(6);
            cells.setCellValue("被反映人职务");

            cells = rows.createCell(7);
            cells.setCellValue("被反应人级别");

            cells = rows.createCell(8);
            cells.setCellValue("具体问题");

            cells = rows.createCell(9);
            cells.setCellValue("六项纪律");

            cells = rows.createCell(10);
            cells.setCellValue("一级分类");
            cells = rows.createCell(11);
            cells.setCellValue("二级分类");

            int i = 1;
            for (PatrolClue patrol : list) {
                int session2 = patrol.getSession();
                int year2 = patrol.getYear();
                int round2 = patrol.getRound();
                int group2 = patrol.getGroupName();
                String unitName2 = patrol.getUnitName();
                String patrolid= patrol.getPatrolunitid();
                List<TClue>clues=service.getClues(patrolid,reactionPost,reactionLevel,discipline,transferingUnit,situation,end,twoCategory,firstcategory);
                for (TClue clue:clues){
                    rows = sheet.createRow(i);
                    i = i + 1;

                    System.out.println("************i:" + i);
                    cells = rows.createCell(0);
                    cells.setCellValue(session2);

                    cells = rows.createCell(1);
                    cells.setCellValue(year2);

                    cells = rows.createCell(2);
                    cells.setCellValue(round2);

                    cells = rows.createCell(3);
                    cells.setCellValue("第" + group2 + "组");

                    cells = rows.createCell(4);
                    cells.setCellValue(unitName2);

                    cells = rows.createCell(5);
                    cells.setCellValue(clue.getReactionName());

                    cells = rows.createCell(6);
                    cells.setCellValue(clue.getReactionPost());

                    cells = rows.createCell(7);
                    cells.setCellValue(clue.getReactionLevel());

                    cells = rows.createCell(8);
                    cells.setCellValue(clue.getProblem());

                    cells = rows.createCell(9);
                    cells.setCellValue(clue.getDiscipline());

                    cells = rows.createCell(10);
                    cells.setCellValue(clue.getFirstcategoryStr());

                    cells = rows.createCell(11);
                    cells.setCellValue(clue.getTwoCategoryStr());

                }
            }
            response.setHeader("Content-disposition",
                    String.format("attachment; filename=\"%s\"", UUID.randomUUID().toString().replace("-","")+".xlsx"));
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("UTF-8");
//            String xlsxPath = fileDir + UUID.randomUUID().toString().replace("-", "") + ".xlsx";
//            FileOutputStream fo = new FileOutputStream(new File(xlsxPath));
            wb.write(response.getOutputStream());
            wb.close();
//            fo.close();
        } catch (Exception e) {
            log.error("报错", e);
        }
    }





    @PostMapping("del")
    @ApiOperation("删除线索")
    public ResultVO del(String id){
      boolean b=  service.removeById(id);

      return ResultVO.success(b);
    }

//    @GetMapping("adds")
//    public ResultVO adds(MultipartFile file){
//        Workbook book;
//
//
//    }


    @RequestMapping("counts")
    public ResultVO getCounts(Integer session,Integer round,Integer firstid){
        return ResultVO.success(service.geunums(session,round,firstid));
    }
    //智能分析
    @RequestMapping("analysis")
    @ApiOperation("智能分析")
    public ResultVO analysis(Integer session,Integer year,Integer round,String unitName,String groupName,Integer lableid,String disciplineid){
        List<HashMap<String, Object>>maps=service.getMap(year,session,round,groupName,unitName,disciplineid,lableid);

        if (session!=null && round==null){
            QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("round")
                    .groupBy("round")
                    .orderByAsc("round");
            if (session!=null){
                queryWrapper.eq("session",session);
            }
            List<TPatrol> list = patrolService.list(queryWrapper);
            HashMap<String, Object>i=new HashMap<>();
            List<Integer>indes=new ArrayList<>();
            for (TPatrol patrol:list){
                indes.add(patrol.getRound());
            }
            i.put("rounds",indes);
            maps.add(i);

        }

        if (round!=null && session==null){
            QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("session")
                    .groupBy("session")
                    .orderByAsc("session");
            if (session!=null){
                queryWrapper.eq("round",round);
            }
            List<TPatrol> list = patrolService.list(queryWrapper);
            HashMap<String, Object>i=new HashMap<>();
            List<Integer>indes=new ArrayList<>();
            for (TPatrol patrol:list){
                indes.add(patrol.getSession());
            }
            i.put("sessions",indes);
            maps.add(i);

        }
        return ResultVO.success(maps);

    }

}
