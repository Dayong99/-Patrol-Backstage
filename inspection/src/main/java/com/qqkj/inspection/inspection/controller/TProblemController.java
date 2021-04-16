package com.qqkj.inspection.inspection.controller;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qqkj.inspection.common.dto.ResultVO;
import com.qqkj.inspection.common.easyExcel.util.LocalDateTimeConverter;
import com.qqkj.inspection.common.exception.OilException;
import com.qqkj.inspection.inspection.entity.*;
import com.qqkj.inspection.inspection.service.*;
import com.qqkj.inspection.inspection.service.impl.TPatrolServiceImpl;
import com.qqkj.inspection.inspection.until.Bzi2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;


/**
 * <p>
 * 巡察问题表 前端控制器
 * </p>
 *
 * @author qqkj
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/inspection/t-problem")
@Api(tags = {"问题清单"})
@Slf4j
public class TProblemController {
    private String message;

    @Autowired
    private ITProblemService problemService;
    @Autowired
    private ITLabelService labelService;
    @Value("${qqkj.fileDir}")
    private String fileDir;

    @Autowired
    private ITUnitService unitService;
    @Autowired
    TPatrolServiceImpl patrolService;
    @Autowired
    ITPatrolUnitService patrolUnitService;
    @Autowired
    private ITMaterialService materialService;
    @Autowired
    private ITSpecialService specialService;
    @Autowired
    private ITLixingService lixingService;
    @Autowired
    private ITClueService clueService;
    @Autowired
    private ITProposalService proposalService;
    @Autowired
    private ITReportService reportService;
    @Autowired
    private ITAttachService attachService;
    @Autowired
    ITSpecialService service;

    static char[] cnArr = new char[]{'一', '二', '三', '四', '五', '六', '七', '八', '九'};
    static char[] chArr = new char[]{'十', '百', '千', '万', '亿'};
    static String allChineseNum = "零一二三四五六七八九十百千万亿";


    @GetMapping
    @ApiOperation(value = "根据条件查询问题")
    public ResultVO list(@ApiParam(value = "每一页数量") @RequestParam Integer size,
                         @ApiParam(value = "当前所在页") @RequestParam Integer current,
                         @ApiParam(value = "年度") @RequestParam(required = false) Integer year,
                         @ApiParam(value = "届次") @RequestParam(required = false) Integer session,
                         @ApiParam(value = "轮次") @RequestParam(required = false) Integer round,
                         @ApiParam(value = "巡察单位") @RequestParam(required = false) String name,
                         @ApiParam(value = "巡察组") @RequestParam(required = false) String groupName,
                         @ApiParam(value = "一级分类名称") @RequestParam(required = false) String firstLabel,
                         @ApiParam(value = "二级分类名称") @RequestParam(required = false) String secondLabel,
                         @ApiParam(value = "其他标签名称") @RequestParam(required = false) String otherLabel,
                         @ApiParam(value = "村级/区级") @RequestParam String sort,
                         @ApiParam("问题信息模糊查询")@RequestParam(required = false) String message) {
        Page<ProblemPatrol> problemPatrolPage = new Page<>();
        problemPatrolPage.setSize(size).setCurrent(current);
        QueryWrapper<ProblemPatrol> problemPatrolQueryWrapper = new QueryWrapper<>();
        if (session != null) {
            problemPatrolQueryWrapper.eq("session", session);
        }
        if (year != null) {
            problemPatrolQueryWrapper.eq("year", year);
        }
        if (round != null) {
            problemPatrolQueryWrapper.eq("round", round);
        }
        if (name != null && !name.equals("")) {
            problemPatrolQueryWrapper.like("u.name", name);
        }
        if (groupName != null) {
            problemPatrolQueryWrapper.like("group_name", groupName);
        }
        if (firstLabel != null && !firstLabel.equals("")) {
            problemPatrolQueryWrapper.eq("first_category", firstLabel);
        }
        if (secondLabel != null && !secondLabel.equals("")) {
            problemPatrolQueryWrapper.eq("two_category", secondLabel);
        }
        if (otherLabel != null && !otherLabel.equals("")) {
            problemPatrolQueryWrapper.eq("other_category", otherLabel);
        }
        if (message !=null && !message.equals("")){
            problemPatrolQueryWrapper.like("message",message);
        }

        problemPatrolQueryWrapper.eq("sort", sort);
        IPage<ProblemPatrol> query = problemService.query(problemPatrolPage, problemPatrolQueryWrapper);
        return ResultVO.success(query);
    }

    @GetMapping("getById")
    @ApiOperation(value = "根据id查询单个问题")
    public ResultVO getById(String id) {
        return ResultVO.success(problemService.getById(id));
    }

    @PostMapping
    @ApiOperation(value = "添加一个问题")
    public ResultVO add(TProblem problem) {
        problem.setCreatTime(LocalDateTime.now());
        problemService.save(problem);
        return ResultVO.success(problem.getId());
    }

    @PutMapping
    @ApiOperation(value = "修改一个问题")
    public ResultVO put(TProblem problem) {
        UpdateWrapper<TProblem> tProblemUpdateWrapper = new UpdateWrapper<>();
        tProblemUpdateWrapper.eq("id", problem.getId());
        TProblem byId = problemService.getById(problem.getId());
        if (byId.getFirstCategory() != null) {
            if (!byId.getFirstCategory().equals(problem.getFirstCategory()) && problem.getFirstCategory() != null) {
                tProblemUpdateWrapper.set("two_category", null);
            }
        }
        return ResultVO.success(problemService.update(problem, tProblemUpdateWrapper));
    }

    @PutMapping("putAll")
    @ApiOperation(value = "清空一行问题")
    public ResultVO putAll(@ApiParam(value = "问题表id") @RequestParam String id) {
        UpdateWrapper<TProblem> tProblemUpdateWrapper = new UpdateWrapper<>();
        tProblemUpdateWrapper.set("stress", null).set("message", null).set("first_category", null)
                .set("two_category", null).set("other_category", null).set("creat_time", null).eq("id", id);
        return ResultVO.success(problemService.update(tProblemUpdateWrapper));
    }



    //智能分析
    @GetMapping("analysis")
    @ApiOperation("智能分析")
    public ResultVO analysis(Integer session,Integer year,Integer round,String unitName,Integer groupName,Integer firstId,String sort){
        List<HashMap<String, Object>>maps=problemService.getMap(year,session,round,groupName,unitName,firstId,sort);

        if (session!=null && round==null){
            QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("round")
                    .groupBy("round")
                    .orderByAsc("round");
            if (session!=null){
                queryWrapper.eq("session",session);
            }
            List<TPatrol> list = patrolService.list(queryWrapper);
            HashMap<String, Object>i=new HashMap<>();
            List<Integer>indes=new ArrayList<>();
            for (TPatrol patrol:list){
                indes.add(patrol.getRound());
            }
            i.put("rounds",indes);
            maps.add(i);
        }

        if (round!=null && session==null){
            QueryWrapper<TPatrol> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("session")
                    .groupBy("session")
                    .orderByAsc("session");
            if (session!=null){
                queryWrapper.eq("round",round);
            }
            List<TPatrol> list = patrolService.list(queryWrapper);
            HashMap<String, Object>i=new HashMap<>();
            List<Integer>indes=new ArrayList<>();
            for (TPatrol patrol:list){
                indes.add(patrol.getSession());
            }
            i.put("sessions",indes);
            maps.add(i);

        }
        return ResultVO.success(maps);
    }

    @GetMapping("counts")
    @ApiOperation("查询全部数量")
    public ResultVO getCounts(Integer session,Integer round,Integer firstId,String sort){
        return ResultVO.success(problemService.getNums(session,round,firstId,sort));
    }


    @DeleteMapping
    @ApiOperation(value ="删除一个问题")
    public ResultVO delete(String id){
        return ResultVO.success(problemService.removeById(id));
    }


    @PostMapping("inserts")
    public ResultVO inserts(@RequestParam("file") MultipartFile file){
        Workbook book;
        String fileName = file.getOriginalFilename();
        String filePath = fileDir + "problem/" + LocalDateTime.now().getYear() + "/" + LocalDateTime.now().getMonthValue() + "/" + LocalDateTime.now().getDayOfMonth() + "/";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            book = Workbook.getWorkbook(dest);
            //获取所有工作簿
            Sheet[] sheets = book.getSheets();
            for (Sheet sheet:sheets){
                //工作簿行与列
                int rows = sheet.getRows();
                int clomns = sheet.getColumns();
                System.out.println("row:" + rows);
                System.out.println("clomns:" + clomns);
                for (int i=1;i<rows;i++){
                    Cell[] cells = sheet.getRow(i);
                    String s=cells[0].getContents();
                    String sessionStr=cells[0].getContents().replace("届","").replace("第","");
                    boolean b=isInteger(sessionStr);
                    Integer session=null;
                    if (b==true){
                        session=Integer.parseInt(sessionStr);
                    }else {
                        session=chineseNumToArabicNum(sessionStr);
                    }
                    String yearStr=cells[1].getContents().replace("年","").replace("第","");
                    b=isInteger(sessionStr);
                    Integer year=null;
                    if (b==true){
                        year=Integer.parseInt(yearStr);
                    }else {
                        year=chineseNumToArabicNum(yearStr);
                    }
                    String roundStr=cells[2].getContents().replace("轮","").replace("第","");
                    b=isInteger(sessionStr);
                    Integer round=null;
                    if (b==true){
                        round=Integer.parseInt(roundStr);
                    }else {
                        round=chineseNumToArabicNum(roundStr);
                    }
                    String groupName=cells[3].getContents().replace("组","").replace("第","");
                    b=isInteger(groupName);
                    Integer group=null;
                    if (b==true){
                        group=Integer.parseInt(roundStr);
                    }else {
                        group=chineseNumToArabicNum(roundStr);
                    }
                    //巡察对象名称
                    String name=cells[4].getContents();
                    String sort=cells[5].getContents();
                    TUnit tUnit = new TUnit().setName(name).setSort(sort);
                    QueryWrapper<TUnit> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("name", tUnit.getName());
                    TUnit unit = unitService.getOne(queryWrapper);
                    if (unit == null) {
                        unitService.save(tUnit);
                    } else {
                       tUnit.setId(unit.getId());
                    }


                    TPatrol patrol=new TPatrol();
                    patrol.setRound(round);
                    patrol.setSession(session);
                    patrol.setGroupName(group);
                    patrol.setYear(year);


                    QueryWrapper<TPatrol> patrolQueryWrapper = new QueryWrapper<>();
                    patrolQueryWrapper
                            .eq("session", patrol.getSession())
                            .eq("year", patrol.getYear())
                            .eq("round", patrol.getRound())
                            .eq("group_name", patrol.getGroupName());
                    //查询是否有重复
                    HashMap<String, String> map = new HashMap<>();
                    TPatrol patrol1 = patrolService.getOne(patrolQueryWrapper);
                    if (patrol1 == null) {
                        String id = UUID.randomUUID().toString().replace("-", "");
                        patrol.setId(id);
                        patrolService.save(patrol);
                        //判断是否需要修改patrolUnit关系表
                        if (name!= null) {
                            QueryWrapper<TUnit> tUnitQueryWrapper = new QueryWrapper<>();
                            tUnitQueryWrapper.eq("name", name);
                            TUnit one = unitService.getOne(tUnitQueryWrapper);
                            //判断unit中是否有该对象
                            if (one != null) {
                                TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol.getId()).setUnitId(one.getId());
                                patrolUnitService.save(patrolUnit);
                                map = addAll(map, patrolUnit.getId());
                                map.put("patrolId", patrol.getId());
                            } else {
                                //没有unit对象就先创建
                                unitService.save(tUnit);
                                TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol.getId()).setUnitId(tUnit.getId());
                                patrolUnitService.save(patrolUnit);
                                map = addAll(map, patrolUnit.getId());
                                map.put("unitId", tUnit.getId());
                                map.put("patrolId", patrol.getId());
                            }
                        }
                    } else {
                        //判断是否需要修改patrolUnit关系表
                        if (name != null) {
                            QueryWrapper<TUnit> tUnitQueryWrapper = new QueryWrapper<>();
                            tUnitQueryWrapper.eq("name",name);
                            TUnit one = unitService.getOne(tUnitQueryWrapper);
                            //判断unit中是否有该对象
                            if (one != null) {
                                QueryWrapper<TPatrolUnit> tPatrolUnitQueryWrapper = new QueryWrapper<>();
                                tPatrolUnitQueryWrapper.eq("unit_id", one.getId()).eq("patrol_id", patrol1.getId());
                                TPatrolUnit one1 = patrolUnitService.getOne(tPatrolUnitQueryWrapper);
                                //判断关系表是否存在
                                if (one1 != null) {
                                    TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol1.getId()).setUnitId(one.getId()).setId(one1.getId());
                                    patrolUnitService.updateById(patrolUnit);
                                    map.put("unitId", one.getId());
                                    map.put("patrolUnitId", one1.getId());
                                    //查询问题表中的数据
                                    QueryWrapper<TProblem> problemQueryWrapper = new QueryWrapper<>();
                                    problemQueryWrapper.eq("patrol_id", one1.getId());
                                    TProblem problem = problemService.getOne(problemQueryWrapper);
                                    if (problem != null) {
                                        map.put("problemId", problem.getId());
                                    } else {

                                            TProblem problem1 = new TProblem().setPatrolId(one1.getId()).setCreatTime(LocalDateTime.now());
                                            problemService.save(problem1);
                                            map.put("problemId", problem1.getId());
                                    }
                                } else {
                                    TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol1.getId()).setUnitId(one.getId());
                                    patrolUnitService.save(patrolUnit);
                                    map = addAll(map, patrolUnit.getId());
                                }
                                map.put("patrolId", patrol1.getId());
                            } else {
                                //没有unit对象就先创建
                                unitService.save(tUnit);
                                TPatrolUnit patrolUnit = new TPatrolUnit().setPatrolId(patrol1.getId()).setUnitId(tUnit.getId());
                                patrolUnitService.save(patrolUnit);
                                map = addAll(map, patrolUnit.getId());
                                map.put("unitId", tUnit.getId());
                                map.put("patrolId", patrol.getId());
                            }
                        }
                    }



                    TProblem problem = new TProblem();
                    String stress1=cells[6].getContents();
                    Integer stress=null;
                    if (stress1.equals("重点")){
                        stress=1;
                    }else{
                        stress=0;
                    }
                    problem.setStress(stress);
                    problem.setMessage(cells[7].getContents());

                    String label1=cells[8].getContents();
                    QueryWrapper<TLabel> tLabelQueryWrapper1 = new QueryWrapper<>();
                    tLabelQueryWrapper1.eq("result",label1).isNull("parentid").eq("labletype",1);
                    TLabel tLabel1 = labelService.getOne(tLabelQueryWrapper1);
                    TLabel labelOne = new TLabel();
                    if (tLabel1==null){
                        QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
                        labelQueryWrapper
                                .isNull("parentid")
                                .eq("labletype",1);
                        List<TLabel> list=labelService.list(labelQueryWrapper);
                        Integer size1=list.size()+1;
                        labelOne.setLabletype(1).setLevel(1).setOrdernum(size1).setResult(label1);
                        labelService.save(labelOne);
                    }
                    problem.setFirstCategory(tLabel1==null?labelOne.getId():tLabel1.getId());


                    String label2=cells[9].getContents();
                    QueryWrapper<TLabel> tLabelQueryWrapper2 = new QueryWrapper<>();
                    tLabelQueryWrapper2.eq("result",label2).eq("parentid",tLabel1==null?labelOne.getId():tLabel1.getId()).eq("labletype",1);
                    TLabel tLabel2 = labelService.getOne(tLabelQueryWrapper2);
                    TLabel labelTwo = new TLabel();
                    if (tLabel2==null){
                        QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
                        labelQueryWrapper
                                .eq("parentid",tLabel1==null?labelOne.getId():tLabel1.getId())
                                .eq("labletype",1);
                        List<TLabel> list=labelService.list(labelQueryWrapper);
                        Integer size1=list.size()+1;
                        labelTwo.setLabletype(1).setLevel(2).setOrdernum(size1).setResult(label2).setParentid(tLabel1==null?labelOne.getId():tLabel1.getId());
                        labelService.save(labelTwo);
                    }
                    problem.setTwoCategory(tLabel2==null?labelTwo.getId():tLabel2.getId());

                    String label3=cells[10].getContents();
                    QueryWrapper<TLabel> tLabelQueryWrapper3 = new QueryWrapper<>();
                    tLabelQueryWrapper3.eq("result",label3).eq("labletype",3);
                    TLabel tLabelOther = labelService.getOne(tLabelQueryWrapper3);
                    TLabel labelOther = new TLabel();
                    if (tLabelOther==null){
                        QueryWrapper<TLabel> labelQueryWrapper=new QueryWrapper<>();
                        labelQueryWrapper
                                .isNull("parentid")
                                .eq("labletype",3);
                        List<TLabel> list=labelService.list(labelQueryWrapper);
                        Integer size1=list.size()+1;
                        labelOther.setLabletype(3).setLevel(1).setOrdernum(size1).setResult(label3);
                        labelService.save(labelOther);
                    }
                    problem.setOtherCategory(tLabelOther==null?labelOther.getId():tLabelOther.getId());
                    String problemId = map.get("problemId");
                    String patrounitid=map.get("patrolUnitId");
                    problem.setId(problemId);
                    problem.setPatrolId(patrounitid);
                    boolean bb=problemService.updateById(problem);
                    if (bb==true){
                        log.info("插入线索 ："+problem);
                    }
                }
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return null;
    }


    public HashMap addAll(HashMap map,String id){
        map.put("patrolUnitId",id);
        //初始化problem表
        TProblem problem = new TProblem().setPatrolId(id).setCreatTime(LocalDateTime.now());
        problemService.save(problem);
        map.put("problemId",problem.getId());
        return map;
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static int chineseNumToArabicNum(String chineseNum) {
        int result = 0;
        int temp = 1;//存放一个单位的数字如：十万
        int count = 0;//判断是否有chArr
        for (int i = 0; i < chineseNum.length(); i++) {
            boolean b = true;//判断是否是chArr
            char c = chineseNum.charAt(i);
            for (int j = 0; j < cnArr.length; j++) {//非单位，即数字
                if (c == cnArr[j]) {
                    if(0 != count){//添加下一个单位之前，先把上一个单位值添加到结果中
                        result += temp;
                        temp = 1;
                        count = 0;
                    }
                    // 下标+1，就是对应的值
                    temp = j + 1;
                    b = false;
                    break;
                }
            }
            if(b){//单位{'十','百','千','万','亿'}
                for (int j = 0; j < chArr.length; j++) {
                    if (c == chArr[j]) {
                        switch (j) {
                            case 0:
                                temp *= 10;
                                break;
                            case 1:
                                temp *= 100;
                                break;
                            case 2:
                                temp *= 1000;
                                break;
                            case 3:
                                temp *= 10000;
                                break;
                            case 4:
                                temp *= 100000000;
                                break;
                            default:
                                break;
                        }
                        count++;
                    }
                }
            }
            if (i == chineseNum.length() - 1) {//遍历到最后一个字符
                result += temp;
            }
        }
        return result;
    }

    /**
     * 导出 Excel
     */
    @PostMapping("export")
    @ApiOperation("导出 Excel")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "年度") @RequestParam(required = false) Integer year,
                       @ApiParam(value = "届次") @RequestParam(required = false) Integer session,
                       @ApiParam(value = "轮次") @RequestParam(required = false) Integer round,
                       @ApiParam(value = "巡察单位") @RequestParam(required = false) String name,
                       @ApiParam(value = "巡察组") @RequestParam(required = false) String groupName,
                       @ApiParam(value = "一级分类名称") @RequestParam(required = false) String firstLabel,
                       @ApiParam(value = "二级分类名称") @RequestParam(required = false) String secondLabel,
                       @ApiParam(value = "其他标签名称") @RequestParam(required = false) String otherLabel,
                       @ApiParam(value = "村级/区级") @RequestParam String sort)
            throws OilException {
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-disposition",
                String.format("attachment; filename=\"%s\"", "problem.xlsx"));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        try {
            Page<ProblemPatrol> problemPatrolPage = new Page<>();
            QueryWrapper<ProblemPatrol> problemPatrolQueryWrapper = new QueryWrapper<>();
            if (session != null) {
                problemPatrolQueryWrapper.eq("session", session);
            }
            if (year != null) {
                problemPatrolQueryWrapper.eq("year", year);
            }
            if (round != null) {
                problemPatrolQueryWrapper.eq("round", round);
            }
            if (name != null && !name.equals("")) {
                problemPatrolQueryWrapper.like("u.name", name);
            }
            if (groupName != null) {
                problemPatrolQueryWrapper.like("group_name", groupName);
            }
            if (firstLabel != null && !firstLabel.equals("")) {
                problemPatrolQueryWrapper.eq("first_category", firstLabel);
            }
            if (secondLabel != null && !secondLabel.equals("")) {
                problemPatrolQueryWrapper.eq("two_category", secondLabel);
            }
            if (otherLabel != null && !otherLabel.equals("")) {
                problemPatrolQueryWrapper.eq("other_category", otherLabel);
            }
            problemPatrolQueryWrapper.eq("sort", sort);
            List<ProblemPatrol> query = problemService.query2(problemPatrolQueryWrapper);
            EasyExcel.write(response.getOutputStream(), ProblemPatrol.class).registerConverter(new LocalDateTimeConverter()).sheet("写入").doWrite(query);
        } catch (Exception e) {
            message = "导出Excel失败";
            log.error(message, e);
            throw new OilException(message);
        }
    }




}
