package com.qqkj.inspection.inspection.service;

import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.entity.TUserToken;

public interface ShiroService {
    TUserToken queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    TUser queryUser(Long userId);


}
