package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.ITAttachService;
import com.qqkj.inspection.inspection.service.ITMaterialService;
import com.qqkj.inspection.inspection.service.ITPatrolUnitService;
import com.qqkj.inspection.inspection.service.impl.*;
import com.qqkj.inspection.inspection.until.Bzi2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * 立行立改及查立处表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@RestController
@RequestMapping("/inspection/lixing")
@Slf4j
@Api(tags = {"立行立改"})
public class TLixingController {
    @Autowired
    TLixingServiceImpl service;
    @Autowired
    TAttachServiceImpl attachService;
    @Autowired
    TMaterialServiceImpl materialService;
    @Value("${qqkj.fileDir}")
    private String fileDir;
    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    ITPatrolUnitService patrolUnitService;
    //创建
    @GetMapping("add_or_upd")
    @ApiOperation("添加或修改立行立改")
    public ResultVO addOrUpd(TLixing lixing){
        String pid=lixing.getPatrolId();
        if (pid==null){
            return ResultVO.error(800,"没有巡察任务id，patrounitlid");
        }
        QueryWrapper<TLixing>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("patrol_id",lixing.getPatrolId());
        TLixing tLixing=service.getOne(queryWrapper);
        boolean b=false;
        if (tLixing==null){
            b=service.save(lixing);
        }else {
            String id=lixing.getId();
            if (id==null){
                return ResultVO.error(800,"没有id,无法修改立行立改");
            }
            b=service.updateById(lixing);
        }
        return ResultVO.success(b);
    }

    @PostMapping("del")
    @ApiOperation("删除立行立改")
    public ResultVO del(String id){
       boolean b= service.removeById(id);
       QueryWrapper<TMaterial>queryWrapper=new QueryWrapper<>();
       queryWrapper.eq("parent_id",id);
       b=materialService.remove(queryWrapper);
       return  ResultVO.success(b);
    }

    @GetMapping("list")
    @ApiOperation("查询立行立改列表")
    public ResultVO list(int page,int num,Integer session, Integer year, Integer round, Integer groupName, String unitName,Integer lixingType){
        Page<TPatrolLixing>iPage=new Page<>(page,num);
       IPage<TPatrolLixing>lixingIPage= service.getList(iPage,session,year,round,groupName,unitName,lixingType);
        List<TPatrolLixing>lists=lixingIPage.getRecords();
        Map<String,Object>map=new HashMap<>();

        List<TPatrolLixing>l=new ArrayList<>();
        for (int i=0;i<lists.size();i++){
            System.out.println("立行立改 ："+lists.get(i));
            QueryWrapper<TLixing>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("patrol_id",lists.get(i).getPatrolunitid())
                        .eq("lixing_type",lixingType);
            TLixing tLixing=service.getOne(queryWrapper);
            if (tLixing!=null){
                lists.get(i).setLixing(tLixing);
                List<TMaterial> problemMaterials=materialService.getMaterial(tLixing.getId(),7);
                if (problemMaterials.size()!=0){
                    lists.get(i).setProblemMaterials(problemMaterials);
                }
                List<TMaterial>rectreportMaterials=materialService.getMaterial(tLixing.getId(),8);
                if (rectreportMaterials.size()!=0){
                    lists.get(i).setRectreportMaterials(rectreportMaterials);
                }
            }
            l.add(lists.get(i));
        }

        map.put("list",l);
        map.put("pages",lixingIPage.getPages());
        map.put("nums",lixingIPage.getTotal());
        return ResultVO.success(map);
    }

