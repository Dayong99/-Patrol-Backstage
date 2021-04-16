package com.qqkj.inspection.inspection.service;

import com.qqkj.inspection.inspection.entity.TUserToken;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户Token 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
public interface ITUserTokenService extends IService<TUserToken> {
    void logout(Long id);
}
