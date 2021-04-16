package com.qqkj.inspection.inspection.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.ITLabelService;
import com.qqkj.inspection.inspection.service.ITProblemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-26
 */
@RestController
@RequestMapping("/inspection/t-label")
@Api(tags = {"标签/处理结果接口"})
public class TLabelController {
    @Autowired
    private ITLabelService labelService;
    @Autowired
    private ITProblemService problemService;

    @GetMapping("getLable1")
    @ApiOperation(value = "查询下拉列表")
    public ResultVO getLable1(@ApiParam(value = "类型：0.处理结果 1.标签分类2.六项纪律3.其他标签4.线索分类")@RequestParam int labletype,
                              @ApiParam(value = "类型：1.一级分类2.二级分类")@RequestParam Integer type,
                              @ApiParam(value = "如果是二级分类就需要传递父id")@RequestParam(required = false) Integer parentid){
        QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
        if (type==1){
            labelQueryWrapper.select("result","id")
                    .isNull("parentid")
                    .eq("labletype",labletype);
        }else {
            labelQueryWrapper.select("result")
                    .eq("parentid",parentid)
                    .eq("labletype",labletype);
        }
        List<TLabel> list=labelService.list(labelQueryWrapper);
        return ResultVO.success(list);
    }

    @DeleteMapping
    @ApiOperation(value = "删除一个标签或者处理结果")
    public ResultVO delete(Integer id){
        QueryWrapper<TLabel> tLabelQueryWrapper = new QueryWrapper<>();
        remove(id,"first_category");
        remove(id,"two_category");
        remove(id,"other_category");
        //如果删除一级标签，二级标签全部删除
        tLabelQueryWrapper.eq("parentid",id);
        List<TLabel> list = labelService.list(tLabelQueryWrapper);
        for (TLabel label:list){
            remove(label.getId(),"first_category");
            remove(label.getId(),"two_category");
            remove(label.getId(),"other_category");
        }
        labelService.remove(tLabelQueryWrapper);
        return ResultVO.success(labelService.removeById(id));
    }

    public void remove(Integer id, String label){
        QueryWrapper<TProblem> problemQueryWrapper = new QueryWrapper<>();
        problemQueryWrapper.eq(label,id);
        List<TProblem> list = problemService.list(problemQueryWrapper);
        for (TProblem problem:list){
            UpdateWrapper<TProblem> tProblemUpdateWrapper = new UpdateWrapper<>();
            tProblemUpdateWrapper.set(label,null).eq("id",problem.getId());
            problemService.update(tProblemUpdateWrapper);
        }
    }


    @PostMapping("addAll")
    @ApiOperation(value = "添加一个一级和多个二级")
    public ResultVO addAll(@ApiParam(value = "一级名称")@RequestParam String labelName1,
                           @ApiParam(value = "多个二级名称")@RequestParam(required = false) String[] labelName2s,
                           @ApiParam(value = "如果直接创建二级，需要传入父id")@RequestParam(required = false) Integer parentId,
                           @ApiParam(value = "类型：0.处理结果 1.标签分类2.六项纪律3.其他标签4.线索分类")@RequestParam Integer labelType) {
        //判断如果有父id,则直接添加二级标签
        int count =0;  //一共插入多少条数据
        if (parentId!=null){
            QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
            labelQueryWrapper.eq("parentid",parentId)
                    .eq("labletype",labelType);
            List<TLabel> labels=labelService.list(labelQueryWrapper);
            int size2=labels.size()+1;
            if (labelName2s!=null){
                count=add(labelName2s,labelType,parentId,size2,count);
            }else{
                //如果为空
                return ResultVO.error(50000,"数据为空");
            }
            //"插入成功"+count+"条数据"
            return ResultVO.success(count);
        }else{
            //查询一级数据有无重复
            QueryWrapper<TLabel> tLabelQueryWrapper = new QueryWrapper<>();
            tLabelQueryWrapper.eq("result",labelName1).eq("labletype",labelType);
            TLabel label1= labelService.getOne(tLabelQueryWrapper);
            Integer size2=1;
            //判断一级标签是否重复
            if (label1==null){
                QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
                labelQueryWrapper
                        .isNull("parentid")
                        .eq("labletype",labelType);
                List<TLabel> list=labelService.list(labelQueryWrapper);
                Integer size1=list.size()+1;
                //一级数据
                TLabel tLabel1 = new TLabel().setLabletype(labelType).setLevel(1).setOrdernum(size1).setResult(labelName1);
                labelService.save(tLabel1);
                count++;
                if (labelName2s!=null)
                    count=add(labelName2s,labelType,tLabel1.getId(),size2,count);
                return ResultVO.success(count);
            }else {
                //如果标签重复，则添加二级标签
                if (labelName2s!=null){
                    count=add(labelName2s,labelType,label1.getId(),size2,count);
                    return ResultVO.success(count);
                }else{
                    //如果为空
                    return ResultVO.error(50000,"数据为空");
                }
            }
        }
    }

