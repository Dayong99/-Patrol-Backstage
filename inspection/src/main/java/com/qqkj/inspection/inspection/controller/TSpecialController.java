package com.qqkj.inspection.inspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.common.utils.TimeUtil;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.*;
import com.qqkj.inspection.inspection.service.impl.*;
import com.qqkj.inspection.inspection.until.Bzi2;
import io.micrometer.core.instrument.util.TimeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;

import com.qqkj.inspection.common.dto.ResultVO;
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
 * 专题报告表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-12-01
 */
@RestController
@RequestMapping("/inspection/special")
@Slf4j
@Api(tags = {"专题报告"})
public class TSpecialController {

    @Autowired
    TSpecialServiceImpl service;
    @Autowired
    TPatrolServiceImpl patrolService;
    @Value("${qqkj.fileDir}")
    private String fileDir;
    @Autowired
    TAttachServiceImpl attachService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    TMaterialServiceImpl materialService;
    @Autowired
    private ITLixingService lixingService;
    @Autowired
    private ITClueService clueService;
    @Autowired
    private ITProposalService proposalService;
    @Autowired
    private ITReportService reportService;
    @Autowired
    private ITProblemService problemService;
    @Autowired
    private TLabelServiceImpl labelService;
    @Autowired
    private TPatrolUnitServiceImpl patrolUnitService;

    static char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
    static char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
    static String allChineseNum = "零一二三四五六七八九十百千万亿";


    //添加巡察报告
//    @GetMapping("add")
//    @ApiOperation(value = "添加专题报告")
//    public ResultVO add(TSpecial tSpecial){
//        boolean b=service.save(tSpecial);
//        return ResultVO.success(b);
//    }
//    //创建巡察任务及报告
//    @GetMapping("addPatrolService")
    //删除专题报告
    @PostMapping("del")
    @ApiOperation(value = "删除专题报告")
    public ResultVO del(String id) {
        boolean b = service.removeById(id);
        QueryWrapper<TMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        b = materialService.remove(queryWrapper);
        return ResultVO.success(b);
    }

    //更新专题报告时间
    @GetMapping("add_or_upd")
    @ApiOperation(value = "添加或更新专题报告")
    public ResultVO upd(TSpecial tSpecial) {
        String pid = tSpecial.getPatrolId();
        if (pid == null) {
            return ResultVO.error(800, "没有巡察任务id，patrol_unit_id");
        }
        QueryWrapper<TSpecial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("patrol_id", tSpecial.getPatrolId());
        TSpecial tSpecial1 = service.getOne(queryWrapper);
        boolean b = false;
        if (tSpecial1 == null) {
            b = service.save(tSpecial);
        } else {
            String id = tSpecial.getId();
            if (id == null) {
                return ResultVO.error(800, "没有id，无法修改专题报告");
            }
            b = service.updateById(tSpecial);
        }
        return ResultVO.success(b);
    }


