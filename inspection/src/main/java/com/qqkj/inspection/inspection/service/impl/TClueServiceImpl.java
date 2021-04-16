package com.qqkj.inspection.inspection.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolClue;
import com.qqkj.inspection.inspection.entity.TClue;
import com.qqkj.inspection.inspection.mapper.TClueMapper;
import com.qqkj.inspection.inspection.mapper.TPatrolMapper;
import com.qqkj.inspection.inspection.service.ITClueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 线索办理表 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Service
public class TClueServiceImpl extends ServiceImpl<TClueMapper, TClue> implements ITClueService {
    @Autowired
    TPatrolMapper patrolMapper;
    @Autowired
    TClueMapper mapperl;
    @Override
    public IPage<PatrolClue> getList(Page<PatrolClue> page, Integer session, Integer year, Integer round, String groupName, String unitName,String discipline, Integer end,String problem) {
        return patrolMapper.getClue(page,year,session,round,groupName,unitName,discipline,end,problem);
    }

    @Override
    public List<PatrolClue> getList2(Integer session, Integer year, Integer round, String groupName, String unitName, String discipline, Integer end) {
        return patrolMapper.getClue2(year,session,round,groupName,unitName,discipline,end);
    }

    @Override
    public List<TClue> getClues(String patrolid, String reactionPost, String reactionLevel,
                                String discipline, String transferingUnit, String situation,Integer end, Integer twoCategory, Integer firstcategory) {

        return mapperl.getList(patrolid,reactionPost,reactionLevel,discipline,transferingUnit,situation,end,twoCategory,firstcategory);
    }

    @Override
    public List<PatrolClue> lists(Integer session, Integer year, Integer round, String groupName, String unitName) {
        return patrolMapper.getClues(year,session,round,groupName,unitName);
    }

    @Override
    public List<HashMap<String, Object>> getMap(Integer year, Integer session, Integer round, String groupName,
                                          String unitName, String disciplineid, Integer firstid) {
        return mapperl.getMap(year,session,round,groupName,unitName,disciplineid,firstid);
    }

    @Override
    public HashMap<String, Object> geunums(Integer session, Integer round, Integer firstid) {
        return mapperl.getnums(session,round,firstid);
    }


}
