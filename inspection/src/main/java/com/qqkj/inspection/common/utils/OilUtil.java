package com.qqkj.inspection.common.utils;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.qqkj.inspection.common.domain.OilConstant;
import com.qqkj.inspection.common.function.CacheSelector;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * 工具类
 */
@Slf4j
public class OilUtil
{
    private static int[] hexCharCodes = new int[256];

    /**
     * 缓存查询摸板，先查缓存，如果缓存查询失败再从数据库查询
     *
     * @param cacheSelector    查询缓存的方法
     * @param databaseSelector 数据库查询方法
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T selectCacheByTemplate(CacheSelector<?> cacheSelector, Supplier<?> databaseSelector)
    {
        try
        {
            // 先查 Redis缓存
            log.debug("query data from redis ······");
            return (T) cacheSelector.select();
        }
        catch (Exception e)
        {
            // 数据库查询
            log.error("redis error：", e);
            log.debug("query data from database ······");
            return (T) databaseSelector.get();
        }
    }


    /**
     * token 加密
     *
     * @param token token
     * @return 加密后的 token
     */
    public static String encryptToken(String token)
    {
        try
        {
            EncryptUtil encryptUtil = new EncryptUtil(OilConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.encrypt(token);
        }
        catch (Exception e)
        {
            log.info("token加密失败：", e);
            return null;
        }
    }

    /**
     * token 解密
     *
     * @param encryptToken 加密后的 token
     * @return 解密后的 token
     */
    public static String decryptToken(String encryptToken)
    {
        try
        {
            EncryptUtil encryptUtil = new EncryptUtil(OilConstant.TOKEN_CACHE_PREFIX);
            return encryptUtil.decrypt(encryptToken);
        }
        catch (Exception e)
        {
            log.info("token解密失败：", e);
            return null;
        }
    }

    /**
     * 驼峰转下划线
     *
     * @param value 待转换值
     * @return 结果
     */
    public static String camelToUnderscore(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return value;
        }
        String[] arr = StringUtils.splitByCharacterTypeCamelCase(value);
        if (arr.length == 0)
        {
            return value;
        }
        StringBuilder result = new StringBuilder();
        IntStream.range(0, arr.length).forEach(i ->
        {
            if (i != arr.length - 1)
            {
                result.append(arr[i]).append(StringPool.UNDERSCORE);
            }
            else
            {
                result.append(arr[i]);
            }
        });
        return StringUtils.lowerCase(result.toString());
    }

    public static String byteArrayToHexString(byte[] array)
    {
        if (array == null)
        {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++)
        {
            buffer.append(byteToHex(array[i]));
        }
        return buffer.toString();
    }

    public static String byteToHex(byte b)
    {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1)
        {
            hex = '0' + hex;
        }
        return hex.toUpperCase(Locale.getDefault());
    }

    public static String toHexString(byte[] bytes)
    {
        if (bytes == null)
        {
            return "";
        }
        int iMax = bytes.length - 1;
        if (iMax == -1)
        {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++)
        {
            b.append(String.format("%02x", bytes[i] & 0xFF).toUpperCase());
            if (i == iMax)
            {
                return b.toString();
            }
        }
    }


    public static byte[] hexStr2Byte(String hex)
    {
        if (hex == null)
        {
            return new byte[]{};
        }
        // 奇数位补0
        if (hex.length() % 2 != 0)
        {
            hex = "0" + hex;
        }
        int length = hex.length();
        ByteBuffer buffer = ByteBuffer.allocate(length / 2);
        for (int i = 0; i < length; i++)
        {
            String hexStr = hex.charAt(i) + "";
            i++;
            hexStr += hex.charAt(i);
            byte b = (byte) Integer.parseInt(hexStr, 16);
            buffer.put(b);
        }
        return buffer.array();
    }


    //时间戳字符串转LocalDateTime ，有除1000的操作注意
    public static LocalDateTime getTimestampToLocalDateTime(String timestampStr)
    {
        Long timestampValue = Long.valueOf(timestampStr).longValue();
        return Instant.ofEpochMilli(timestampValue * 1000).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    public static byte getCheckcode(ByteBuf byteBuf)
    {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.markReaderIndex();
        byteBuf.readBytes(bytes);
        byteBuf.resetReaderIndex();
        return getCheckcode(bytes);
    }

    public static byte getCheckcode(byte[] bytes)
    {
        byte result = bytes[0];
        for (int i = 1; i < bytes.length; i++)
        {
            result ^= bytes[i];
        }
        return result;
    }

    public static float byte2float(byte[] b, int index)
    {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static void createFiles(String filesPath)
    {
        File f = new File(filesPath);
        if (!f.exists())
        {
            f.mkdirs();
        }
    }
}
