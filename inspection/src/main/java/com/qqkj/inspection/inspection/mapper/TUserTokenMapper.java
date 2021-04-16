package com.qqkj.inspection.inspection.mapper;

import com.qqkj.inspection.inspection.entity.TUserToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 系统用户Token Mapper 接口
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
public interface TUserTokenMapper extends BaseMapper<TUserToken> {
    TUserToken queryByToken(String token);
}
