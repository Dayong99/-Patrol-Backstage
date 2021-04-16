package com.qqkj.inspection.common.function;


import com.qqkj.inspection.common.exception.RedisConnectException;

@FunctionalInterface
public interface JedisExecutor<T, R> {
    R excute(T t) throws RedisConnectException;
}
