package com.qqkj.inspection.inspection.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MaterialAttach extends  TMaterial {
    private String fileName;
}
