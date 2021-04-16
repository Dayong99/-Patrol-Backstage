package com.qqkj.inspection.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;


@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //插入填充策略
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        //setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject)
        this.setFieldValByName("datatime", LocalDateTime.now(),metaObject);
    }

    //更新填充测量
    public void updateFill(MetaObject metaObject) {

    }
}
