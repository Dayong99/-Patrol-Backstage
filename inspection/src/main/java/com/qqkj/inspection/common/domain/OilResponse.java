package com.qqkj.inspection.common.domain;

import java.util.HashMap;

public class OilResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public OilResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public OilResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    @Override
    public OilResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
