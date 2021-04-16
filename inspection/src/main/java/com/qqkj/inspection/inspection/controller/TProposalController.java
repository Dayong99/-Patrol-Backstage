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
import com.qqkj.inspection.inspection.service.ITProposalService;
import com.qqkj.inspection.inspection.service.impl.TPatrolServiceImpl;
import com.qqkj.inspection.inspection.service.impl.TUnitServiceImpl;
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
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p>
 * 移交问题建议表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@RestController
@RequestMapping("/inspection/t-proposal")
@Api(tags = {"移交问题建议表"})
@Slf4j
public class TProposalController {

    @Autowired
    private ITProposalService proposalService;
    @Autowired
    private ITAttachService attachService;
    @Autowired
    private ITMaterialService materialService;
    @Value("${qqkj.fileDir}")
    private String fileDir;

    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    ITPatrolUnitService patrolUnitService;

    @PostMapping
    @ApiOperation(value = "添加移交问题建议数据")
    public ResultVO add(@ApiParam(value = "一个移交问题建议") TProposal proposal) {
        QueryWrapper<TProposal> proposalQueryWrapper = new QueryWrapper<>();
        proposalQueryWrapper.eq("transfering_unit",proposal.getTransferingUnit())
                .eq("transfering_num",proposal.getTransferingNum());
        TProposal one = proposalService.getOne(proposalQueryWrapper);
        if (one==null){
            return ResultVO.success(proposal.getId());
        }
        return ResultVO.error(50000, "增加移交问题建议数据失败");
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个移交问题建议表")
    public ResultVO delete(@ApiParam(value = "主键id")@RequestParam String id) {
        QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
        tMaterialQueryWrapper.eq("parent_id",id).eq("filetype",9);
        List<TMaterial> list = materialService.list(tMaterialQueryWrapper);
        for(TMaterial material:list){
            attachService.removeById(material.getAttachId());
        }
        materialService.remove(tMaterialQueryWrapper);
        return ResultVO.success(proposalService.removeById(id));
    }

    @DeleteMapping("delFile")
    @ApiOperation(value = "删除文件")
    public ResultVO delFile(@ApiParam("material_id")@RequestParam String id,
                            @ApiParam("attachid") @RequestParam String attachid){
        boolean b=false;
        b= materialService.removeById(id);
        b=attachService.removeById(attachid);
        return ResultVO.success(b);
    }

    @PutMapping
    @ApiOperation(value = "修改一个移交问题建议表")
    public ResultVO put(TProposal proposal) {
        return ResultVO.success(proposalService.updateById(proposal));
    }

    @GetMapping
    @ApiOperation(value = "分页查询移交问题建议表")
    public ResultVO list( @ApiParam(value = "当前页数")@RequestParam Integer current,
                          @ApiParam(value = "一页多少条数据")@RequestParam Integer size,
                          @ApiParam(value = "年度")@RequestParam(required = false) Integer year,
                          @ApiParam(value = "届次")@RequestParam(required = false) Integer session,
                          @ApiParam(value = "轮次")@RequestParam(required = false) Integer round,
                          @ApiParam(value = "巡察组")@RequestParam(required = false) Integer groupName,
                          @ApiParam(value = "巡察单位")@RequestParam(required = false)String name,
                          @ApiParam(value = "具体办理情况")@RequestParam(required = false)String message,
                          @ApiParam(value = "成果运营情况")@RequestParam(required = false)String information){
        //分页
        Page<PatrolProposal> proposalPatrolPage=new Page<>();
        proposalPatrolPage.setCurrent(current).setSize(size);
        //查询是否已到期
        IPage<PatrolProposal> query = proposalService.getProposal(proposalPatrolPage,year,session,round,groupName,name,message,information);
        List<PatrolProposal> records = query.getRecords();
        for (PatrolProposal proposalPatrol:records){
            //移交问题建议
            QueryWrapper<TProposal> proposalQueryWrapper = new QueryWrapper<>();
            proposalQueryWrapper.eq("patrol_id",proposalPatrol.getPatrolunitid());
            TProposal proposal = proposalService.getOne(proposalQueryWrapper);
            proposalPatrol.setProposal(proposal);
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id",proposalPatrol.getProposal().getId()).eq("filetype",9);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            proposalPatrol.setMaterial(one);
            if (proposalPatrol.getProposal().getTransferingTime()!=null&&!proposalPatrol.getProposal().getTransferingTime().equals("")){
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zdt = proposalPatrol.getProposal().getTransferingTime().atZone(zoneId);
                //数据库的移交时间
                Date date = Date.from(zdt.toInstant());
                if (date.getTime()<new Date().getTime()){
                    proposalPatrol.getProposal().setRemind("已到期");
                }else{
                    //表示当前时间推移7天
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(new Date()); //需要将date数据转移到Calender对象中操作
                    calendar.add(Calendar.DATE, 7);
                    Date time = calendar.getTime();
                    if (date.getTime()<time.getTime()) {
                        proposalPatrol.getProposal().setRemind("即将到期");
                    } else {
                        proposalPatrol.getProposal().setRemind("未到期");
                    }
                }
            }
        }
        return  ResultVO.success(query);
    }

    @GetMapping("export")
    @ApiOperation(value = "批量导出文件")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "年度")@RequestParam(required = false) Integer year,
                       @ApiParam(value = "届次")@RequestParam(required = false) Integer session,
                       @ApiParam(value = "轮次")@RequestParam(required = false) Integer round,
                       @ApiParam(value = "巡察组")@RequestParam(required = false) Integer groupName,
                       @ApiParam(value = "巡察单位")@RequestParam(required = false)String name,
                       @ApiParam(value = "具体办理情况")@RequestParam(required = false)String message,
                       @ApiParam(value = "成果运营情况")@RequestParam(required = false)String information)
            throws OilException {

//        response.setHeader("Content-disposition",
//                String.format("attachment; filename=\"%s\"", "problem.xlsx"));
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
//        response.setCharacterEncoding("UTF-8");
        List<String> fileAll = new ArrayList<>();
        try {
            List<PatrolProposal>list=proposalService.getProposal2(year,session,round,groupName,name,message,information);
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
            cells.setCellValue("移交单位");

            cells = rows.createCell(6);
            cells.setCellValue("移交时间");

            cells = rows.createCell(7);
            cells.setCellValue("移交建议数");

            cells = rows.createCell(8);
            cells.setCellValue("具体办理情况");

            cells = rows.createCell(9);
            cells.setCellValue("成果运营情况");

            int i = 1;
            for (PatrolProposal proposalPatrol : list) {
                List<String>filePath=new ArrayList<>();
                int session2 = proposalPatrol.getSession();
                int year2 = proposalPatrol.getYear();
                int round2 = proposalPatrol.getRound();
                int group2 = proposalPatrol.getGroupName();
                String unitName2 = proposalPatrol.getUnitName();

                QueryWrapper<TProposal> proposalQueryWrapper = new QueryWrapper<>();
                proposalQueryWrapper.eq("patrol_id",proposalPatrol.getPatrolunitid());
                TProposal proposal = proposalService.getOne(proposalQueryWrapper);
                if (proposal==null){
                    continue;
                }

                if (proposal!=null){
                    String transunit=new String();
                    if (proposal.getTransferingUnit()!=null){
                        transunit=proposal.getTransferingUnit();
                    }

                    String trantime=new String();
                    if (proposal.getTransferingTime()!=null){
                        trantime=proposal.getTransferingTime().toString();
                    }
                    String num=new String();
                    if (proposal.getTransferingNum()!=null){
                        num=proposal.getTransferingNum().toString();
                    }
                    String message2=new String();
                    if (proposal.getMessage()!=null){
                        message2=proposal.getMessage();
                    }
                    String infomation2=new String();
                    if (proposal.getInformation()!=null){
                        infomation2=proposal.getInformation();
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
                    cells.setCellValue(transunit);

                    cells = rows.createCell(6);
                    cells.setCellValue(trantime);

                    cells = rows.createCell(7);
                    cells.setCellValue(num);

                    cells = rows.createCell(8);
                    cells.setCellValue(message2);

                    cells = rows.createCell(9);
                    cells.setCellValue(infomation2);
                    String path =session2 + "届_" + year2 + "年_" + round2 + "轮_" + group2 + "组_" +unitName2;
                    QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
                    tMaterialQueryWrapper.eq("parent_id",proposal.getId()).eq("filetype",9);
                    TMaterial one = materialService.getOne(tMaterialQueryWrapper);
                    if (one!=null){
                        TAttach attachment = attachService.getById(one.getAttachId());
                        if (attachment!=null){
                            File file1 = new File(fileDir + attachment.getFilepath());
                            if (file1.exists()) {
                                filePath.add(file1.getPath());
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
    @ApiOperation(value = "上传文件或者修改文件")
    public ResultVO upload(@ApiParam("移交具体问题建议")@RequestParam(required = false) MultipartFile file,
                           @ApiParam("移交问题建议表id")@RequestParam String proposalId) {
        int count=0;
        //添加或者修改移交具体问题建议
        if (file!=null&&!file.isEmpty()) {
            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            long size = file.getSize();
            String path = fileDir + "proposal/proposal/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id",proposalId).eq("filetype",9);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one==null){
                //添加移交具体问题建议
                log.info("移交具体问题建议");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count ++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(proposalId)
                        .setFiletype(9);
                materialService.save(material);
                log.info("移交具体问题建议 :" + material);
            }else {
                log.info("移交具体问题建议");
                TMaterial material = new TMaterial()
                        .setId(one.getId())
                        .setFileName(fileName);
                materialService.updateById(material);
                TAttach attach = new TAttach()
                        .setId(one.getAttachId())
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size))
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType);
                attachService.updateById(attach);
                count ++;
                log.info("成功修改移交具体问题建议 :" + material);
            }
        }
        return ResultVO.success("成功修改或添加"+count+"个附件");
    }

    @GetMapping("getAllAttach")
    @ApiOperation(value = "获取全部materialId")
    public ResultVO getAttach(){
        List<TProposal> proposalList=proposalService.list();
        ArrayList<String> list = new ArrayList<>();
        for (TProposal proposal: proposalList){
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id",proposal.getId()).eq("filetype",9);
            List<TMaterial> list1=materialService.list(tMaterialQueryWrapper);
            for (TMaterial material:list1){
                list.add(material.getId());
            }
        }
        return ResultVO.success(list);
    }

    @PostMapping("inserts_report")
    @ApiOperation("批量导入巡察报告")
    public ResultVO inserts(@RequestParam("file") MultipartFile file,Integer session,Integer year,Integer round,String groupname){
        String into=fileDir+"proposal/into/"+session+"/"+year+"/"+round+"/"+groupname+"/";
        String outDir="proposal/out/"+session+"/"+year+"/"+round+"/"+groupname+"/";
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
                        inserts(file1.getPath());
                    }
                }
                for (File file1:list){
                    boolean g=isInteger(groupname);
                    Integer group=null;
                    if (g==true){
                        group=Integer.parseInt(groupname);
                    }else {
                        group=chineseNumToArabicNum(groupname);
                    }
                    QueryWrapper<TPatrol>patrolQueryWrapper=new QueryWrapper<>();
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
                                QueryWrapper<TProposal>tReportQueryWrapper=new QueryWrapper<>();
                                tReportQueryWrapper.eq("patrol_id",patrolUnit.getId());
                                TProposal tReport=proposalService.getOne(tReportQueryWrapper);
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
                                    if (file1.getName().contains("移交问题具体建议")){
                                        material.setFiletype(9);
                                        TMaterial m= getOne(tReport.getId(),9);
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

    public boolean inserts(String reportPath){
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
                    TProposal proposal=new TProposal();
                    proposal.setPatrolId(patrolUnit.getId());
                    proposal.setId(UUID.randomUUID().toString().replace("-",""));
                    proposal.setTransferingTime(LocalDateTime.of(year,1,1,1,1,1));
                    proposal.setTransferingUnit(cells[10].getContents());
                    proposal.setTransferingNum(Integer.valueOf(cells[11].getContents()));
                    int its=1;
                    if (cells[12].getContents().contains("否")){
                        its=0;
                    }
                    proposal.setEnd(String.valueOf(its));
                    proposal.setMessage(cells[13].getContents());
                    proposal.setInformation(cells[14].getContents());
                    QueryWrapper<TProposal>queryWrapper1=new QueryWrapper<>();
                    queryWrapper1.eq("patrol_id",patrolUnit.getId());
                    TProposal e=proposalService.getOne(queryWrapper1);
                    if (e==null){
                        b= proposalService.save(proposal);

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
