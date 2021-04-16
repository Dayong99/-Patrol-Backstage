package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.qqkj.inspection.common.easyExcel.util.SettlementConverter;
import com.wuwenze.poi.annotation.ExcelField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 巡察问题表
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TProblem implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    @ExcelIgnore
    @ApiModelProperty(value = "巡察问题表id")
    private String id;

    /**
     * 巡察id
     */
    @ApiModelProperty(value = "巡察表和巡察对象关系表id")
    @ExcelIgnore
    private String patrolId;

    /**
     * 是否是重点  1.重点 0.不重点
     */

    @ApiModelProperty(value = "是否是重点  1.重点 0.不重点")
    @ExcelProperty(value = "是否是重点", index = 6,converter = SettlementConverter.class)
    private Integer stress;

    /**
     * 问题
     */
    @ApiModelProperty(value = "问题")
    @ExcelProperty(value = "问题", index = 7)
    private String message;

    /**
     * 一级分类
     */
    @ApiModelProperty(value = "一级分类id")
    @ExcelIgnore
    private Integer firstCategory;

    /**
     * 二级分类
     */
    @ApiModelProperty(value = "二级分类id")
    @ExcelIgnore
    private Integer twoCategory;

    /**
     * 其他标签
     */
    @ApiModelProperty(value = "其他标签id")
    @ExcelIgnore
    private Integer otherCategory;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间，后台处理了")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ExcelIgnore
    private LocalDateTime creatTime;
}
