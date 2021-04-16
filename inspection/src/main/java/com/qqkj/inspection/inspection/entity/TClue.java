package com.qqkj.inspection.inspection.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 线索办理表
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TClue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 巡察id
     */
    @ApiModelProperty(value = "巡察id")
    private String patrolId;

    /**
     * 反应人姓名
     */
    @ApiModelProperty(value = "反应人姓名")
    private String reactionName;

    /**
     * 反应人职务
     */
    @ApiModelProperty(value = "反应人职务")
    private String reactionPost;

    /**
     * 反应人级别
     */
    @ApiModelProperty(value = "反应人级别")
    private String reactionLevel;

    /**
     * 具体问题
     */
    @ApiModelProperty(value = "具体问题")
    private String problem;

    /**
     * 六项纪律
     */
    @ApiModelProperty(value = "六项纪律")
    private String discipline;
    @TableField("first_category")

    @ApiModelProperty(value = "一级分类id")
    private Integer firstcategory;
    @ApiModelProperty(value = "一级分类名称 no")
    @TableField(exist = false)
    private String firstcategoryStr;


    @TableField("handover_type")
    @ApiModelProperty(value = "移交方式")
    private String handoverType;
    /**
     * 二级分类
     */
    @ApiModelProperty(value = "二级分类id")
    private Integer twoCategory;
    @TableField(exist = false)
    @ApiModelProperty(value = "二级分类名称 no")
    private String twoCategoryStr;
    /**
     * 移交时间
     */
    @ApiModelProperty(value = "移交时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime handoverTime;

    /**
     * 分类处理意见
     */
    @ApiModelProperty(value = "分类处理意见")
    private String classification;

    /**
     * 移交单位
     */
    @ApiModelProperty(value = "移交单位")
    private String transferingUnit;

    /**
     * 处理结果
     */
    @ApiModelProperty(value = "处理结果")
    private String result;

    /**
     * 办理情况
     */
    @ApiModelProperty(value = "办理情况")
    private String situation;

    /**
     * 是否结束:0未结束 1结束
     */
    @ApiModelProperty(value = "是否结束:0未结束 1结束")
    private Integer end;


}
