package com.qqkj.inspection.inspection.until;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.springframework.util.StringUtils;
@Slf4j
public class Bzi2 {


    @SneakyThrows
    public static void main(String[] args) {
        File file=new File("C:\\Users\\Administrator\\Downloads\\jnotify-lib-0.94.zip");
        String outDir="C:\\Users\\Administrator\\Desktop\\abs\\";
        Bzi2 bzi2=new Bzi2();
        List<File>files=Bzi2.unZipFiles(file,outDir);
        for (File file1:files){
            System.out.println(file1);
        }

    }
    public static Boolean IsFileInUse(String filePath) {
        boolean b=true;
        LocalDateTime localDateTime=LocalDateTime.now();
        LocalDateTime localDateTime2=localDateTime.plusSeconds(5);
//        while (b){
//            localDateTime=LocalDateTime.now();
//            if (localDateTime.isAfter(localDateTime2)){
//                b=false;
//            }
//        }

        boolean inUse = true;
        FileInputStream fs = null;
        try
        {
            fs = new FileInputStream(filePath);
            inUse = false;
        } catch(Exception e){
            inUse=true;
        }finally {
            if (fs!=null){
                try {
                    fs.close();
                    inUse=false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (inUse){
            log.info(filePath+"正在被使用");
        }else {
            log.info(filePath+"文件没有被使用");
        }
        return inUse;
    }
    /**
     *  构建目录
     * @param outputDir 输出目录
     * @param subDir 子目录
     */
    private static void createDirectory(String outputDir, String subDir){
        File file = new File(outputDir);
        if(!(subDir == null || subDir.trim().equals(""))) {//子目录不为空
            file = new File(outputDir + File.separator + subDir);
        }
        if(!file.exists()){
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }


    public static File decompressBZ2(File file, String targetPath, boolean delete){
        File tempFile=null;
        FileInputStream fis = null;
        OutputStream fos = null;
        BZip2CompressorInputStream bis = null;
        String suffix = ".bz2";
        try {
            fis = new FileInputStream(file);
            bis = new BZip2CompressorInputStream(fis);
            // 创建输出目录
            createDirectory(targetPath, null);
            tempFile = new File(targetPath + File.separator + file.getName().replace(suffix, ""));
            fos = new FileOutputStream(tempFile);

            int count;
            byte data[] = new byte[2048];
            while ((count = bis.read(data)) != -1) {
                fos.write(data, 0, count);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(fis != null){
                    fis.close();
                }
                if(fos != null){
                    fos.close();
                }
                if(bis != null){
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    public static List<File> unZipFiles(File fileZip, String descDir) throws IOException {
        List<File>files=new ArrayList<>();
        File pathFile = new File(descDir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        ZipFile zipFile = new ZipFile(fileZip, StandardCharsets.UTF_8);
        for (Enumeration entries = zipFile.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zipFile.getInputStream(entry);
            String outPath = (descDir + zipEntryName).replaceAll("\\*", "/");
// 判断路径是否存在,不存在则创建文件路径
            File file = new File(outPath);
            File des=file.getParentFile();
            if (!des.exists()) {
                des.mkdirs();
            }
// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outPath).isDirectory()) {
                continue;
            }
// 输出文件路径信息
            log.info(outPath);

            OutputStream out = new FileOutputStream(outPath);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
            File file1=new File(outPath);
            files.add(file1);
        }
        log.info("******************解压完毕********************");
        return files;
    }


    /**
     * 根据原始rar路径，解压到指定文件夹下.
     *
     * @param srcRarPath       原始rar路径
     * @param dstDirectoryPath 解压到的文件夹
     */
    public static List<File> unRarFile(String srcRarPath, String dstDirectoryPath) {
        List<File>files=new ArrayList<>();
        if (!srcRarPath.toLowerCase().endsWith(".rar")) {
            System.out.println("非rar文件！");
            return files;
        }
        File dstDiretory = new File(dstDirectoryPath);
        if (!dstDiretory.exists()) {// 目标目录不存在时，创建该文件夹
            dstDiretory.mkdirs();
        }



        Archive a = null;
        try {
            FileInputStream fileInputStream=new FileInputStream(srcRarPath);
            a = new Archive(fileInputStream);

            if (a != null) {
                a.getMainHeader().print(); // 打印文件信息.
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    String fileName = fh.getFileNameW().isEmpty() ? fh
                            .getFileNameString() : fh.getFileNameW();
                    if (fh.isDirectory()) { // 文件夹


                        File fol = new File(dstDirectoryPath + File.separator
                                + fileName);
                        fol.mkdirs();
                    } else { // 文件
                        File out = new File(dstDirectoryPath + File.separator
                                + fileName);
                        try {// 之所以这么写try，是因为万一这里面有了异常，不影响继续解压.
                            if (!out.exists()) {
                                if (!out.getParentFile().exists()) {// 相对路径可能多级，可能需要创建父目录.
                                    out.getParentFile().mkdirs();
                                }
                                out.createNewFile();
                            }
                            FileOutputStream os = new FileOutputStream(out);
                            a.extractFile(fh, os);
                            os.close();
                            files.add(out);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }
    /**
     * @Title: compress
     * @Description: TODO
     * @param filePaths 需要压缩的文件地址列表（绝对路径）
     * @param zipFilePath 需要压缩到哪个zip文件（无需创建这样一个zip，只需要指定一个全路径）
     * @param keepDirStructure 压缩后目录是否保持原目录结构
     * @throws IOException
     * @return int   压缩成功的文件个数
     */
    public static int compress(List<String> filePaths, String zipFilePath,Boolean keepDirStructure) throws IOException{
        byte[] buf = new byte[1024];
        File zipFile = new File(zipFilePath);
        //zip文件不存在，则创建文件，用于压缩
        if(!zipFile.exists())
            zipFile.createNewFile();
        int fileCount = 0;//记录压缩了几个文件？
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
            for(int i = 0; i < filePaths.size(); i++){
                String relativePath = filePaths.get(i);
                if(StringUtils.isEmpty(relativePath)){
                    continue;
                }
                File sourceFile = new File(relativePath);//绝对路径找到file
                if(sourceFile == null || !sourceFile.exists()){
                    continue;
                }

                FileInputStream fis = new FileInputStream(sourceFile);
                if(keepDirStructure!=null && keepDirStructure){
                    //保持目录结构
                    zos.putNextEntry(new ZipEntry(relativePath));
                }else{
                    //直接放到压缩包的根目录
                    zos.putNextEntry(new ZipEntry(sourceFile.getName()));
                }
                //System.out.println("压缩当前文件："+sourceFile.getName());
                int len;
                while((len = fis.read(buf)) > 0){
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                fis.close();
                fileCount++;
            }
            zos.close();
            //System.out.println("压缩完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileCount;
    }
}
