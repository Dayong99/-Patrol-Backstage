package com.qqkj.inspection.inspection.service.impl;

import com.qqkj.inspection.inspection.entity.TUserToken;
import com.qqkj.inspection.inspection.mapper.TUserTokenMapper;
import com.qqkj.inspection.inspection.service.ITUserTokenService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 * 系统用户Token 服务实现类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-24
 */
@Service
public class TUserTokenServiceImpl extends ServiceImpl<TUserTokenMapper, TUserToken> implements ITUserTokenService {

    @Override
    public void logout(Long id) {
        //修改token
        TUserToken tokenEntity = new TUserToken();
        tokenEntity.setUserId(id);
        tokenEntity.setToken(UUID.randomUUID().toString().replace("-",""));
        this.updateById(tokenEntity);
    }
}
