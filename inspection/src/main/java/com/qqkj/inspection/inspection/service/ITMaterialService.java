package com.qqkj.inspection.inspection.service;

import com.qqkj.inspection.inspection.entity.TMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 专题报告材料表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-01
 */
public interface ITMaterialService extends IService<TMaterial> {
    List<TMaterial> getMaterial(String parentid,Integer filetype);
}
