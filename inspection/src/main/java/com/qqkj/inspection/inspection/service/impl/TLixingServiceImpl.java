package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.TLixing;
import com.qqkj.inspection.inspection.entity.TPatrolLixing;
import com.qqkj.inspection.inspection.mapper.TLixingMapper;
import com.qqkj.inspection.inspection.mapper.TPatrolMapper;
import com.qqkj.inspection.inspection.service.ITLixingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 立行立改及查立处表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Service
public class TLixingServiceImpl extends ServiceImpl<TLixingMapper, TLixing> implements ITLixingService {
    @Autowired
    TPatrolMapper patrolMapper;

    @Override
    public IPage<TPatrolLixing> getList(Page<TPatrolLixing> page, Integer session, Integer year, Integer round, Integer groupName, String unitName,Integer lixingType) {
        return  patrolMapper.getLixing(page,year,session,round,groupName,unitName,lixingType);
    }

    @Override
    public List<TPatrolLixing> getList2(Integer session, Integer year, Integer round, Integer groupName, String unitName, Integer lixingType) {
        return  patrolMapper.getLixing2(year,session,round,groupName,unitName,lixingType);
    }
}