    public Integer add(String[] labelName2s,Integer labelType,Integer parentId,Integer size2,Integer count){
        for (int i=0;i<labelName2s.length;i++){
            QueryWrapper<TLabel> tLabelQueryWrapper = new QueryWrapper<>();
            tLabelQueryWrapper.eq("result",labelName2s[i]).eq("labletype",labelType).eq("parentid",parentId);
            TLabel one = labelService.getOne(tLabelQueryWrapper);
            //判断二级名称是否重复
            if (one==null){
                TLabel tLabel2 = new TLabel().setLabletype(labelType).setLevel(2).setOrdernum(size2).setResult(labelName2s[i]).setParentid(parentId);
                Boolean b2=labelService.save(tLabel2);
                if (b2)
                    count++;
                size2++;
            }
        }
        return count;
    }

    @PutMapping
    @ApiOperation(value = "修改一个标签或者处理结果")
    public ResultVO put(TLabel label) {

        QueryWrapper<TLabel> tLabelQueryWrapper = new QueryWrapper<>();
        tLabelQueryWrapper.eq("result",label.getResult()).eq("labletype",label.getLabletype());
        if(label.getParentid()!=null){
            tLabelQueryWrapper.eq("parentid",label.getParentid());
        }else {
            tLabelQueryWrapper.isNull("parentid");
        }
        TLabel label1= labelService.getOne(tLabelQueryWrapper);
        if (label1==null) {
            return ResultVO.success(labelService.updateById(label));
        }
        return ResultVO.error(50000,"修改失败，数据重复");
    }

    @GetMapping("getResultSix")
    @ApiOperation(value = "查询六项纪律或者其他标签")
    public ResultVO getResultSix( @ApiParam(value = "当前页数")@RequestParam Integer current,
                          @ApiParam(value = "一页多少条数据")@RequestParam Integer size,
                          @ApiParam(value = "类型：0.处理结果 1.标签分类2.六项纪律3.其他标签4.线索分类")@RequestParam Integer labletype,
                          @ApiParam(value = "模糊查询条件")@RequestParam(required = false)String name
                          ){
        //分页
        Page<TLabel> labelPage=new Page<>();
        labelPage.setCurrent(current).setSize(size);
        QueryWrapper<TLabel> labelQueryWrapper = new QueryWrapper<>();
        if (name!=null&&name!=""){
            labelQueryWrapper.like("result",name);
        }
        labelQueryWrapper.eq("labletype",labletype)
        .orderByDesc("ordernum");
        return  ResultVO.success(labelService.page(labelPage,labelQueryWrapper));
    }

    @GetMapping("getResult")
    @ApiOperation(value = "查询处理结果或者线索分类或者标签分类")
    public ResultVO getResult( @ApiParam(value = "当前页数")@RequestParam Integer current,
                          @ApiParam(value = "一页多少条数据")@RequestParam Integer size,
                          @ApiParam(value = "类型：0.处理结果 1.标签分类2.六项纪律3.其他标签4.线索分类")@RequestParam Integer labletype,
                          @ApiParam(value = "模糊查询条件")@RequestParam(required = false)String name){
        //分页
        Page<TLabel> labelPage=new Page<>();
        labelPage.setCurrent(current).setSize(size);
        QueryWrapper<TLabel> labelQueryWrapper = new QueryWrapper<>();
        if (name!=null&&name!=""){
            labelQueryWrapper.like("l1.result",name).or().like("l2.result",name);
        }
        return  ResultVO.success(labelService.query(labelPage,labelQueryWrapper,labletype));
    }
}