    /**
     * 导出 Excel
     */
    @GetMapping("export")
    @ApiOperation("批量导出Excel")
    public void export(HttpServletResponse response, Integer session, Integer year, Integer round, Integer groupName, String unitName,Integer lixingType)
            throws OilException {

//        response.setHeader("Content-disposition",
//                String.format("attachment; filename=\"%s\"", "problem.xlsx"));
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
//        response.setCharacterEncoding("UTF-8");
        List<String> fileAll = new ArrayList<>();
        try {
            List<TPatrolLixing>list=service.getList2(session,year,round,groupName,unitName,lixingType);
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
            cells.setCellValue("涉及问题数");

            cells = rows.createCell(6);
            cells.setCellValue("形成报告");
            int i = 1;
            for (TPatrolLixing patrol : list) {
                List<String>filePath=new ArrayList<>();
                int session2 = patrol.getSession();
                int year2 = patrol.getYear();
                int round2 = patrol.getRound();
                int group2 = patrol.getGroupName();
                String unitName2 = patrol.getUnitName();
                QueryWrapper<TLixing>queryWrapper=new QueryWrapper<>();
                queryWrapper.eq("patrol_id",patrol.getPatrolunitid());
                TLixing tLixing=service.getOne(queryWrapper);
                if (tLixing==null){
                    continue;
                }


                int problemNum=0;
                if (tLixing.getProblemNum()!=null){
                    problemNum=tLixing.getProblemNum();
                }
                String reportTime=" ";
                if (tLixing.getReportTime()!=null){
                   reportTime=tLixing.getReportTime().toString();
                }


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
                cells.setCellValue(problemNum);

                cells = rows.createCell(6);
                cells.setCellValue(reportTime);

                String path =session2 + "届_" + year2 + "年_" + round2 + "轮_" + group2 + "组_" +unitName2;

                List<TMaterial> problemMaterials=materialService.getMaterial(tLixing.getId(),7);
                List<TMaterial>rectreportMaterials=materialService.getMaterial(tLixing.getId(),8);

                if (problemMaterials.size()!=0){
                    for (TMaterial material:problemMaterials){
                        TAttach attachment = attachService.getById(material.getAttachId());
                        if (attachment!=null){
                            File file1 = new File(fileDir + attachment.getFilepath());
                            if (file1.exists()) {
                                filePath.add(file1.getPath());
                            }
                        }
                    }
                }

                if (rectreportMaterials.size()!=0){
                    for (TMaterial material:rectreportMaterials){
                        TAttach attachment = attachService.getById(material.getAttachId());
                        if (attachment!=null){
                            File file1 = new File(fileDir + attachment.getFilepath());
                            if (file1.exists()) {
                                filePath.add(file1.getPath());
                            }
                        }
                    }
                }

                int count = 0;
                if (filePath.size() != 0) {
                    try {
                        count = Bzi2.compress(filePath, fileDir + path + ".zip", false);
                    } catch (Exception ex) {
                        log.error("压缩失败",ex);
                    }
                }

                if (count != 0) {
                    fileAll.add(fileDir + path + ".zip");
                }

                log.info("成功压缩个数" + count);
            }

            String xlsxPath = fileDir + UUID.randomUUID().toString().replace("-", "") + ".xlsx";
            FileOutputStream fo = new FileOutputStream(new File(xlsxPath));
            wb.write(fo);
            wb.close();
            fo.close();
            fileAll.add(xlsxPath);
            String uid = UUID.randomUUID().toString().replace("-", "");
            int count = Bzi2.compress(fileAll, fileDir + uid + ".zip", false);
            for (String p : fileAll) {
                File file = new File(p);
                try {
                    file.delete();
                } catch (Exception e) {
                    log.error("删除失败", e);
                }
            }
            BufferedInputStream bu = null;
            BufferedOutputStream bo = null;
            response.setHeader("Content-type", "application/x-zip-compressed");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.setHeader("Content-Disposition", "attachment;fileName=" + uid + ".zip");
            byte[] bs = null;
            OutputStream ps = response.getOutputStream();
            //这句话的意思，使得放入流的数据是utf8格式
//            ps.write(data.getBytes("UTF-8"));
            FileInputStream in = new FileInputStream(fileDir + uid + ".zip");
            bu = new BufferedInputStream(in);
            OutputStream ou = response.getOutputStream();
            bo = new BufferedOutputStream(ou);
            int j = 0;
            byte[] bytes = new byte[10 * 1024];
            while ((j = in.read(bytes)) != -1) {
                bo.write(bytes, 0, j);
            }
            bo.flush();
            bu.close();
            ou.close();
            File file = new File(fileDir + uid + ".zip");
            file.delete();
        } catch (Exception e) {
            log.error("报错", e);
        }
    }


