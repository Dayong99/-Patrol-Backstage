package com.qqkj.inspection.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseController
{
    protected Map<String, Object> getDataTable(IPage<?> pageInfo)
    {
        Map<String, Object> rspData = new HashMap<>();
        rspData.put("rows", pageInfo.getRecords());
        rspData.put("total", pageInfo.getTotal());
        return rspData;
    }
//    protected Map<String, Object> getMaterialable(IPage<Material> pageInfo)
//    {
//        Map<String, Object> rspData = new HashMap<>();
//        List<Material>materials=pageInfo.getRecords();
//        for (Material stu:materials){
//          String type=stu.getFileType();
//            if (!type.contains("png")){
//                stu.setFileType("文档");
//            }else {
//                stu.setFileType("图片");
//            }
//        }
//
//        rspData.put("rows",materials );
//        rspData.put("total", pageInfo.getTotal());
//        return rspData;
//    }

    protected Map<String,Object> getDataConvert(IPage<Map<Object,Object>> pageInfo)
    {
        Map<String,Object> repData = new HashMap<>();
        repData.put("total",pageInfo.getTotal());
        for(Map<Object,Object> current:pageInfo.getRecords())
        {
            if((current.get("oilType").toString().trim()).equals("0"))
            {
                current.put("oilType","汽油");
            }
            if((current.get("oilType").toString().trim()).equals("1"))
            {
                current.put("oilType","柴油");
            }
            if((current.get("oilType").toString().trim()).equals("2"))
            {
                current.put("oilType","3号喷气燃料");
            }
            if((current.get("fransferType").toString()).trim().equals("2"))
            {
                current.put("fransferType","飞机加油");
            }
            if((current.get("fransferType").toString()).trim().equals("3"))
            {
                current.put("fransferType","槽车加油");
            }
            if((current.get("fransferType").toString()).trim().equals("4"))
            {
                current.put("fransferType","零发油");
            }
            if((current.get("fransferType").toString()).trim().equals("5"))
            {
                current.put("fransferType","地面加油");
            }
        }
        repData.put("rows",pageInfo.getRecords());
        return repData;
    }
}