    //查询所有专题报告
    @GetMapping("list")
    @ApiOperation(value = "查询专题报告")
    public ResultVO list(int page, int num, Integer session, Integer year, Integer round, String groupName, String unitName, String department) {
        Page<PatrolSpecial> iPage = new Page<>(page, num);
        IPage<PatrolSpecial> specialIPage = patrolService.getList(iPage, session, year, round, groupName, unitName, department);
        List<PatrolSpecial> list = specialIPage.getRecords();
        for (PatrolSpecial patrolSpecial : list) {
            QueryWrapper<TSpecial> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("patrol_id", patrolSpecial.getPatrolunitid());
            if (department != null && department.length() != 0) {
                queryWrapper.eq("uder_department", department);
            }
            TSpecial tSpecial = service.getOne(queryWrapper);
            if (tSpecial != null) {
                List<TMaterial> specialMaterials = materialService.getMaterial(tSpecial.getId(), 4);
                if (specialMaterials.size() != 0) {
                    patrolSpecial.setSpecialMaterials(specialMaterials);
                }
                List<TMaterial> leaderMaterials = materialService.getMaterial(tSpecial.getId(), 5);
                if (leaderMaterials.size() != 0) {
                    patrolSpecial.setLeaderMaterials(leaderMaterials);
                }

                List<TMaterial> materials = materialService.getMaterial(tSpecial.getId(), 6);
                if (materials.size() != 0) {
                    patrolSpecial.setMaterials(materials);
                }
                patrolSpecial.setSpecial(tSpecial);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("pages", specialIPage.getPages());
        map.put("nums", specialIPage.getTotal());
        return ResultVO.success(map);
    }


    /**
     * 导出 Excel
     */
    @GetMapping("export")
    @ApiOperation("批量导出Excel")
    public void export(HttpServletResponse response, Integer session, Integer year, Integer round, String groupName, String unitName, String department)
            throws OilException {

//        response.setHeader("Content-disposition",
//                String.format("attachment; filename=\"%s\"", "problem.xlsx"));
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
//        response.setCharacterEncoding("UTF-8");
        List<String> fileAll = new ArrayList<>();
        try {
            List<PatrolSpecial> list = patrolService.getList2(session, year, round, groupName, unitName, department);
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
            cells.setCellValue("问题涉及单位");

            cells = rows.createCell(5);
            cells.setCellValue("承办部门");

            cells = rows.createCell(6);
            cells.setCellValue("移交时间");

            cells = rows.createCell(7);
            cells.setCellValue("办理期限");

            int i = 1;
            for (PatrolSpecial patrol : list) {
                List<String>filePath=new ArrayList<>();
                int session2 = patrol.getSession();
                int year2 = patrol.getYear();
                int round2 = patrol.getRound();
                int group2 = patrol.getGroupName();
                String unitName2 = patrol.getUnitName();
                QueryWrapper<TSpecial> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("patrol_id", patrol.getPatrolunitid());
                if (department != null && department.length() != 0) {
                    queryWrapper.eq("uder_department", department);
                }
                TSpecial special = service.getOne(queryWrapper);
                if (special==null){
                   continue;
                }
                String department2=special.getUderDepartment();
                String handoverTime=" ";
                if (special.getHandoverTime()!=null){
                    handoverTime=special.getHandoverTime().toString();
                }
                String endTime=" ";
                if (special.getEndtime()!=null){
                    endTime=special.getEndtime().toString();
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
                cells.setCellValue(department2);

                cells = rows.createCell(6);
                cells.setCellValue(handoverTime);

                cells = rows.createCell(7);
                cells.setCellValue(endTime);

                String path = department2+"_"+session2 + "届_" + year2 + "年_" + round2 + "轮_" + group2 + "组_" +unitName2;

                List<TMaterial> specilsM = materialService.getMaterial(special.getId(), 4);

                List<TMaterial> leadersM = materialService.getMaterial(special.getId(), 5);
                List<TMaterial>materials = materialService.getMaterial(special.getId(), 6);

                if (specilsM.size()!=0){
                    for (TMaterial material:specilsM){
                        TAttach attachment = attachService.getById(material.getAttachId());
                        if (attachment!=null){
                            File file1 = new File(fileDir + attachment.getFilepath());
                            if (file1.exists()) {
                                filePath.add(file1.getPath());
                            }
                        }
                    }
                }

                if (leadersM.size()!=0){
                    for (TMaterial material:leadersM){
                        TAttach attachment = attachService.getById(material.getAttachId());
                        if (attachment!=null){
                            File file1 = new File(fileDir + attachment.getFilepath());
                            if (file1.exists()) {
                                filePath.add(file1.getPath());
                            }
                        }
                    }
                }

                if (materials.size()!=0){
                    for (TMaterial material:materials){
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


    @GetMapping("delfile")
    @ApiOperation(value = "删除文件")
    public ResultVO delfile(@ApiParam("material_id") String id, @ApiParam("attachid") String attachid) {
        boolean b = false;
        b = materialService.removeById(id);
        b = attachService.removeById(attachid);
        return ResultVO.success(b);
    }

    //上传文件
    @PostMapping("upload")
    @ApiOperation(value = "上传文件")
    public ResultVO upload(MultipartFile file, @ApiParam("巡察任务id") String patrolunitid, @ApiParam("tSpecial对象id") String specialid, @ApiParam("上传文件类型special,leader,material") String type) {
        boolean b = false;
        String id = new String();
        if (!file.isEmpty()) {
            if (specialid != null && specialid.length() != 0) {
                TSpecial tSpecial = service.getById(specialid);
                if (tSpecial == null) {
                    tSpecial = new TSpecial();
                    id = UUID.randomUUID().toString().replace("-", "");
                    tSpecial.setId(id);
                    tSpecial.setPatrolId(patrolunitid);
                    service.save(tSpecial);
                } else {
                    id = tSpecial.getId();
                }
            } else {
                QueryWrapper<TSpecial> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("patrol_id", patrolunitid);
                TSpecial special = service.getOne(queryWrapper);
                if (special == null) {
                    special = new TSpecial();
                    id = UUID.randomUUID().toString().replace("-", "");
                    special.setId(id);
                    special.setPatrolId(patrolunitid);
                    service.save(special);
                } else {

                    id = special.getId();
                }

            }

            String contentType = file.getContentType();
            String fileName = file.getOriginalFilename();
            long size = file.getSize();
            if (type.equals("special")) {
                String path = fileDir + "special/special/";
                File des = new File(path);
                if (!des.exists()) {
                    des.mkdirs();
                }
                try {
                    file.transferTo(new File(path + fileName));
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid);
                attach.setFilepath(path.replace(fileDir, "") + fileName);
                attach.setContentType(contentType);
                attach.setFilename(fileName);
                attach.setFilesize(Math.toIntExact(size));
                b = attachService.save(attach);
                TMaterial material = new TMaterial();
                material.setFileName(fileName);
                material.setAttachId(attachid);
                material.setParentId(id);
                material.setFiletype(4);
                QueryWrapper<TMaterial> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("parent_id", id)
                        .eq("filetype", 4);
                TMaterial m = materialService.getOne(queryWrapper);
                if (m == null) {
                    materialService.save(material);
                } else {
                    m.setAttachId(attachid);
                    m.setFileName(fileName);
                    materialService.updateById(m);
                }

                log.info("成功添加专题报告 :" + material);
            } else if (type.equals("leader")) {
                log.info("领导批示");
                String path = fileDir + "special/leader/";
                File des = new File(path);
                if (!des.exists()) {
                    des.mkdirs();
                }
                try {
                    file.transferTo(new File(path + fileName));
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }

                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid);
                attach.setContentType(contentType);
                attach.setFilename(fileName);
                attach.setFilepath(path.replace(fileDir, "") + fileName);
                attach.setFilesize(Math.toIntExact(size));
                b = attachService.save(attach);
                TMaterial material = new TMaterial();
                material.setFileName(fileName);
                material.setAttachId(attachid);
                material.setParentId(id);
                material.setFiletype(5);
                QueryWrapper<TMaterial> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("parent_id", id)
                        .eq("filetype", 5);
                TMaterial m = materialService.getOne(queryWrapper);
                if (m == null) {
                    materialService.save(material);
                } else {
                    m.setAttachId(attachid);
                    m.setFileName(fileName);
                    materialService.updateById(m);
                }
                log.info("成功添加领导批示 :" + material);
            } else if (type.equals("material")) {
                String path = fileDir + "special/special/";
                File des = new File(path);
                if (!des.exists()) {
                    des.mkdirs();
                }
                try {
                    file.transferTo(new File(path + fileName));
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }

                log.info("办结材料");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid);
                attach.setContentType(contentType);
                attach.setFilename(fileName);
                attach.setFilepath(path.replace(fileDir, "") + fileName);
                attach.setFilesize(Math.toIntExact(size));
                TMaterial material = new TMaterial();
                material.setParentId(id);
                material.setAttachId(attachid);
                material.setFileName(fileName);
                material.setFiletype(6);
                b = materialService.save(material);
                log.info("成功添加办结材料 :" + material);
            } else {
                return ResultVO.error(800, "没有查询巡察报告数据,id:" + id);
            }
        } else {
            return ResultVO.error(800, "上传文件为空");
        }
        return ResultVO.success(b);
    }

    @PostMapping("inserts_special")
    public ResultVO inserts_special(@RequestParam("file") MultipartFile file) {
        Workbook book;
        String fileName = file.getOriginalFilename();
        String filePath = fileDir + "Clue/" + LocalDateTime.now().getYear() + "/" + LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/";

        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            book = Workbook.getWorkbook(dest);
            //获取所有工作簿
            Sheet[] sheets = book.getSheets();
            for (Sheet sheet : sheets) {
                //工作簿行与列
                int rows = sheet.getRows();
                int clomns = sheet.getColumns();
                System.out.println("row:" + rows);
                System.out.println("clomns:" + clomns);
                for (int i = 1; i < rows; i++) {
                    Cell[] cells = sheet.getRow(i);
                    String sessionStr = cells[0].getContents().replace("届", "").replace("第", "");
                    boolean b = isInteger(sessionStr);
                    Integer session = null;
                    if (b == true) {
                        session = Integer.parseInt(sessionStr);
                    } else {
                        session = chineseNumToArabicNum(sessionStr);
                    }
                    String yearStr = cells[1].getContents().replace("年", "").replace("第", "");
                    b = isInteger(yearStr);
                    Integer year = null;
                    if (b == true) {
                        year = Integer.parseInt(yearStr);
                    } else {
                        year = chineseNumToArabicNum(yearStr);
                    }
                    String roundStr = cells[2].getContents().replace("轮", "").replace("第", "");
                    b = isInteger(sessionStr);
                    Integer round = null;
                    if (b == true) {
                        round = Integer.parseInt(roundStr);
                    } else {
                        round = chineseNumToArabicNum(roundStr);
                    }

                    LocalDateTime startTime = TimeUtil.getTime(cells[3].getContents());
                    LocalDateTime endTime = TimeUtil.getTime(cells[4].getContents());


                    String groupName = cells[5].getContents().replace("组", "").replace("第", "");
                    b = isInteger(groupName);
                    Integer group = null;
                    if (b == true) {
                        group = Integer.parseInt(roundStr);
                    } else {
                        group = chineseNumToArabicNum(roundStr);
                    }

                    String unitName = cells[6].getContents();
                    QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("name", unitName);
                    TUnit tunit = unitService.getOne(queryWrapper);
                    String unitid = new String();
                    if (tunit != null) {
                        unitid = tunit.getId();
                    } else {
                        unitid = UUID.randomUUID().toString().replace("-", "");
                        TUnit u = new TUnit();
                        u.setName(unitName);
                        u.setId(unitid);
                        unitService.save(u);
                    }

                    TPatrol patrol = new TPatrol();
                    patrol.setRound(round);
                    patrol.setSession(session);
                    patrol.setGroupName(group);
                    patrol.setYear(year);
                    patrol.setEndTime(endTime);
                    patrol.setPatrolTime(startTime);
                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    patrolQueryWrapper
                            .eq("session", patrol.getSession())
                            .eq("year", patrol.getYear())
                            .eq("round", patrol.getRound())
                            .eq("group_name", patrol.getGroupName());
                    //查询是否有重复
                    TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
                    if (patrol1 == null) {
                        patrol.setId(UUID.randomUUID().toString().replace("-", ""));
                        patrolService.save(patrol);
                    }
                    patrol1 = patrolService.getOne(patrolQueryWrapper);


                    TPatrolUnit patrolUnit = addPatrol(patrol1, unitid);

                    TSpecial clue = new TSpecial();

                }


            }
        } catch (Exception e) {
            log.error(e.toString());
        }
        return ResultVO.success();
    }


    @PostMapping("inserts_report")
    @ApiOperation("批量导入专题报告")
    public ResultVO inserts(@RequestParam("file") MultipartFile file, Integer session, Integer year, Integer round, String groupname) {
        String into = fileDir + "special/into/" + session + "/" + year + "/" + round + "/" + groupname + "/";
        String outDir ="special/out/" + session + "/" + year + "/" + round + "/" + groupname + "/";
        File des = new File(into);
        if (!des.exists()) {
            des.mkdirs();
        }
        File out = new File(fileDir + outDir);
        if (!out.exists()) {
            out.mkdirs();
        }
        if (file.isEmpty()) {
            return ResultVO.error(50000, "上传文件为空");
        }
        String filename = file.getOriginalFilename();
        if (filename.contains("zip") || filename.contains("rar")) {
            try {
                file.transferTo(new File(into + filename));
                File f = new File(into + filename);
                List<File> list = new ArrayList<>();
                if (filename.contains("zip")) {
                    list = Bzi2.unZipFiles(f, fileDir + outDir);
                } else {
                    list = Bzi2.unRarFile(f.getPath(), fileDir + outDir);
                }

                for (File file1 : list) {
                    if (file1.getName().contains("xls")) {
                        inserts(file1.getPath());
                    }
                }
                for (File file1 : list) {
                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    boolean g = isInteger(groupname);
                    Integer group = null;
                    if (g == true) {
                        group = Integer.parseInt(groupname);
                    } else {
                        group = chineseNumToArabicNum(groupname);
                    }
                    patrolQueryWrapper.eq("session", session)
                            .eq("year", year)
                            .eq("round", round)
                            .eq("group_name", group);
                    TPatrol patrol = patrolService.getOne(patrolQueryWrapper);
                    if (patrol != null) {
                        if (!file1.getName().contains("xls")) {
                            String unitName = file1.getName().split("-")[0];
                            QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
                            queryWrapper.eq("name", unitName);
                            TUnit unit = unitService.getOne(queryWrapper);
                            System.out.println("查询到单位" + unit);
                            if (unit != null) {

                                QueryWrapper<TPatrolUnit> queryWrapper1 = new QueryWrapper<>();
                                queryWrapper1.eq("unit_id", unit.getId())
                                        .eq("patrol_id", patrol.getId());
                                TPatrolUnit patrolUnit = patrolUnitService.getOne(queryWrapper1);
                                QueryWrapper<TSpecial> tReportQueryWrapper = new QueryWrapper<>();
                                tReportQueryWrapper.eq("patrol_id", patrolUnit.getId());
                                TSpecial tReport = service.getOne(tReportQueryWrapper);
                                System.out.println("查询到报告表" + tReport);
                                if (tReport != null) {
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
                                    if (file1.getName().contains("专题报告")) {
                                        material.setFiletype(4);
                                    } else if (file1.getName().contains("领导批示")) {
                                        material.setFiletype(5);
                                    } else if (file1.getName().contains("办结材料")) {
                                        material.setFiletype(6);
                                    }

                                    boolean b = materialService.save(material);
                                    if (b == true) {
                                        System.out.println("成功导入" + material);
                                    }
                                }

                            }


                        }

                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return ResultVO.error(50000, "文件类型不对");
        }
        return ResultVO.success();
    }

    public boolean inserts(String reportPath) {
        Workbook book;

        boolean b = false;
        File dest = new File(reportPath);
        try {
            book = Workbook.getWorkbook(dest);
            //获取所有工作簿
            Sheet[] sheets = book.getSheets();
            for (Sheet sheet : sheets) {
                //工作簿行与列
                int rows = sheet.getRows();
                int clomns = sheet.getColumns();
                System.out.println("row:" + rows);
                System.out.println("clomns:" + clomns);
                for (int i = 1; i < rows; i++) {
                    Cell[] cells = sheet.getRow(i);
                    String sessionStr = cells[0].getContents().replace("届", "").replace("第", "");
                    b = isInteger(sessionStr);
                    Integer session = null;
                    if (b == true) {
                        session = Integer.parseInt(sessionStr);
                    } else {
                        session = chineseNumToArabicNum(sessionStr);
                    }
                    String yearStr = cells[1].getContents().replace("年", "").replace("第", "");
                    b = isInteger(yearStr);
                    Integer year = null;
                    if (b == true) {
                        year = Integer.parseInt(yearStr);
                    } else {
                        year = chineseNumToArabicNum(yearStr);
                    }
                    String roundStr = cells[2].getContents().replace("轮", "").replace("第", "");
                    b = isInteger(sessionStr);
                    Integer round = null;
                    if (b == true) {
                        round = Integer.parseInt(roundStr);
                    } else {
                        round = chineseNumToArabicNum(roundStr);
                    }
                    for (int sc = 0; sc < cells.length; sc++) {
                        System.out.println(cells[sc].getContents());
                    }
                    String groupName = cells[3].getContents().replace("组", "").replace("第", "");
                    b = isInteger(groupName);
                    Integer group = null;
                    if (b == true) {
                        group = Integer.parseInt(groupName);
                    } else {
                        group = chineseNumToArabicNum(groupName);
                    }


                    String unitlevel = cells[4].getContents();

                    String unitName = cells[5].getContents();
                    String shortName = cells[6].getContents();
                    String mobile = cells[7].getContents();
                    String user = cells[8].getContents();
                    String email = cells[9].getContents();

                    QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("name", unitName);
                    TUnit tunit = unitService.getOne(queryWrapper);
                    System.out.println("xls里的单位" + tunit);
                    String unitid = new String();
                    if (tunit != null) {
                        unitid = tunit.getId();
                    } else {
                        unitid = UUID.randomUUID().toString().replace("-", "");
                        TUnit u = new TUnit();
                        u.setName(unitName);
                        u.setSort(unitlevel);
                        u.setEmail(email);
                        u.setUser(user);
                        u.setMobile(mobile);
                        u.setShorter(shortName);
                        u.setId(unitid);
                        unitService.save(u);
                    }

                    TPatrol patrol = new TPatrol();
                    patrol.setRound(round);
                    patrol.setSession(session);
                    patrol.setGroupName(group);
                    patrol.setYear(year);
                    patrol.setEndTime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    patrol.setPatrolTime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    patrolQueryWrapper
                            .eq("session", patrol.getSession())
                            .eq("year", patrol.getYear())
                            .eq("round", patrol.getRound())
                            .eq("group_name", patrol.getGroupName());
                    //查询是否有重复
                    TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
                    if (patrol1 == null) {
                        patrol.setId(UUID.randomUUID().toString().replace("-", ""));
                        patrolService.save(patrol);
                    }
                    patrol1 = patrolService.getOne(patrolQueryWrapper);
                    TPatrolUnit patrolUnit = addPatrol(patrol1, unitid);
                    TSpecial special = new TSpecial();
                    special.setId(UUID.randomUUID().toString().replace("-", ""));
                    special.setEndtime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    special.setPatrolId(patrolUnit.getId());
                    special.setHandoverTime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    special.setUderDepartment(cells[10].getContents());
                    QueryWrapper<TSpecial> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("patrol_id", patrolUnit.getId());
                    TSpecial e = service.getOne(queryWrapper1);
                    if (e == null) {
                        b = service.save(special);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    @PostMapping("inserts_clus")
    public ResultVO inserts_clus(@RequestParam("file") MultipartFile file) {
        Workbook book = null;
        String fileName = file.getOriginalFilename();
        String filePath = fileDir + "Clue/" + LocalDateTime.now().getYear() + "/" + LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/";

        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            book = Workbook.getWorkbook(dest);
            //获取所有工作簿
            Sheet[] sheets = book.getSheets();
            for (Sheet sheet : sheets) {
                //工作簿行与列
                int rows = sheet.getRows();
                int clomns = sheet.getColumns();
                System.out.println("row:" + rows);
                System.out.println("clomns:" + clomns);
                for (int i = 1; i < rows; i++) {
                    Cell[] cells = sheet.getRow(i);
                    String sessionStr = cells[0].getContents().replace("届", "").replace("第", "");
                    boolean b = isInteger(sessionStr);
                    Integer session = null;
                    if (b == true) {
                        session = Integer.parseInt(sessionStr);
                    } else {
                        session = chineseNumToArabicNum(sessionStr);
                    }
                    String yearStr = cells[1].getContents().replace("年", "").replace("第", "");
                    b = isInteger(yearStr);
                    Integer year = null;
                    if (b == true) {
                        year = Integer.parseInt(yearStr);
                    } else {
                        year = chineseNumToArabicNum(yearStr);
                    }
                    String roundStr = cells[2].getContents().replace("轮", "").replace("第", "");
                    b = isInteger(roundStr);
                    Integer round = null;
                    if (b == true) {
                        round = Integer.parseInt(roundStr);
                    } else {
                        round = chineseNumToArabicNum(roundStr);
                    }

                    LocalDateTime startTime = TimeUtil.getTime(cells[3].getContents());
                    LocalDateTime endTime = TimeUtil.getTime(cells[4].getContents());


                    String groupName = cells[5].getContents().replace("组", "").replace("第", "");
                    b = isInteger(groupName);
                    Integer group = null;
                    if (b == true) {
                        group = Integer.parseInt(roundStr);
                    } else {
                        group = chineseNumToArabicNum(roundStr);
                    }

                    String unitName = cells[6].getContents();
                    QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("name", unitName);
                    TUnit tunit = unitService.getOne(queryWrapper);
                    String unitid = new String();
                    if (tunit != null) {
                        unitid = tunit.getId();
                    } else {
                        unitid = UUID.randomUUID().toString().replace("-", "");
                        TUnit u = new TUnit();
                        u.setName(unitName);
                        u.setId(unitid);
                        unitService.save(u);
                    }

                    TPatrol patrol = new TPatrol();
                    patrol.setRound(round);
                    patrol.setSession(session);
                    patrol.setGroupName(group);
                    patrol.setYear(year);
                    patrol.setEndTime(endTime);
                    patrol.setPatrolTime(startTime);
                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    patrolQueryWrapper
                            .eq("session", patrol.getSession())
                            .eq("year", patrol.getYear())
                            .eq("round", patrol.getRound())
                            .eq("group_name", patrol.getGroupName());
                    //查询是否有重复
                    TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
                    if (patrol1 == null) {
                        patrol.setId(UUID.randomUUID().toString().replace("-", ""));
                        patrolService.save(patrol);
                    }
                    patrol1 = patrolService.getOne(patrolQueryWrapper);


                    TPatrolUnit patrolUnit = addPatrol(patrol1, unitid);

                    TClue clue = new TClue();
                    Integer firstid = null;
                    Integer twoid = null;
                    if (cells[7] != null) {
                        String userName = cells[7].getContents();
                        clue.setReactionName(userName);
                    }
                    if (cells[8] != null) {
                        clue.setReactionPost(cells[8].getContents());
                    }
                    if (cells[9] != null) {

                        clue.setReactionLevel(cells[9].getContents());
                    }
                    if (cells[10] != null) {
                        String problem = cells[10].getContents();
                        clue.setProblem(problem);
                    }
//                    if (cells[11]!=null){
//
//                        String problem=cells[11].getContents();
//                        clue.setProblem(problem);
//                    }
                    if (cells[11] != null) {
                        String pipline = cells[11].getContents();
                        clue.setDiscipline(pipline);
                    }
                    String first = new String();
                    if (cells[12] != null) {
                        first = cells[12].getContents();
                        QueryWrapper<TLabel> tLabelQueryWrapper = new QueryWrapper<>();
                        tLabelQueryWrapper.eq("result", first)
                                .eq("level", 1)
                                .eq("labletype", 4);
                        TLabel label = labelService.getOne(tLabelQueryWrapper);

                        if (label != null) {
                            firstid = label.getId();
                        } else {
                            TLabel tLabel = new TLabel();
                            tLabel.setResult(first);
                            tLabel.setLabletype(4);
                            tLabel.setLevel(1);
                            labelService.save(tLabel);
                            label = labelService.getOne(tLabelQueryWrapper);
                            firstid = label.getId();
                        }
                    }

                    String two = new String();
                    if (cells[13] != null && firstid != null) {
                        two = cells[13].getContents();
                        QueryWrapper<TLabel> LabelQueryWrapper = new QueryWrapper<>();
                        LabelQueryWrapper.eq("result", two)
                                .eq("level", 2)
                                .eq("parentid", firstid)
                                .eq("labletype", 4);
                        TLabel label2 = labelService.getOne(LabelQueryWrapper);

                        if (label2 != null) {
                            twoid = label2.getId();
                        } else {
                            TLabel tLabel2 = new TLabel();
                            tLabel2.setResult(two);
                            tLabel2.setLabletype(4);
                            tLabel2.setLevel(2);
                            tLabel2.setParentid(firstid);
                            labelService.save(tLabel2);
                            label2 = labelService.getOne(LabelQueryWrapper);
                            twoid = label2.getId();
                        }
                    }


//

                    clue.setFirstcategory(firstid);

                    clue.setTwoCategory(twoid);
                    if (cells[14] != null) {
                        clue.setHandoverType(cells[14].getContents());
                    }
                    if (cells[15] != null) {
                        clue.setHandoverTime(TimeUtil.getTime(cells[15].getContents()));
                    }
                    if (cells[16] != null) {
                        clue.setClassification(cells[16].getContents());
                    }
                    if (cells[17] != null) {
                        clue.setTransferingUnit(cells[17].getContents());
                    }
                    if (cells[18] != null) {
                        clue.setResult(cells[18].getContents());
                    }
                    if (cells[19] != null) {
                        clue.setSituation(cells[19].getContents());
                    }
                    String end = cells[20].getContents();
                    if (end.equals("是")) {
                        clue.setEnd(1);
                    } else {
                        clue.setEnd(0);
                    }
                    clue.setPatrolId(patrolUnit.getId());
                    boolean bb = clueService.save(clue);
                    if (bb = true) {
                        log.info("插入线索 ：" + clue);
                    }


                }


            }
            book.close();
        } catch (Exception e) {
            log.error("",e);
            book.close();
        }
        return ResultVO.success();
    }




    public TPatrolUnit addPatrol(TPatrol patrol, String untid) {
        QueryWrapper<TPatrolUnit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unit_id", untid)
                .eq("patrol_id", patrol.getId());
        TPatrolUnit tPatrolUnit = patrolUnitService.getOne(queryWrapper);
        String patrolid = new String();
        if (tPatrolUnit == null) {
            System.out.println("没有查询到巡察任务 " + patrol);
            patrolid = UUID.randomUUID().toString().replace("-", "");
            TPatrolUnit patrolUnit = new TPatrolUnit();
            patrolUnit.setId(patrolid);
            patrolUnit.setPatrolId(patrol.getId());
            patrolUnit.setUnitId(untid);
            patrolUnitService.save(patrolUnit);
//            //初始化report表
//            TReport tReport = new TReport().setPatrolId(patrol.getId()).setCreatTime(LocalDateTime.now());
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
        tPatrolUnit = patrolUnitService.getOne(queryWrapper);
        return tPatrolUnit;
    }

    public static boolean isInteger(String str) {
        boolean b=false;
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                b=false;
            }else {
                b=true;
            }
        }
        return b;
    }

    public static int chineseNumToArabicNum(String chineseNum) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        for (int i = 0; i < chineseNum.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNum.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if (0 != count) {//添加下一个单位之前，先把上一个单位值添加到结果中
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
            if (b) {//单位{'十','百','千','万','亿'}
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
