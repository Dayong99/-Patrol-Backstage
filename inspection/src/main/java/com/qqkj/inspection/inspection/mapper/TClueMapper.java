package com.qqkj.inspection.inspection.mapper;

import com.qqkj.inspection.inspection.entity.TClue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 线索办理表 Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
public interface TClueMapper extends BaseMapper<TClue> {

    List<TClue>getList(@Param("patrolid") String patrolid,@Param("reactionPost") String  reactionPost,
                       @Param("reactionLevel")String reactionLevel, @Param("discipline")String discipline,@Param("transferingUnit") String transferingUnit,
                       @Param("situation")String situation,@Param("end") Integer end,@Param("twoCategor") Integer twoCategory, @Param("firstcategory")Integer firstcategory);

    List<HashMap<String,Object>>getMap(@Param("year") Integer year,
                                 @Param("session") Integer session, @Param("round") Integer round,
                                 @Param("groupName") String groupName, @Param("unitName") String unitName,
                                 @Param("disciplineid")String disciplineid,@Param("firstid") Integer firstid);
    HashMap<String,Object> getnums(@Param("session") Integer session, @Param("round") Integer round,@Param("firstid") Integer firstid);
}
