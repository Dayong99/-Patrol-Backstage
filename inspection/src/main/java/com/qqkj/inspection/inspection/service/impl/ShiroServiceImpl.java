package com.qqkj.inspection.inspection.service.impl;

import com.qqkj.inspection.inspection.entity.TUser;
import com.qqkj.inspection.inspection.entity.TUserToken;
import com.qqkj.inspection.inspection.mapper.TUserMapper;
import com.qqkj.inspection.inspection.mapper.TUserTokenMapper;
import com.qqkj.inspection.inspection.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private TUserMapper sysUserDao;

    @Autowired
    private TUserTokenMapper sysUserTokenDao;

    @Override
    public TUserToken queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);

    }

    @Override
    public TUser queryUser(Long userId) {
        return sysUserDao.selectById(userId);

    }
}
