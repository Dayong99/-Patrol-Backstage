package com.qqkj.inspection.inspection.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qqkj.inspection.inspection.entity.TAttach;
import com.qqkj.inspection.inspection.entity.TMaterial;
import com.qqkj.inspection.inspection.service.impl.TAttachServiceImpl;
import com.qqkj.inspection.inspection.service.impl.TMaterialServiceImpl;
import com.qqkj.inspection.inspection.until.PdfUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

@RestController
@Slf4j
public class FileController {
    @Value("${qqkj.fileDir}")
    private String fileDir;

    @Autowired
    TMaterialServiceImpl service;

    @Autowired
    TAttachServiceImpl attachService;

    public static void main(String[] args) {
        String str="1.2.23.5";
        int j=str.indexOf(".");
        str.substring(0,j);
    }

    @RequestMapping("/download")
    public void test(String materialId, HttpServletResponse response){
        log.info("這是最新的版本");
        try {
            QueryWrapper<TMaterial> materialQueryWrapper = new QueryWrapper<>();
            materialQueryWrapper.eq("id", materialId);
            TMaterial material = service.getOne(materialQueryWrapper);
            //根据id查找
            TAttach attachment =attachService.getById(material.getAttachId());
            File file=new File(fileDir+attachment.getFilepath());
//            File file=new File(attachment.getFilepath());
            log.info("file名"+file.getName());
            BufferedInputStream bu=null;
            BufferedOutputStream bo=null;
            String path=file.getPath();

            log.info("查询到附件  :"+material.toString());
            response.setHeader("Content-type", attachment.getContentType());
            String name=java.net.URLEncoder.encode(file.getName(),"UTF-8");
            response.setHeader("Content-Disposition", "attachment;fileName="+name);
            byte[]bs=null;

                OutputStream ps = response.getOutputStream();
                //这句话的意思，使得放入流的数据是utf8格式
//            ps.write(data.getBytes("UTF-8"));
                log.info("开始下载文件  ：" + file.getPath());
                FileInputStream in=new FileInputStream(path);
                bu=new BufferedInputStream(in);
                OutputStream ou=response.getOutputStream();
                bo=new BufferedOutputStream(ou);
                int i=0;
                byte[]bytes=new byte[10*1024];
                while ((i=in.read(bytes))!=-1){
                    bo.write(bytes,0,i);
                }
                bo.flush();
                bu.close();
                ou.close();
        } catch (Exception e) {
            log.error("没有找到数据   ："+e);
        }
    }



    @GetMapping("preview")
    @ApiOperation("预览文件")
    public void preview(String materialId, HttpServletResponse response){
        log.info("這是预览");
        try {
            QueryWrapper<TMaterial> materialQueryWrapper = new QueryWrapper<>();
            materialQueryWrapper.eq("id", materialId);
            TMaterial material = service.getOne(materialQueryWrapper);
            //根据id查找
            TAttach attachment =attachService.getById(material.getAttachId());
            File file=new File(fileDir+attachment.getFilepath());
//            File file=new File(attachment.getFilepath());
            log.info("file名"+file.getName());

            BufferedInputStream bu=null;
            BufferedOutputStream bo=null;
            String path=file.getPath();

            log.info("查询到附件  :"+material.toString());
            if (file.getName().contains(".doc")){
                PdfUtils.convertToPdf(file.getPath());
                String filename=file.getName();
                String pdfpath=file.getParent()+"/"+filename.substring(0,filename.lastIndexOf("."))+".pdf";
                File pdfFile=new File(pdfpath);
                response.setHeader("Content-type", "application/pdf");
                String name=java.net.URLEncoder.encode(pdfFile.getName(),"UTF-8");
                response.setHeader("Content-Disposition", "attachment;fileName="+name);
                byte[]bs=null;

                OutputStream ps = response.getOutputStream();
                //这句话的意思，使得放入流的数据是utf8格式
//            ps.write(data.getBytes("UTF-8"));
                log.info("开始下载文件  ：" + pdfFile.getPath());
                FileInputStream in=new FileInputStream(pdfpath);
                bu=new BufferedInputStream(in);
                OutputStream ou=response.getOutputStream();
                bo=new BufferedOutputStream(ou);
                int i=0;
                byte[]bytes=new byte[10*1024];
                while ((i=in.read(bytes))!=-1){
                    bo.write(bytes,0,i);
                }
                bo.flush();
                bu.close();
                ou.close();
                pdfFile.delete();
            }else {

                log.info("查询到附件  :"+material.toString());
                response.setHeader("Content-type", attachment.getContentType());
                String name=java.net.URLEncoder.encode(file.getName(),"UTF-8");
                response.setHeader("Content-Disposition", "attachment;fileName="+name);
                byte[]bs=null;

                OutputStream ps = response.getOutputStream();
                //这句话的意思，使得放入流的数据是utf8格式
//            ps.write(data.getBytes("UTF-8"));
                log.info("开始下载文件  ：" + file.getPath());
                FileInputStream in=new FileInputStream(path);
                bu=new BufferedInputStream(in);
                OutputStream ou=response.getOutputStream();
                bo=new BufferedOutputStream(ou);
                int i=0;
                byte[]bytes=new byte[10*1024];
                while ((i=in.read(bytes))!=-1){
                    bo.write(bytes,0,i);
                }
                bo.flush();
                bu.close();
                ou.close();
            }

        } catch (Exception e) {
            log.error("没有找到数据   ："+e);
        }
    }

}
