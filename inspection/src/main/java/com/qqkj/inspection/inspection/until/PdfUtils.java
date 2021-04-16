package com.qqkj.inspection.inspection.until;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.ExternalOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;
@Slf4j
public class PdfUtils {
    private static final Logger logger = Logger.getLogger(PdfUtils.class
            .getName());
    @SuppressWarnings("static-access")
    private static String officeHome = "C:/Program Files (x86)/OpenOffice 4/"; //这里写的是你的openoffice的安装地址，如果你在安装openOffice 的时候选择的是默认安装，那么地址是：C:/Program Files (x86)/OpenOffice 4/。　　　　　//如果是自定义的安装方式，请填写自定义安装路径

    @SuppressWarnings("static-access")
    private static int port = 8100;//这里的内容是根据你的系统选择不同的端口号，windows系统的端口号是8100

    private static OfficeManager officeManager; // 尝试连接已存在的服务器

    public static void main(String[] args) {
        PdfUtils.convertToPdf("C:\\Users\\Administrator\\Desktop\\区检察院-反馈报告.docx");
        File sf = new File("C:\\Users\\Administrator\\Desktop\\区检察院-反馈报告.pdf");
        System.out.println(sf.getPath());
    }

    private static boolean reconnect() {
        try {// 尝试连接openoffice的已存在的服务器
            ExternalOfficeManagerConfiguration externalProcessOfficeManager = new ExternalOfficeManagerConfiguration();
            externalProcessOfficeManager.setConnectOnStart(true);
            externalProcessOfficeManager.setPortNumber(port);
            officeManager = externalProcessOfficeManager.buildOfficeManager();
            officeManager.start();
            return true;
        } catch (OfficeException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 开启新的openoffice的进程
    private static void start() {
        logger.debug("启动OpenOffice服务");
        try {
            DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();
            configuration.setOfficeHome(officeHome);// 安装地址
            configuration.setPortNumbers(port);// 端口号
            configuration.setTaskExecutionTimeout(1000 * 60 * 5);// 设置任务执行超时为5分钟
            configuration.setTaskQueueTimeout(1000 * 60 * 60 * 24);// 设置任务队列超时为24小时
            officeManager = configuration.buildOfficeManager();
            officeManager.start(); // 启动服务
        } catch (Exception e) {
            logger.error("启动OpenOffice服务出错" + e);
        }
    }

    // 使用完需要关闭该进程
    private static void stop() {
        logger.debug("关闭OpenOffice服务");
        try {
            if (officeManager != null)
                officeManager.stop();
        } catch (Exception e) {
            logger.error("关闭OpenOffice服务出错" + e);
        }
    }

    public static File convertToPdf(String input) {
        File inputFile = null;
        File outFile = null;
        try {// 如果已存在的服务不能连接或者不存在服务，那么开启新的服务　　　　
            if (!reconnect()) {
                start();// 开启服务
            }// filenameUtils是Apache对java io的封装。　FilenameUtils.separatorsToSystem：转换分隔符为当前系统分隔符　/ FilenameUtils.getFullPath:获取文件的完整目录　　　　　　　　　　　　　　// FilenameUtils.getBaseName:取出文件目录和后缀名的文件名
            String output = FilenameUtils.separatorsToSystem(FilenameUtils.getFullPath(input) + FilenameUtils.getBaseName(input) + ".pdf");
            inputFile = new File(input);
            outFile = new File(output);
            logger.info("开始转换文档：" + input + "=>" + output);
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            converter.convert(inputFile, outFile); // 转换文档
        } catch (Exception e) {
            logger.error("转换文档出错" + e);
            outFile = null;
        } finally {
            logger.info("结束转换文档");
            stop();
        }
        return outFile;
    }


}
