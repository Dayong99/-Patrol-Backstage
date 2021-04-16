package com.qqkj.inspection.inspection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.inspection.entity.PatrolClue;
import com.qqkj.inspection.inspection.entity.TClue;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 线索办理表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
public interface ITClueService extends IService<TClue> {
    IPage<PatrolClue> getList(Page<PatrolClue> page, Integer session,
                              Integer year, Integer round, String groupName, String unitName,String discipline,Integer end,String problem);

    List<PatrolClue>getList2(Integer session,
                             Integer year, Integer round, String groupName, String unitName,String discipline,Integer end);
    List<TClue>getClues(String patrolid,String  reactionPost,String reactionLevel,String discipline,String transferingUnit,
                        String situation,Integer end,Integer twoCategory,Integer firstcategory);

    List<PatrolClue>lists(Integer session, Integer year, Integer round, String groupName, String unitName);
    List<HashMap<String,Object>>getMap(@Param("year") Integer year,
                                 @Param("session") Integer session, @Param("round") Integer round,
                                 @Param("groupName") String groupName, @Param("unitName") String unitName,
                                 @Param("disciplineid")String disciplineid,@Param("firstid") Integer firstid);
    HashMap<String,Object>geunums(@Param("session") Integer session, @Param("round") Integer round,@Param("firstid") Integer firstid);
}
