package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qqkj.inspection.inspection.entity.MaterialAttach;
import com.qqkj.inspection.inspection.entity.TMaterial;
import com.qqkj.inspection.inspection.mapper.TMaterialMapper;
import com.qqkj.inspection.inspection.service.ITMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 专题报告材料表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-01
 */
@Service
public class TMaterialServiceImpl extends ServiceImpl<TMaterialMapper, TMaterial> implements ITMaterialService {
    @Autowired
    TMaterialMapper materialMapper;
    @Override
    public List<TMaterial> getMaterial(String parentid,Integer filetype) {
        QueryWrapper<TMaterial>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id",parentid)
                    .eq("filetype",filetype);
        List<TMaterial> tMaterial=materialMapper.selectList(queryWrapper);
        return tMaterial;
    }
}