    @PostMapping("upload")
    @ApiOperation("上传立行立改文件")
    public ResultVO upload(MultipartFile file, @ApiParam("巡察任务id") String patrolunitid,
                           @ApiParam("立行立改id")String lixingid,@ApiParam("上传文件类型  立行立改问题:problem和整改情况报告:rectreport") String type){
        String id=new String();
        if (lixingid!=null && lixingid.length()!=0){
            TLixing lixing=service.getById(lixingid);
            if (lixing==null){
                return ResultVO.error(800,"没有查询到该立行立改数据");
            }else {
                id=lixingid;
            }
        }else {
            QueryWrapper<TLixing>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("patrol_id",patrolunitid);
            TLixing tLixing=service.getOne(queryWrapper);
            if (tLixing==null){
                tLixing=new TLixing();
                id=UUID.randomUUID().toString().replace("-","");
                tLixing.setId(id);
                tLixing.setPatrolId(patrolunitid);
                service.save(tLixing);
            }else {
                id=tLixing.getId();
            }
        }
        String contentType= file.getContentType();
        String fileName=file.getOriginalFilename();
        long size=file.getSize();
        boolean b=false;
        if (type.equals("problem")){
            String path=fileDir+"special/special/";
            File des=new File(path);
            if (!des.exists()){
                des.mkdirs();
            }
            try {
                file.transferTo(new File(path+fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            TAttach attach=new TAttach();
            String attachid=UUID.randomUUID().toString().replace("-","");
            attach.setId(attachid);
            attach.setFilepath(path.replace(fileDir,"")+fileName);
            attach.setContentType(contentType);
            attach.setFilename(fileName);
            attach.setFilesize(Math.toIntExact(size));
            b= attachService.save(attach);
            TMaterial material=new TMaterial();
            material.setFileName(fileName);
            material.setAttachId(attachid);
            material.setParentId(id);
            material.setFiletype(7);
            QueryWrapper<TMaterial>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("parent_id",id)
                    .eq("filetype",7);
            TMaterial m=materialService.getOne(queryWrapper);
            if (m==null){
                materialService.save(material);
            }else {
                m.setAttachId(attachid);
                m.setFileName(fileName);
                materialService.updateById(m);
            }
            log.info("成功移交立行立改问题 :"+material);
        }else if (type.equals("rectreport")){
            String path=fileDir+"special/special/";
            File des=new File(path);
            if (!des.exists()){
                des.mkdirs();
            }
            try {
                file.transferTo(new File(path+fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            TAttach attach=new TAttach();
            String attachid=UUID.randomUUID().toString().replace("-","");
            attach.setId(attachid);
            attach.setFilepath(path.replace(fileDir,"")+fileName);
            attach.setContentType(contentType);
            attach.setFilename(fileName);
            attach.setFilesize(Math.toIntExact(size));
            b= attachService.save(attach);
            TMaterial material=new TMaterial();
            material.setFileName(fileName);
            material.setAttachId(attachid);
            material.setParentId(id);
            material.setFiletype(8);
            QueryWrapper<TMaterial>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("parent_id",id)
                    .eq("filetype",8);
            TMaterial m=materialService.getOne(queryWrapper);
            if (m==null){
                materialService.save(material);
            }else {
                m.setAttachId(attachid);
                m.setFileName(fileName);
                materialService.updateById(m);
            }
            log.info("成功添加整改情况报告 :"+material);

        }

        return ResultVO.success(b);


    }



    @GetMapping("delfile")
    @ApiOperation(value = "删除文件")
    public ResultVO delfile(@ApiParam("material_id") String id,@ApiParam("attachid")String attachid){
        boolean b=false;
        b= materialService.removeById(id);
        b=attachService.removeById(attachid);
        return ResultVO.success(b);
    }

    @PostMapping("inserts_report")
    @ApiOperation("批量导入立行立改及立查立处问题")
    public ResultVO inserts(@RequestParam("file") MultipartFile file, Integer session, Integer year, Integer round, String groupname,Integer lixingType){
        String into=fileDir+"lixing/into/"+session+"/"+year+"/"+round+"/"+groupname+"/";
        String outDir="lixing/out/"+session+"/"+year+"/"+round+"/"+groupname+"/";
        File des=new File( into);
        if (!des.exists()){
            des.mkdirs();
        }
        File out=new File(fileDir+outDir);
        if (!out.exists()){
            out.mkdirs();
        }
        if (file.isEmpty()){
            return ResultVO.error(50000,"上传文件为空");
        }
        String filename=file.getOriginalFilename();
        if (filename.contains("zip") || filename.contains("rar")){
            try {
                file.transferTo(new File(into+filename));
                File f=new File(into+filename);
                List<File>list=new ArrayList<>();
                if (filename.contains("zip")){
                    list= Bzi2.unZipFiles(f,fileDir+outDir);
                }else {
                    list=Bzi2.unRarFile(f.getPath(),fileDir+outDir);
                }

                for (File file1:list){
                    if (file1.getName().contains("xls")){
                        inserts(file1.getPath(),lixingType);
                    }
                }
                for (File file1:list){
                    QueryWrapper<TPatrol>patrolQueryWrapper=new QueryWrapper<>();
                    boolean g=isInteger(groupname);
                    Integer group=null;
                    if (g==true){
                        group=Integer.parseInt(groupname);
                    }else {
                        group=chineseNumToArabicNum(groupname);
                    }
                    patrolQueryWrapper.eq("session",session)
                            .eq("year",year)
                            .eq("round",round)
                            .eq("group_name",group);
                    TPatrol patrol=patrolService.getOne(patrolQueryWrapper);
                    if (patrol!=null){
                        if (!file1.getName().contains("xls")){
                            String unitName=file1.getName().split("-")[0];
                            QueryWrapper<TUnit>queryWrapper=new QueryWrapper<>();
                            queryWrapper.eq("name",unitName);
                            TUnit unit=unitService.getOne(queryWrapper);
                            System.out.println("查询到单位"+unit);
                            if (unit!=null){

                                QueryWrapper<TPatrolUnit>queryWrapper1=new QueryWrapper<>();
                                queryWrapper1.eq("unit_id",unit.getId())
                                        .eq("patrol_id",patrol.getId());
                                TPatrolUnit patrolUnit=patrolUnitService.getOne(queryWrapper1);
                                QueryWrapper<TLixing>tReportQueryWrapper=new QueryWrapper<>();
                                tReportQueryWrapper.eq("patrol_id",patrolUnit.getId())
                                                   .eq("lixing_type",lixingType);
                                TLixing tReport=service.getOne(tReportQueryWrapper);
                                System.out.println("查询到报告表"+tReport);
                                if (tReport!=null){
                                    TAttach attach = new TAttach();
                                    String attachid = UUID.randomUUID().toString().replace("-", "");
                                    attach.setId(attachid)
                                            .setFilepath(outDir+file.getOriginalFilename().substring(0,file.getOriginalFilename().lastIndexOf("."))+"/"+file1.getName())
                                            .setContentType(Files.probeContentType(Paths.get(file1.getPath())))
                                            .setFilename(file1.getName())
                                            .setFilesize(Math.toIntExact(file1.length()));
                                    attachService.save(attach);
                                    TMaterial material = new TMaterial()
                                            .setFileName(file1.getName())
                                            .setAttachId(attachid)
                                            .setParentId(tReport.getId());
                                    if (file1.getName().contains("移交立行立改问题") || file1.getName().contains("移交立查立处问题")){
                                        material.setFiletype(7);
                                        TMaterial m= getOne(tReport.getId(),7);
                                        if (m!=null){
                                            m.setAttachId(attachid);
                                            materialService.updateById(m);
                                        }else{
                                            boolean b = materialService.save(material);
                                            if (b == true) {
                                                System.out.println("成功导入" + material);
                                            }
                                        }
                                    }else if (file1.getName().contains("整改情况报告")){
                                        material.setFiletype(8);
                                        TMaterial m= getOne(tReport.getId(),8);
                                        if (m!=null){
                                            m.setAttachId(attachid);
                                            materialService.updateById(m);
                                        }else{
                                            boolean b = materialService.save(material);
                                            if (b == true) {
                                                System.out.println("成功导入" + material);
                                            }
                                        }
                                    }
//                                    boolean b=materialService.save(material);
//                                    if (b==true){
//                                        System.out.println("成功导入"+material);
//                                    }
                                }

                            }



                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            return ResultVO.error(50000,"文件类型不对");
        }
        return ResultVO.success();
    }

    public TMaterial getOne(String id, Integer filetype) {
        QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
        tMaterialQueryWrapper.eq("parent_id", id).eq("filetype", filetype);
        //领导小组会报告
        TMaterial one1 = materialService.getOne(tMaterialQueryWrapper);
        return one1;
    }

    public boolean inserts(String reportPath,Integer lixingType){
        Workbook book;

        boolean b=false;
        File dest = new File(reportPath);
        try {
            book = Workbook.getWorkbook(dest);
            //获取所有工作簿
            Sheet[] sheets = book.getSheets();
            for (Sheet sheet:sheets){
                //工作簿行与列
                int rows = sheet.getRows();
                int clomns = sheet.getColumns();
                System.out.println("row:" + rows);
                System.out.println("clomns:" + clomns);
                for (int i=1;i<rows;i++){
                    Cell[] cells = sheet.getRow(i);
                    String sessionStr=cells[0].getContents().replace("届","").replace("第","");
                    b =isInteger(sessionStr);
                    Integer session=null;
                    if (b==true){
                        session=Integer.parseInt(sessionStr);
                    }else {
                        session=chineseNumToArabicNum(sessionStr);
                    }
                    String yearStr=cells[1].getContents().replace("年","").replace("第","");
                    b=isInteger(yearStr);
                    Integer year=null;
                    if (b==true){
                        year=Integer.parseInt(yearStr);
                    }else {
                        year=chineseNumToArabicNum(yearStr);
                    }
                    String roundStr=cells[2].getContents().replace("轮","").replace("第","");
                    b=isInteger(sessionStr);
                    Integer round=null;
                    if (b==true){
                        round=Integer.parseInt(roundStr);
                    }else {
                        round=chineseNumToArabicNum(roundStr);
                    }
                    for (int sc=0;sc<cells.length;sc++){
                        System.out.println(cells[sc].getContents());
                    }
                    String groupName=cells[3].getContents().replace("组","").replace("第","");
                    b=isInteger(groupName);
                    Integer group=null;
                    if (b==true){
                        group=Integer.parseInt(groupName);
                    }else {
                        group=chineseNumToArabicNum(groupName);
                    }


                    String unitlevel=cells[4].getContents();

                    String unitName=cells[5].getContents();
                    String shortName=cells[6].getContents();
                    String mobile=cells[7].getContents();
                    String user=cells[8].getContents();
                    String email=cells[9].getContents();

                    QueryWrapper<TUnit>queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("name",unitName);
                    TUnit tunit=unitService.getOne(queryWrapper);
                    System.out.println("xls里的单位"+tunit);
                    String unitid=new String();
                    if (tunit!=null){
                        unitid=tunit.getId();
                    }else {
                        unitid=UUID.randomUUID().toString().replace("-","");
                        TUnit u=new TUnit();
                        u.setName(unitName);
                        u.setSort(unitlevel);
                        u.setEmail(email);
                        u.setUser(user);
                        u.setMobile(mobile);
                        u.setShorter(shortName);
                        u.setId(unitid);
                        unitService.save(u);
                    }

                    TPatrol patrol=new TPatrol();
                    patrol.setRound(round);
                    patrol.setSession(session);
                    patrol.setGroupName(group);
                    patrol.setYear(year);
                    patrol.setEndTime(LocalDateTime.of(year,1,1,1,1,1));
                    patrol.setPatrolTime(LocalDateTime.of(year,1,1,1,1,1));
                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    patrolQueryWrapper
                            .eq("session",patrol.getSession())
                            .eq("year",patrol.getYear())
                            .eq("round",patrol.getRound())
                            .eq("group_name",patrol.getGroupName());
                    //查询是否有重复
                    TPatrol patrol1=patrolService.getOne(patrolQueryWrapper);
                    if (patrol1==null) {
                        patrol.setId(UUID.randomUUID().toString().replace("-", ""));
                        patrolService.save(patrol);
                    }
                    patrol1=patrolService.getOne(patrolQueryWrapper);
                    TPatrolUnit patrolUnit= addPatrol(patrol1,unitid);
                    TLixing lixing=new TLixing();
                    lixing.setId(UUID.randomUUID().toString().replace("-",""));
                    lixing.setProblemNum(Integer.valueOf(cells[10].getContents()));
                    lixing.setPatrolId(patrolUnit.getId());
                    lixing.setReportTime(LocalDateTime.of(year,1,1,1,1,1));
                    lixing.setLixingType(lixingType);
                    QueryWrapper<TLixing>queryWrapper1=new QueryWrapper<>();
                    queryWrapper1.eq("patrol_id",patrolUnit.getId())
                                 .eq("lixing_type",lixingType);
                    TLixing e=service.getOne(queryWrapper1);
                    if (e==null){
                        b= service.save(lixing);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }

    public TPatrolUnit addPatrol(TPatrol patrol,String untid){
        QueryWrapper<TPatrolUnit>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("unit_id",untid)
                .eq("patrol_id",patrol.getId());
        TPatrolUnit tPatrolUnit=patrolUnitService.getOne(queryWrapper);
        String patrolid=new String();
        if (tPatrolUnit==null) {
            System.out.println("没有查询到巡察任务 "+patrol);
            patrolid=UUID.randomUUID().toString().replace("-","");
            TPatrolUnit patrolUnit=new TPatrolUnit();
            patrolUnit.setId(patrolid);
            patrolUnit.setPatrolId(patrol.getId());
            patrolUnit.setUnitId(untid);
            patrolUnitService.save(patrolUnit);
//            //初始化report表
//            TReport tReport = new TReport().setPatrolId(patrolUnit.getId()).setCreatTime(patrol.getPatrolTime());
//            reportService.save(tReport);
//            //初始化problem表
//            TProblem problem = new TProblem().setPatrolId(patrol.getId()).setCreatTime(LocalDateTime.now());
//            problemService.save(problem);
//            //初始化special表
//            TSpecial special = new TSpecial().setPatrolId(patrol.getId());
//            service.save(special);
//            //初始化Lixing表
//            TLixing tLixing = new TLixing().setPatrolId(patrol.getId());
//            lixingService.save(tLixing);
//            //初始化tProposal表
//            TProposal tProposal = new TProposal().setPatrolId(patrol.getId());
//            proposalService.save(tProposal);
        }
//        else {
//            QueryWrapper<TReport>queryWrapper1=new QueryWrapper<>();
//            queryWrapper1.eq("patrol_id",tPatrolUnit.getId());
//            TReport report=reportService.getOne(queryWrapper1);
//            if (report==null){
//
//                TReport tReport = new TReport().setPatrolId(tPatrolUnit.getId()).setCreatTime(patrol.getPatrolTime());
//                reportService.save(tReport);
//            }
//        }
        tPatrolUnit=patrolUnitService.getOne(queryWrapper);
        return tPatrolUnit;
    }




    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
    static char[] cnArr = new char [] {'一','二','三','四','五','六','七','八','九'};
    static char[] chArr = new char [] {'十','百','千','万','亿'};
    static String allChineseNum = "零一二三四五六七八九十百千万亿";
    public static int chineseNumToArabicNum(String chineseNum) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        for (int i = 0; i < chineseNum.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNum.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNum.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }


}
