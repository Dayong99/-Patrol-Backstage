package com.qqkj.inspection.common.dto;

import com.qqkj.inspection.common.enums.ResultEnum;

import java.io.Serializable;

public class ResultVO<T> implements Serializable
{

    private static final long serialVersionUID = -5897234194040793245L;

    /**
     * 状态吗
     */
    private Integer code;

    /**
     * 状态吗信息
     */
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 具体信息
     */
    private T data;

    public static <T> ResultVO<T> success(T object)
    {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setData(object);
        resultVO.setCode(20000);
        resultVO.setMessage("成功");
        return resultVO;
    }



    public static ResultVO success()
    {
        return success(null);
    }

    public static <T> ResultVO<T> error(Integer code, String msg)
    {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMessage(msg);
        return resultVO;
    }

    public static <T> ResultVO<T> error(ResultEnum resultEnum)
    {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMessage(resultEnum.getMessage());
        return resultVO;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }


    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

}
