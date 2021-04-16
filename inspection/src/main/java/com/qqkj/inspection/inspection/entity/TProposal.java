package com.qqkj.inspection.inspection.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 移交问题建议表
 * </p>
 *
 * @author qqkj
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TProposal implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 巡察id
     */
    @ApiModelProperty(value = "巡察id")
    private String patrolId;

    /**
     * 移交单位
     */
    @ApiModelProperty(value = "移交单位")
    private String transferingUnit;

    /**
     * 移交时间
     */
    @ApiModelProperty(value = "移交时间")
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime transferingTime;

    /**
     * 移交建议数
     */
    @ApiModelProperty(value = "移交建议数")
    private Integer transferingNum;

    /**
     * 是否办结  0未结束 1结束
     */
    @ApiModelProperty(value = "是否办结  0未结束 1结束")
    private String end;

    /**
     * 具体办理情况
     */
    @ApiModelProperty(value = "具体办理情况")
    private String message;

    /**
     * 成果运营情况
     */
    @ApiModelProperty(value = "成果运营情况")
    private String information;

    /**
     * 期限提醒
     */
    @ApiModelProperty(value = "期限提醒")
    private String remind;


}
