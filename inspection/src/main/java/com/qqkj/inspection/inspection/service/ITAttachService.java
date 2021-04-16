package com.qqkj.inspection.inspection.service;

import com.qqkj.inspection.inspection.entity.TAttach;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
public interface ITAttachService extends IService<TAttach> {

    String getName(String id);
}
