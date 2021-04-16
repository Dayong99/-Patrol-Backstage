package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.ProblemPatrol;
import com.qqkj.inspection.inspection.entity.TProblem;
import com.qqkj.inspection.inspection.mapper.TProblemMapper;
import com.qqkj.inspection.inspection.service.ITProblemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 巡察问题表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@Service
public class TProblemServiceImpl extends ServiceImpl<TProblemMapper, TProblem> implements ITProblemService {

    @Autowired
    TProblemMapper problemMapper;

    @Override
    public IPage<ProblemPatrol> query(Page<ProblemPatrol> page , QueryWrapper<ProblemPatrol> patrolQueryWrapper) {
        return problemMapper.query(page,patrolQueryWrapper);
    }

    @Override
    public List<ProblemPatrol> query2(QueryWrapper<ProblemPatrol> patrolQueryWrapper) {

        return problemMapper.query2(patrolQueryWrapper);
    }

    @Override
    public List<HashMap<String, Object>> getFirstCategoryNum(Integer year, Integer session,
                                                        Integer round, Integer groupName,
                                                        String unitName, Integer first_category,String sort) {
        return problemMapper.getFirstCategoryNum(year,session, round, groupName, unitName, first_category,sort);
    }

    @Override
    public List<ProblemPatrol> listAll(String sort) {
        return problemMapper.listAll(sort);
    }

    @Override
    public  List<HashMap<String, Object>> getFirstCount(Integer year, Integer session,
                                             Integer round, Integer groupName,
                                             String unitName, Integer first_category,String sort) {
        return  problemMapper.getFirstCount(year, session, round, groupName, unitName, first_category,sort);
    }

    @Override
    public List<HashMap<String, Object>> getTwoCategoryNum(Integer year, Integer session,
                                                      Integer round, Integer groupName,
                                                      String unitName, Integer first_category,String sort,boolean b) {
        return problemMapper.getTwoCategoryNum(year, session, round, groupName, unitName, first_category,sort,b);
    }

    @Override
    public List<HashMap<String, Object>> getTwoCount(Integer year, Integer session, Integer round, Integer groupName, String unitName, Integer first_category, String sort, boolean b) {
        return problemMapper.getTwoCount(year, session, round, groupName, unitName, first_category, sort,b);
    }

    @Override
    public List<HashMap<String, Object>> getMap(Integer year, Integer session, Integer round, Integer groupName, String unitName, Integer firstId,String sort) {
        return problemMapper.getMap(year, session, round, groupName, unitName, firstId,sort);
    }

    @Override
    public HashMap<String, Object> getNums(Integer session, Integer round, Integer firstId,String sort) {
        return problemMapper.getNums(session, round, firstId,sort);
    }
}
