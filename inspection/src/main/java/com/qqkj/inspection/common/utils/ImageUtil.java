package com.qqkj.inspection.common.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

//import java.util.Base64;

@Slf4j
public class ImageUtil
{

//    public static String fileToBase64(String filePath) throws IOException {
//        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
//        String encodedString = Base64.getEncoder().encodeToString(fileContent);
//        return encodedString;
//    }
//
//    public static void base64ToFile(String filePath,String encodedString) throws IOException {
//        encodedString = clearbase64(encodedString);
//        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.replace("\n", ""));
//        FileUtils.writeByteArrayToFile(new File(filePath), decodedBytes);
//    }

    public static String base64Encode(byte[] bytes) {
        Base64 base64 = new Base64();
        String encodeAsString = base64.encodeAsString(bytes);
        return encodeAsString;
    }


    public static byte[] base64Dcode(String base4String) {
        Base64 base64 = new Base64();
        byte[] decode = base64.decode(base4String);
        return decode;
    }

    public static String fileToBase64(String filePath) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
        Base64 base64 = new Base64();
        String encodeAsString = base64.encodeAsString(fileContent);
        return encodeAsString;
    }

    public static void base64ToFile(String filePath, String encodedString) throws IOException {
        encodedString = clearbase64(encodedString);
        Base64 base64 = new Base64();
        byte[] decode = base64.decode(encodedString.replace("\n", ""));
        FileUtils.writeByteArrayToFile(new File(filePath), decode);
    }

    public static String saveImageByDate(String filePath, String fileName,byte[] bytes) throws IOException {

        String dateData = LocalDateTime.now().toLocalDate().toString();
        File folder = new File(filePath + dateData);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String uuidName = UUID.randomUUID().toString().replace("-", "");
        //获取上传文件的后缀名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = dateData + File.separator + uuidName + "." + fileSuffix;
        String imgPath = filePath + path;
        File dest = new File(imgPath );

        FileUtils.writeByteArrayToFile(dest, bytes);

        return path;
    }

    public static String saveImageByDateAndScale(String filePath, String fileName, byte[] bytes) throws IOException {

        String dateData = LocalDateTime.now().toLocalDate().toString();
        File folder = new File(filePath + dateData);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        long size = bytes.length;
        double scale = 1.0d;
        if (size >= 5120000f) { //500*1024
            if (size > 0) {
                scale = (5120000f) / size;
            }
        }
        String uuidName = UUID.randomUUID().toString().replace("-", "");
        String temp = uuidName + "_temp";

        //获取上传文件的后缀名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = dateData + File.separator + uuidName + "." + fileSuffix;
        String path_temp = dateData + File.separator + temp + "." + fileSuffix;
        if (path.toLowerCase().contains(".jpg")) {
            path = path.replace(".jpg", ".png");
        }

        String imgPath = filePath + path;
        String img_tepm = filePath + path_temp;
        File dest = new File(imgPath);
        File file_temp = new File(img_tepm);

        FileUtils.writeByteArrayToFile(file_temp, bytes);

        if (size < 5120000f) {
            Thumbnails.of(file_temp).scale(1f).outputFormat("png").toFile(dest);
        } else {
            Thumbnails.of(file_temp).scale(1f).outputQuality(scale).outputFormat("png").toFile(dest);
        }


        file_temp.delete();
        return path;
    }


    public static String saveFile(String filePath, String fileName, byte[] bytes) throws IOException {

        String dateData = LocalDateTime.now().toLocalDate().toString();
        File folder = new File(filePath + dateData);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String uuidName = UUID.randomUUID().toString().replace("-", "");
        String temp = uuidName + "_temp";

        //获取上传文件的后缀名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = dateData + File.separator + uuidName + "." + fileSuffix;

        String filep = filePath + path;
        File dest = new File(filep);

        FileUtils.writeByteArrayToFile(dest, bytes);

        return path;
    }


    public static String getImageBase64(String filePath, String fileName, byte[] bytes) throws IOException {

        String dateData = LocalDateTime.now().toLocalDate().toString();
        File folder = new File(filePath + dateData);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        long size = bytes.length;
        double scale = 1.0d;
        if (size >= 5120000) { //1000*1024*5
            if (size > 0) {
                scale = (5120000f) / size;
            }
        }
        String uuidName = UUID.randomUUID().toString().replace("-", "");
        String temp = uuidName + "_temp";

        //获取上传文件的后缀名
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = dateData + File.separator + uuidName + "." + fileSuffix;
        String path_temp = dateData + File.separator + temp + "." + fileSuffix;
        if (path.toLowerCase().contains(".jpg")) {
            path = path.replace(".jpg", ".png");
        }
        if (path.toLowerCase().contains(".jpeg")) {
            path = path.replace(".jpeg", ".png");
        }
        String imgPath = filePath + path;
        String img_tepm = filePath + path_temp;
        File dest = new File(imgPath);
        File file_temp = new File(img_tepm);

        FileUtils.writeByteArrayToFile(file_temp, bytes);

        if (size < 512000) {
            Thumbnails.of(file_temp).scale(1f).outputFormat("png").toFile(dest);
        } else {
            Thumbnails.of(file_temp).scale(1f).outputQuality(scale).outputFormat("png").toFile(dest);
        }


        file_temp.delete();

        byte[] fileContent = FileUtils.readFileToByteArray(dest);
        Base64 base64 = new Base64();
        String encodeAsString = base64.encodeAsString(fileContent);
        dest.delete();
        return encodeAsString;
    }


    public static String base64(String img64) {
        return "data:image/jpeg;base64," + img64;
    }

    public static String clearbase64(String img64) {
        if (img64.startsWith("data:image/jpeg;base64,")) {
            return img64.substring("data:image/jpeg;base64,".length());
        } else {
            return img64;
        }
    }
}
