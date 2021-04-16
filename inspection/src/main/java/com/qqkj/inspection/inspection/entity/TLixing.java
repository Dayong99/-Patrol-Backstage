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
 * 立行立改及查立处表
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TLixing implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 巡察id
     */
    @ApiModelProperty(value = "巡察任务id")
    private String patrolId;



    /**
     * 移交立行立改问题输
     */
    @ApiModelProperty("可移交问题数")
    private Integer problemNum;



    /**
     * 形成报告时间
     */
    @ApiModelProperty("形成报告时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reportTime;

    @ApiModelProperty("1 立行 0立处")
    @TableField("lixing_type")
    private Integer lixingType;
}
