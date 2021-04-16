package com.qqkj.inspection.inspection.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "标签分类表id")
    private Integer id;

    /**
     * 处理结果/标签名
     */
    @ApiModelProperty(value = "处理结果/标签名")
    private String result;

    /**
     * 处理结果等级/标签等级
     */
    @ApiModelProperty(value = "处理结果等级/标签等级")
    private Integer level;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer ordernum;

    /**
     * 父id
     */
    @ApiModelProperty(value = "父id")
    private Integer parentid;

    /**
     * 0.线索清单分类  1.问题清单分类2.六项纪律
     */
    @ApiModelProperty(value = "类型：0.处理结果 1.标签分类2.六项纪律3.其他标签4.线索分类")
    private Integer labletype;

}
