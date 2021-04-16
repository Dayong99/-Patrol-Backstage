package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.Patrol;
import com.qqkj.inspection.inspection.entity.PatrolSpecial;
import com.qqkj.inspection.inspection.entity.TPatrol;
import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.mapper.TPatrolMapper;
import com.qqkj.inspection.inspection.service.ITPatrolService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 巡察表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Service
public class TPatrolServiceImpl extends ServiceImpl<TPatrolMapper, TPatrol> implements ITPatrolService {

    @Autowired
    TPatrolMapper patrolMapper;

    @Override
    public IPage<Patrol> queryPatrol(Page<Patrol> page, QueryWrapper<Patrol> patrolQueryWrapper) {
        return patrolMapper.queryPatrol(page,patrolQueryWrapper);
    }

    @Override
    public IPage<PatrolSpecial> getList(Page<PatrolSpecial> page,
                                        Integer session, Integer year, Integer round, String groupName, String unitName,String department) {

        return patrolMapper.getList(page,year,session,round,groupName,unitName,department);
    }

    @Override
    public List<PatrolSpecial> getList2(Integer session, Integer year, Integer round, String groupName, String unitName, String department) {
        return patrolMapper.getList2(year,session,round,groupName,unitName,department);
    }

    @Override
    public IPage<Patrol> getAll(Page<Patrol> page, Integer session, Integer year, Integer round, Integer groupName, String unitName) {
        return patrolMapper.getAll(page,year,session,round,groupName,unitName);
    }

    @Override
    public List<Patrol> getAll2(Integer session, Integer year, Integer round, Integer groupName, String unitName) {
        return patrolMapper.getAll2(session,year,round,groupName,unitName);
    }

}
