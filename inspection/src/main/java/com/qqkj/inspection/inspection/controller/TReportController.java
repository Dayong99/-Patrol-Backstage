package com.qqkj.inspection.inspection.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.easyExcel.util.LocalDateTimeConverter;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.common.utils.TimeUtil;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.*;
import com.qqkj.inspection.inspection.service.impl.*;
import com.qqkj.inspection.inspection.until.Bzi2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;
import net.sf.saxon.trans.SymbolicName;
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
 * 巡察报告表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/inspection/t-report")
@Api(tags = {"巡察报告表接口"})
@Slf4j
public class TReportController {

    @Autowired
    private ITReportService reportService;
    @Autowired
    private ITMaterialService materialService;
    @Value("${qqkj.fileDir}")
    private String fileDir;
    @Autowired
    private ITAttachService attachService;
    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    TUnitServiceImpl unitService;
    @Autowired
    ITPatrolUnitService patrolUnitService;

    @PostMapping
    @ApiOperation(value = "添加一行报告表数据")
    public ResultVO addReport(TReport report) {
        QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("patrol_id", report.getPatrolId());
        TReport one = reportService.getOne(reportQueryWrapper);
        if (one == null) {
            report.setCreatTime(LocalDateTime.now());
            reportService.save(report);
            return ResultVO.success(report.getId());
        }
        return ResultVO.error(50000, "增加报告表重复");
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个报告表")
    public ResultVO delete(String id) {
        QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
        //删除material表的相关数据
        tMaterialQueryWrapper.eq("parent_id", id);
        List<TMaterial> list = materialService.list(tMaterialQueryWrapper);
        for (TMaterial material : list) {
            //删除附件
            attachService.removeById(material.getAttachId());
        }
        materialService.remove(tMaterialQueryWrapper);
        return ResultVO.success(reportService.removeById(id));
    }

    @PutMapping
    @ApiOperation(value = "修改一个报告")
    public ResultVO put(TReport report) {
        QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
        reportQueryWrapper.eq("patrol_id", report.getPatrolId());
        TReport one = reportService.getOne(reportQueryWrapper);
        report.setId(report.getId() == null ? one.getId() : report.getId());
        report.setPatrolTime(report.getPatrolTime() == null ? LocalDateTime.now() : report.getPatrolTime());
        return ResultVO.success(reportService.updateById(report));
    }

    public TMaterial getOne(String id, Integer filetype) {
        QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
        tMaterialQueryWrapper.eq("parent_id", id).eq("filetype", filetype);
        //领导小组会报告
        TMaterial one1 = materialService.getOne(tMaterialQueryWrapper);
        return one1;
    }


    @GetMapping("outputs")
    public void outputs() {

    }

    @GetMapping
    @ApiOperation(value = "分页查询巡察报告数据")
    public ResultVO list(@ApiParam(value = "当前页数") @RequestParam Integer current,
                         @ApiParam(value = "一页多少条数据") @RequestParam Integer size,
                         @ApiParam(value = "年度") @RequestParam(required = false) Integer year,
                         @ApiParam(value = "届次") @RequestParam(required = false) Integer session,
                         @ApiParam(value = "轮次") @RequestParam(required = false) Integer round,
                         @ApiParam(value = "巡察组") @RequestParam(required = false) Integer groupName,
                         @ApiParam(value = "巡察单位") @RequestParam(required = false) String name) {
        //分页
        Page<Patrol> patrolPage = new Page<>(current, size);
        IPage<Patrol> patrolIPage = patrolService.getAll(patrolPage, session, year, round, groupName, name);
        List<Patrol> list = patrolIPage.getRecords();
        for (Patrol patrol : list) {
            List<ReportPatrolUnit> reportPatrolUnits = patrol.getReportPatrolUnits();
            for (ReportPatrolUnit reportPatrolUnit : reportPatrolUnits) {
                QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
                reportQueryWrapper.eq("patrol_id", reportPatrolUnit.getId());
                TReport tReport = reportService.getOne(reportQueryWrapper);
                if (tReport != null) {
                    Report report = new Report();
                    report.setId(tReport.getId()).setPatrolId(tReport.getPatrolId()).setPatrolTime(tReport.getPatrolTime()).setCreatTime(tReport.getCreatTime());
                    reportPatrolUnit.setReport(report);
                    //领导小组会报告
                    TMaterial one1 = getOne(report.getId(), 1);
                    if (one1 != null) {
                        reportPatrolUnit.getReport().setLeadershipReport(one1);
                    }

                    //书记专题会报告
                    TMaterial one2 = getOne(report.getId(), 2);
                    if (one2 != null) {
                        reportPatrolUnit.getReport().setSecretaryReport(one2);
                    }

                    //反馈报告
                    TMaterial one3 = getOne(report.getId(), 3);
                    if (one3 != null) {
                        reportPatrolUnit.getReport().setFeedbackReport(one3);
                    }


                    TMaterial one12 = getOne(report.getId(), 12);
                    if (one12 != null) {
                        reportPatrolUnit.getReport().setTeamReport(one12);
                    }

                    //情况报告
                    TMaterial one10 = getOne(report.getId(), 10);
                    if (one10 != null) {
                        reportPatrolUnit.getReport().setSituationReport(one10);
                    }

                    //线索报告
                    TMaterial one11 = getOne(report.getId(), 11);
                    if (one11 != null) {
                        reportPatrolUnit.getReport().setLeadReport(one11);
                    }

                }

            }
        }
        return ResultVO.success(patrolIPage);
    }

    /**
     * 导出 Excel
     */
    @GetMapping("export")
    @ApiOperation("批量导出Excel")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "年度") @RequestParam(required = false) Integer year,
                       @ApiParam(value = "届次") @RequestParam(required = false) Integer session,
                       @ApiParam(value = "轮次") @RequestParam(required = false) Integer round,
                       @ApiParam(value = "巡察组") @RequestParam(required = false) Integer groupName,
                       @ApiParam(value = "巡察单位") @RequestParam(required = false) String name)
            throws OilException {

//        response.setHeader("Content-disposition",
//                String.format("attachment; filename=\"%s\"", "problem.xlsx"));
//        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        List<String> fileAll = new ArrayList<>();
        try {
            List<Patrol> patrols = patrolService.getAll2(session, year, round, groupName, name);
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
            cells.setCellValue("巡察对象分类");

            cells = rows.createCell(5);
            cells.setCellValue("被巡察单位");

            int i = 1;
            for (Patrol patrol : patrols) {
                int session2 = patrol.getSession();
                int year2 = patrol.getYear();
                int round2 = patrol.getRound();
                int group2 = patrol.getGroupName();
                List<ReportPatrolUnit> reportPatrolUnits = patrol.getReportPatrolUnits();
                List<TUnit> units = patrol.getUnits();
                for (ReportPatrolUnit reportPatrolUnit : reportPatrolUnits) {
                    String uniName = new String();
                    String unitType = new String();
                    String reportUnitid = reportPatrolUnit.getUnitId();
                    for (TUnit unit : units) {
                        if (reportUnitid.equals(unit.getId())) {
                            uniName = unit.getName();
                            unitType = unit.getSort();
                        }
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
                    cells.setCellValue(unitType);

                    cells = rows.createCell(5);
                    cells.setCellValue(uniName);

                    String path = unitType + session2 + "届_" + year2 + "年_" + round2 + "轮_" + group2 + "组_" + uniName;


                    //把所有文件压缩导一个zip中
                    QueryWrapper<TReport> reportQueryWrapper = new QueryWrapper<>();
                    reportQueryWrapper.eq("patrol_id", reportPatrolUnit.getId());
                    TReport tReport = reportService.getOne(reportQueryWrapper);
                    List<String> filePath = new ArrayList<>();
                    if (tReport != null) {
                        //领导小组会报告
                        TMaterial one1 = getOne(tReport.getId(), 1);
                        if (one1 != null) {
                            TAttach attachment = attachService.getById(one1.getAttachId());
                            if (attachment != null) {
                                File file1 = new File(fileDir + attachment.getFilepath());
                                if (file1.exists()) {
                                    filePath.add(file1.getPath());
                                }

                            }
                        }

                        //书记专题会报告
                        TMaterial one2 = getOne(tReport.getId(), 2);
                        if (one2 != null) {
                            TAttach attachment2 = attachService.getById(one2.getAttachId());
                            if (attachment2 != null) {
                                File file2 = new File(fileDir + attachment2.getFilepath());
                                if (file2.exists()) {
                                    filePath.add(file2.getPath());
                                }

                            }
                        }


                        //反馈报告
                        TMaterial one3 = getOne(tReport.getId(), 3);

                        if (one3 != null) {
                            TAttach attachment3 = attachService.getById(one3.getAttachId());
                            if (attachment3 != null) {
                                File file3 = new File(fileDir + attachment3.getFilepath());
                                if (file3.exists()) {
                                    filePath.add(file3.getPath());
                                }

                            }
                        }


                        TMaterial one12 = getOne(tReport.getId(), 12);
                        if (one12 != null) {
                            TAttach attachment12 = attachService.getById(one12.getAttachId());
                            if (attachment12 != null) {
                                File file12 = new File(fileDir + attachment12.getFilepath());
                                if (file12.exists()) {
                                    filePath.add(file12.getPath());
                                }


                            }
                        }
                        int count = 0;
                        if (filePath.size() != 0) {
                            count = Bzi2.compress(filePath, fileDir + path + ".zip", false);
                        }

                        if (count != 0) {
                            fileAll.add(fileDir + path + ".zip");
                        }

                        log.info("成功压缩个数" + count);
                    }
                }
            }
            String xlsxPath = fileDir + UUID.randomUUID().toString().replace("-", "") +".xlsx";
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
            response.setHeader("Content-type", "application/zip");
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


    @PutMapping("putAll")
    @ApiOperation(value = "清空一行报告")
    public ResultVO putAll(@ApiParam(value = "巡察报告id") @RequestParam String id) {
        UpdateWrapper<TReport> tReportUpdateWrapper = new UpdateWrapper<>();
        tReportUpdateWrapper.set("patrol_time", null).eq("id", id);
        QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
        tMaterialQueryWrapper.eq("parent_id", id).and(i -> i.eq("filetype", 1).or().eq("filetype", 2).or().eq("filetype", 3).or().eq("filetype", 10).or().eq("filetype", 11));
        materialService.remove(tMaterialQueryWrapper);
        return ResultVO.success(reportService.update(tReportUpdateWrapper));
    }

    @PostMapping("upload")
    @ApiOperation(value = "上传文件或者修改文件")
    public ResultVO upload(@ApiParam("领导小组会报告") @RequestParam(required = false) MultipartFile file1,
                           @ApiParam("书记专题会报告") @RequestParam(required = false) MultipartFile file2,
                           @ApiParam("反馈报告") @RequestParam(required = false) MultipartFile file3,
                           @ApiParam("情况报告") @RequestParam(required = false) MultipartFile file4,
                           @ApiParam("线索报告") @RequestParam(required = false) MultipartFile file5,
                           @ApiParam("班子反馈报告") @RequestParam(required = false) MultipartFile file12,
                           @ApiParam("巡察报告表id") @RequestParam String reportId) {
        TReport report = new TReport().setId(reportId).setPatrolTime(LocalDateTime.now());
        reportService.updateById(report);
        //判断领导小组会报告是否是修改
        int count = 0;
        //添加或者修改领导小组会报告
        //添加或者修改线索报告
        if (file12 != null && !file12.isEmpty()) {
            String contentType = file12.getContentType();
            String fileName = file12.getOriginalFilename();
            long size = file12.getSize();
            String path = fileDir + "report/leadReport/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file12.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }

            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 12);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加线索报告
                log.info("班子反馈报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(12);
                materialService.save(material);
                log.info("班子反馈报告 :" + material);
            } else {
                log.info("班子反馈报告");
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
                count++;
                log.info("成功修改班子反馈报告 :" + material);
            }

        }


        if (file1 != null && !file1.isEmpty()) {
            String contentType = file1.getContentType();
            String fileName = file1.getOriginalFilename();
            long size = file1.getSize();
            String path = fileDir + "report/leadershipReport/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file1.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }

            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 1);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加领导小组会报告
                log.info("领导小组会报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(1);
                materialService.save(material);
                log.info("领导小组会报告 :" + material);
            } else {
                log.info("领导小组会报告");
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
                count++;
                log.info("成功修改领导小组会报告 :" + material);
            }
        }

        //添加或者修改书记专题会报告
        if (file2 != null && !file2.isEmpty()) {
            String contentType = file2.getContentType();
            String fileName = file2.getOriginalFilename();
            long size = file2.getSize();
            String path = fileDir + "report/secretaryReport/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file2.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 2);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加书记专题会报告
                log.info("书记专题会报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(2);
                materialService.save(material);
                log.info("书记专题会报告 :" + material);
            } else {
                log.info("书记专题会报告");
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
                count++;
                log.info("成功修改书记专题会报告 :" + material);
            }
        }

        //添加或者修改反馈报告
        if (file3 != null && !file3.isEmpty()) {
            String path = fileDir + "report/feedbackReport/";
            String contentType = file3.getContentType();
            String fileName = file3.getOriginalFilename();
            long size = file3.getSize();
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file3.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 3);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加反馈报告
                log.info("反馈报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(3);
                materialService.save(material);
                log.info("反馈报告 :" + material);
            } else {
                //修改反馈报告
                log.info("反馈报告");
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
                count++;
                log.info("成功修改反馈报告 :" + material);
            }
        }
        //添加或者修改情况报告
        if (file4 != null && !file4.isEmpty()) {
            String contentType = file4.getContentType();
            String fileName = file4.getOriginalFilename();
            long size = file4.getSize();
            String path = fileDir + "report/situationReport/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file4.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }

            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 10);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加情况报告
                log.info("情况报告报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(10);
                materialService.save(material);
                log.info("情况报告 :" + material);
            } else {
                log.info("情况报告");
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
                count++;
                log.info("成功修改情况报告 :" + material);
            }
        }
        //添加或者修改线索报告
        if (file5 != null && !file5.isEmpty()) {
            String contentType = file5.getContentType();
            String fileName = file5.getOriginalFilename();
            long size = file5.getSize();
            String path = fileDir + "report/leadReport/";
            File des = new File(path);
            if (!des.exists()) {
                des.mkdirs();
            }
            try {
                file5.transferTo(new File(path + fileName));
            } catch (IOException e) {
                log.error(String.valueOf(e));
            }

            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", reportId).eq("filetype", 11);
            TMaterial one = materialService.getOne(tMaterialQueryWrapper);
            if (one == null) {
                //添加线索报告
                log.info("线索报告");
                TAttach attach = new TAttach();
                String attachid = UUID.randomUUID().toString().replace("-", "");
                attach.setId(attachid)
                        .setFilepath(path.replace(fileDir, "") + fileName)
                        .setContentType(contentType)
                        .setFilename(fileName)
                        .setFilesize(Math.toIntExact(size));
                attachService.save(attach);
                count++;
                TMaterial material = new TMaterial()
                        .setFileName(fileName)
                        .setAttachId(attachid)
                        .setParentId(reportId)
                        .setFiletype(11);
                materialService.save(material);
                log.info("线索报告 :" + material);
            } else {
                log.info("线索报告");
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
                count++;
                log.info("成功修改线索报告 :" + material);
            }

        }

        return ResultVO.success("成功修改或添加" + count + "个附件");
    }

    @GetMapping("getAllAttach")
    @ApiOperation(value = "获取全部materialId，用于下载全部附件或者第几组的全部附件")
    public ResultVO getAttach(@ApiParam(value = "需要下载附件第几组的组名") @RequestParam(required = false) Integer group) {
        List<TReport> reportList = reportService.list();
        ArrayList<String> list = new ArrayList<>();
        for (TReport report : reportList) {
            QueryWrapper<TMaterial> tMaterialQueryWrapper = new QueryWrapper<>();
            tMaterialQueryWrapper.eq("parent_id", report.getId()).and(i -> i.eq("filetype", 1).or().eq("filetype", 2).or().eq("filetype", 3));
            if (group != null) {
                tMaterialQueryWrapper.eq("group_name", group);
            }
            List<TMaterial> list1 = materialService.list(tMaterialQueryWrapper);
            for (TMaterial material : list1) {
                list.add(material.getId());
            }
        }
        return ResultVO.success(list);
    }


    @DeleteMapping("delFile")
    @ApiOperation(value = "删除文件")
    public ResultVO delFile(@ApiParam("material_id") @RequestParam String id,
                            @ApiParam("attachid") @RequestParam String attachid) {
        boolean b = false;
        b = materialService.removeById(id);
        b = attachService.removeById(attachid);
        return ResultVO.success(b);
    }


    @PostMapping("inserts_report")
    @ApiOperation("批量导入巡察报告")
    public ResultVO inserts(@RequestParam("file") MultipartFile file, Integer session, Integer year, Integer round, String groupname) {
        String into = fileDir + "report/into/" + session + "/" + year + "/" + round + "/" + groupname + "/";
        String outDir ="report/out/" + session + "/" + year + "/" + round + "/" + groupname + "/";
        File des = new File(into);
        if (!des.exists()) {
            des.mkdirs();
        }
        File out = new File( fileDir + outDir);
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
                    list = Bzi2.unZipFiles(f,  fileDir + outDir);
                } else {
                    list = Bzi2.unRarFile(f.getPath(),  fileDir + outDir);
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
                        if (file1.getName().contains("报告")) {
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
                                if (patrolUnit==null){
                                    TPatrolUnit tPatrolUnit=new TPatrolUnit();
                                    tPatrolUnit.setUnitId(unit.getId());
                                    tPatrolUnit.setPatrolId(patrol.getId());
                                    patrolUnitService.save(tPatrolUnit);
                                }
                                patrolUnit=patrolUnitService.getOne(queryWrapper1);
                                QueryWrapper<TReport> tReportQueryWrapper = new QueryWrapper<>();
                                tReportQueryWrapper.eq("patrol_id", patrolUnit.getId());
                                TReport tReport = reportService.getOne(tReportQueryWrapper);
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

                                    if (file1.getName().contains("领导小组会报告")) {
                                        material.setFiletype(1);
                                       TMaterial m= getOne(tReport.getId(),1);
                                       if (m!=null){
                                           m.setAttachId(attachid);
                                           materialService.updateById(m);
                                       }else{
                                           boolean b = materialService.save(material);
                                           if (b == true) {
                                               System.out.println("成功导入" + material);
                                           }
                                       }
                                    } else if (file1.getName().contains("书记专题会报告")) {
                                        material.setFiletype(2);
                                        TMaterial m= getOne(tReport.getId(),2);
                                        if (m!=null){
                                            m.setAttachId(attachid);
                                            materialService.updateById(m);
                                        }else{
                                            boolean b = materialService.save(material);
                                            if (b == true) {
                                                System.out.println("成功导入" + material);
                                            }
                                        }
                                    } else if (file1.getName().contains("班子反馈")) {
                                        material.setFiletype(12);
                                        TMaterial m= getOne(tReport.getId(),12);
                                        if (m!=null){
                                            m.setAttachId(attachid);
                                            materialService.updateById(m);
                                        }else{
                                            boolean b = materialService.save(material);
                                            if (b == true) {
                                                System.out.println("成功导入" + material);
                                            }
                                        }
                                    }else  if (file1.getName().contains("负责人反馈")){
                                        material.setFiletype(3);
                                        TMaterial m= getOne(tReport.getId(),3);
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
        } else {
            return ResultVO.error(50000, "文件类型不对");
        }
        return ResultVO.success();
    }

    @PostMapping("inserts")
    @ApiOperation(value = "批量导入excel")
    public ResultVO inserts(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String filePath = fileDir + "Report/" + LocalDateTime.now().getYear() + "/" + LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File dest = new File(filePath + fileName);
        boolean b=inserts(dest.getPath());
        return ResultVO.success(b);
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
                    TReport report = new Report();
                    report.setId(UUID.randomUUID().toString().replace("-", ""));
                    report.setCreatTime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    report.setPatrolTime(LocalDateTime.of(year, 1, 1, 1, 1, 1));
                    report.setPatrolId(patrolUnit.getId());
                    QueryWrapper<TReport> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.eq("patrol_id", patrolUnit.getId());
                    TReport e = reportService.getOne(queryWrapper1);
                    if (e == null) {
                        b = reportService.save(report);

                    }
                    e = reportService.getOne(queryWrapper1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
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
        tPatrolUnit = patrolUnitService.getOne(queryWrapper);
        return tPatrolUnit;
    }


    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    static char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
    static char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
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
