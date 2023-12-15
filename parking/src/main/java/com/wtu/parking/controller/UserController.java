package com.wtu.parking.controller;

import com.wtu.parking.entity.*;
import com.wtu.parking.entity.Record;
import com.wtu.parking.service.impl.GoodService;
import com.wtu.parking.service.impl.RecordService;
import com.wtu.parking.service.impl.UserService;
import com.wtu.parking.utils.ApiResult;
import com.wtu.parking.utils.JwtUtil;
import com.wtu.parking.utils.Page;
import com.wtu.parking.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@CrossOrigin
@Api(tags = "用户相关API")
@Slf4j
@ResponseBody
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RecordService recordService;

    @Autowired
    GoodService goodService;

    @Resource
    private HttpServletResponse response;

    @PostMapping("register/user")
    @ApiOperation(value = "用户注册", notes = "注册成功返回id")
    public ApiResult register(@ApiParam(name = "用户类的json数据 无需提供id") @RequestBody User user) {
        log.info(user.toString());
        if (user.getUSex() == null || user.getUSex() == "") {
            user.setUSex("男");
        }
        Integer res = userService.register(user);
        if (res > 0) {
            return ApiResult.success(res);
        } else if (res == -2) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
        } else {
            return ApiResult.failure(ResultCode.USER_BEEN_REG, null);
        }
    }


    @PostMapping("/login/user")
    @ApiOperation(value = "用户使用 id或电话号码 及 md5密文登录", notes = "请完成对密码的md5加密," +
            "返回空值代表用户不存在或输入有误，否则返回toekn,请加入到请求头Authorization中")
    public ApiResult login(
            @ApiParam(name = "用户id", required = false, defaultValue = "null") @RequestParam(value = "uId", required = false) Integer uId,
            @ApiParam(name = "用户电话号码", required = false, defaultValue = "null") @RequestParam(value = "teleNum", required = false) String teleNum,
            @ApiParam(name = "用户密码MD5密文", required = true) @RequestParam("pwd") String pwd

    ) {
        User user = null;
        if (uId != null) {
            user = userService.findByUid(uId);
            if (user != null) {
                if (!Objects.equals(user.getPwd(), pwd)) {
                    return ApiResult.failure(ResultCode.PWD_ERR, null);
                }
            }
        } else if (user == null && teleNum != null) {
            user = userService.findByTeleNum(teleNum);
            if (user != null) {
                if (!Objects.equals(user.getPwd(), pwd)) {
                    return ApiResult.failure(ResultCode.PWD_ERR, null);
                }
            }
        }
        if (user == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
        log.info(user.toString());
        JwtUtil jwtUtil = new JwtUtil();
        Map<String, String> stringMap = new TreeMap<>();
        stringMap.put("pwd", user.getPwd());
        stringMap.put("id", String.valueOf(user.getUId()));
        String token = jwtUtil.createToken(stringMap);
        log.info("token=" + token);
        return ApiResult.success(token);
    }


    @GetMapping("/user/records/{uId}")
    @ApiOperation(value = "查询该用户的所有停车记录", notes = "若用户不存在或没有记录返回null")
    ApiResult getAllRecords(@ApiParam(name = "用户id") @PathVariable(name = "uId") Integer uId) {
        List<Record> records = recordService.getByUId(uId);
        if (records == null || records.size() == 0) {
            return ApiResult.failure(ResultCode.NO_SUCH_REC, null);
        } else {
            return ApiResult.success(records);
        }
    }


    @PutMapping("user/leave/{uId}/{leaveGId}")
    @ApiOperation(value = "车辆离场", notes = "成功返回离场后的记录，失败原因见状态码")
    public ApiResult leave(
            @ApiParam(name = "用户id") @PathVariable("uId") Integer uId,
            @ApiParam(name = "离场闸机编号") @PathVariable("leaveGId") Integer leaveGId
    ) {
        Record record = null;
        try {
            record = recordService.leave(uId, leaveGId);
        } catch (RuntimeException re) {
            return ApiResult.failure(ResultCode.NO_SUCH_REC.getCode(), re.getMessage(), null);
        }
        return ApiResult.success(record);
    }


    @PostMapping("user/enter")
    @ApiOperation(value = "车辆入场", notes = "成功返回入场后的记录，失败原因见状态码")
    public ApiResult enter(
            @ApiParam(name = "记录的JSON表单 其中车位（可选），进场时间、离场时间、价格无需填写") @RequestBody Record record
    ) {
        log.info(record.toString());
        Integer x = null;
        try {
            x = recordService.enter(record);
            log.info(String.valueOf(x));
        } catch (Exception re) {
            return ApiResult.failure(ResultCode.CAR_IN_LOT.getCode(),re.getMessage(), null);
        }
        return ApiResult.success(record);
    }

    @PutMapping("user/recharge/{uId}/{amount}")
    @ApiOperation(value = "用户充值", notes = "充值成功返回本次的充值金额，失败返回=-2")
    public ApiResult recharge(
            @ApiParam(name = "充值金额") @PathVariable("amount") Integer amount,
            @ApiParam(name = "用户id") @PathVariable("uId") Integer uId
    ) {
        Integer res = userService.charge(amount, uId);
        return ApiResult.success(amount) ;

    }

    @GetMapping("user/chargeHistory/{uId}")
    @ApiOperation(value = "获取当前用户的所有充值记录", notes = "")
    public ApiResult chargeHistory(@ApiParam(name = "用户id") @PathVariable("uId") Integer uId) {
        List<RechargeHistory> histories = userService.getAllHisByUser(uId);
        if (histories == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
        return ApiResult.success(histories);
    }


    @GetMapping("goodsById/{gId}")
    @ApiOperation(value = "根据商品id获取商品")
    public ApiResult getGoodsById(@ApiParam(name = "商品id") @PathVariable("gId") Integer gId) {
        Goods goods = goodService.findByGid(gId);
        if (goods != null) {
            return ApiResult.success(goods);
        } else {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST.getCode(), "无该商品", null);
        }
    }

    @GetMapping("goodsById/{pageSize}/{pageNum}")
    @ApiOperation(value = "分页查询商品")
    public ApiResult getGoodsByPage(
            @ApiParam("分页大小") @PathVariable("pageSize") Integer pageSize,
            @ApiParam("当前分页") @PathVariable("pageNum") Integer pageNum
    ) {
        Page<Goods> page = goodService.getPage(pageSize, pageNum);
        if (page == null) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
        } else {
            return ApiResult.success(page);
        }
    }

    @PutMapping("user/renew/{uId}/{effective}")
    @ApiOperation(value = "月卡充值", notes = "必须是已经有月卡的用户才能充值")
    public ApiResult renew(
            @ApiParam("用户id") @PathVariable("uId") Integer uId,
            @ApiParam("充值月卡的月数") @PathVariable("effective") Integer effective
    ) {
        try {
            return ApiResult.success(userService.renewMonthlycard(uId, effective));
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG.getCode()
                    , e.getMessage(), null);
        }
    }

    @GetMapping("user/conversionGoods/{uId}/{gId}")
    @ApiOperation(value = "兑换商品", notes = "")
    public ApiResult conversion(
            @ApiParam("地址") @RequestParam("address") String address,
            @ApiParam("用户id") @PathVariable("uId") Integer uId,
            @ApiParam("商品id") @PathVariable("gId") Integer gId
    ) {
        try {
            Integer i = userService.conversionGoods(uId, gId, address);
            return ApiResult.success(i);
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG.getCode(), e.getMessage(), null);
        }
    }

    @PostMapping("user/getAllTrByUId/{uId}")
    @ApiOperation(value = "得到该用户的所有订单", notes = "该用户无订单返回null，无该用户返回失败")
    public ApiResult getAllTrByUId(
            @ApiParam("用户id") @PathVariable("uId") Integer uId
    ) {
        User user = userService.findByUid(uId);
        if (user == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        } else {
            return ApiResult.success(goodService.getAllTrByUId(uId));
        }
    }

    @GetMapping("user/getSystemConfig")
    @ApiOperation(value = "获取停车场系统配置", notes = "包括每小时价格、月卡价格、当日最大价格、当前已用车位、停车场总车位")
    public ApiResult getSysConfig() {
        Map<String, Integer> sysConfig = userService.getSysConfig();
        return ApiResult.success(sysConfig);
    }

    @PutMapping("user/acceptGoods/{trId}")
    @ApiOperation(value = "用户确认收货", notes = "根据订单号确认收货")
    public ApiResult acceptGoods(
            @ApiParam("订单编号") @PathVariable("trId") Integer trId
    ) {
        try {
            int i = goodService.acceptGood(trId);
            if (i == 0) {
                return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
            } else {
                return ApiResult.success(i);
            }
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG.getCode(), e.getMessage(), null);
        }
    }

    @PutMapping("user/update")
    @ApiOperation(value = "更新用户的信息", notes = "余额、积分等内容不可修改 手机号不能与他人重复 若与后端数据不一致会失败")
    public ApiResult upadteUser(
            @ApiParam("用户对象的json") @RequestBody User user
    ) {
        try {
            User user1 = userService.upDateUser(user);
            if (user1 != null) {
                return ApiResult.success(user1);
            } else {
                return ApiResult.failure(ResultCode.USER_BEEN_REG, null);
            }
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG.getCode(), e.getMessage(), null);
        }
    }

    @GetMapping("user/getSelf/{uId}")
    @ApiOperation(value = "查询用户信息", notes = "用户信息的完全json数据")
    public ApiResult getByUId(@ApiParam("uId") @PathVariable Integer uId) {
        User user = userService.findByUid(uId);
        if (user == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
        return ApiResult.success(user);
    }
}

