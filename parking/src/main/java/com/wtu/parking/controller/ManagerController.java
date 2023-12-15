package com.wtu.parking.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.wtu.parking.entity.Goods;
import com.wtu.parking.entity.GoodsTrade;
import com.wtu.parking.entity.Manager;
import com.wtu.parking.entity.User;
import com.wtu.parking.service.impl.GoodService;
import com.wtu.parking.service.impl.MangerService;
import com.wtu.parking.service.impl.UserService;
import com.wtu.parking.utils.ApiResult;
import com.wtu.parking.utils.JwtUtil;
import com.wtu.parking.utils.Page;
import com.wtu.parking.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

@RestController
@CrossOrigin
@Api(tags = "管理员API")
@Slf4j
public class ManagerController {

    @Autowired
    MangerService mangerService;
    @Autowired
    UserService userService;

    @Autowired
    GoodService goodService;


    @PostMapping("/login/manager")
    @ApiOperation(value = "管理员使用 id或身份证 及 md5密文登录", notes = "请完成对密码的md5加密以及身份证号的正则校验," +
            "返回空值代表用户不存在，否则返回用户信息")
    @ResponseBody
    public ApiResult login(
            @ApiParam(name = "管理员id", required = false) @RequestParam("mId") String strMId,
            @ApiParam(name = "管理员身份证号", required = false, defaultValue = "null") @RequestParam(value = "identityId", required = false) String idId,
            @ApiParam(name = "管理员密码MD5密文", required = true) @RequestParam("pwd") String pwd

    ) {
        Manager manager = null;
        log.info("id={}", strMId);
        if (!strMId.equals("")) {
            Integer mId = Integer.valueOf(strMId);
            manager = mangerService.findByMId(mId);
            if (manager != null && !manager.getPwd().equals(pwd)) {
                return ApiResult.failure(ResultCode.PWD_ERR, null);
            }
        } else if (idId != null) {
            manager = mangerService.findByIdentityId(idId);
            if (manager != null && !manager.getPwd().equals(pwd)) {
                return ApiResult.failure(ResultCode.PWD_ERR, null);
            }
        }
        if (manager == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
        log.info(manager.toString());
        JwtUtil jwtUtil = new JwtUtil();
        Map<String, String> stringMap = new TreeMap<>();
        stringMap.put("pwd", manager.getPwd());
        stringMap.put("id", String.valueOf(manager.getMId()));
        String token = jwtUtil.createToken(stringMap);
        log.info("token=" + token);
        return ApiResult.success(token);
    }

    @PostMapping("register/manager")
    @ApiOperation(value = "管理员用户注册", notes = "id无需提交 为后端自动生成 注册成功返回id 失败返回-1")
    public ApiResult register(@ApiParam(name = "管理员类的json数据", required = true) @RequestBody Manager manager) {
        log.info(manager.toString());
        int id = mangerService.insert(manager);
        if (id != -1) {
            return ApiResult.success(id);
        } else {
            return ApiResult.failure(ResultCode.USER_BEEN_REG, -1);
        }
    }

    @GetMapping("register/userByteleNum/{teleNum}")
    @ApiOperation(value = "管理员根据用户手机号获取用户信息", notes = "失败返回null，成功返回user对象")
    public ApiResult getUserByTeleNum(@ApiParam(name = "用户的电话号字符串") @PathVariable("teleNum") String teleNum) {
        User user = userService.findByTeleNum(teleNum);
        if (user == null) {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
        return ApiResult.success(user);
    }

    @PostMapping("manager/MonthlyUser/{uId}/{effective}")
    @ApiOperation(value = "开通月卡", notes = "若所给的用户不存在或已经有月卡返回null 成功返回扣费金额")
    public ApiResult addMonthlyUser(
            @ApiParam(name = "用户id") @PathVariable("uId") Integer uId,
            @ApiParam(name = "有效期") @PathVariable("effective") Integer effective
    ) {
        if (effective < 1) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG.getCode(),
                    "不能小于30天", null);
        }
        try {
            int i = mangerService.insertMonthlyUser(uId, effective);
            if (i != 0) {
                return ApiResult.success(i);
            }
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.USER_BEEN_REG.getCode(), e.getMessage(), null);
        }
        return null;
    }

    @PostMapping("manager/getCarNum")
    @ApiOperation(value = "车牌识别", notes = "成功返回车牌号，失败返回null")
    public ApiResult getCarNum(MultipartFile img) {


        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject imgJson = new JSONObject();
        log.info(img.toString());
        try {
            byte[] imgBytes = img.getBytes();
            final Base64 base64 = new Base64();
            String encode = base64.encodeToString(imgBytes);
            imgJson.put("image", encode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        imgJson.put("url", null);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, imgJson.toJSONString());
        Request request = new Request.Builder()
                .url("https://eolink.o.apispace.com/lisence-recognition/api/v1/detect")
                .method("POST", body)
                .addHeader("X-APISpace-Token", "smyn59ya2sc2fedfutzy3td5w1gzaoiw")
                .addHeader("Authorization-Type", "apikey")
                .addHeader("Content-Type", "form-data")
                .build();
        String carNum = null;
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String resJsonStr = response.body().string();
            log.info(resJsonStr);
            JSONObject jsonObject = JSON.parseObject(resJsonStr);
            carNum = jsonObject.getJSONObject("data").getJSONArray("result").getJSONObject(0).getString("number");
            log.info("识别的车牌为：{}", carNum);
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
        }
        return ApiResult.success(carNum);
    }

    @PostMapping("manager/addGoods")
    @ApiOperation(value = "添加商品", notes = "失败返回空")
    public ApiResult addGoods(@ApiParam("商品的json数据") @RequestBody Goods goods) {
        int i = goodService.insert(goods);
        if (i != 0) {
            return ApiResult.success("添加成功");
        } else {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
        }
    }

    @PostMapping("manager/getUserPage/{pageSize}/{pageNum}")
    @ApiOperation(value = "分页查询用户", notes = "返回Page对象")
    public ApiResult getUserPage(
            @ApiParam("分页大小") @PathVariable("pageSize") int pageSize,
            @ApiParam("分页号") @PathVariable("pageNum") int pageNum
    ) {
        Page<User> userPage = userService.selectByPage(pageSize, pageNum);
        if (userPage == null) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, null);
        } else {
            return ApiResult.success(userPage);
        }
    }

    @PutMapping("manager/updateTrade")
    @ApiOperation(value = "给商品发货", notes = "更新商品的状态和物流运单号信息")
    public ApiResult updateTrade(
            @ApiParam("要修改的订单记录") @RequestBody GoodsTrade goodsTrade
    ) {
        int i = goodService.updateTrade(goodsTrade);
        if (i == 0) {
            return ApiResult.failure(ResultCode.NO_SUCH_REC, null);
        } else {
            return ApiResult.success(i);
        }
    }

    @GetMapping("userByIdOrTele")
    @ApiOperation(value = "根据uId或电话号获取用户")
    public ApiResult getUSerBy(
            @ApiParam("uId") @RequestParam(value = "uId", required = false) Integer uId,
            @ApiParam("teleNum") @RequestParam(value = "teleNum", required = false) String teleNum
    ) {
        User user = null;
        if (uId != null) {
            user = userService.findByUid(uId);
        }
        if (user == null && teleNum != null && !teleNum.equals("")) {
            user = userService.findByTeleNum(teleNum);
        }
        if (user != null) {
            return ApiResult.success(user);
        } else {
            return ApiResult.failure(ResultCode.USER_NOT_EXIST, null);
        }
    }

    @PostMapping("manager/goodsTrRecordsPage/{pageSize}/{pageNum}")
    @ApiOperation("分页获取所有交易记录")
    public ApiResult getGTrRecPage(
            @ApiParam("分页大小") @PathVariable("pageSize") int pageSize,
            @ApiParam("分页号") @PathVariable("pageNum") int pageNum
    ) {
        try {
            Page<GoodsTrade> page = goodService.GoodTrRecordByPage(pageSize, pageNum);
            return ApiResult.success(page);
        } catch (Exception e) {
            return ApiResult.failure(ResultCode.PARAM_FORMAT_WRONG, e.getMessage());
        }
    }

    @PostMapping("manager/getSelfById/{mId}")
    @ApiOperation("根据管理员id获取自身")
    public  ApiResult getSelf(
            @ApiParam("mid") @PathVariable("mId") Integer mId
    ){
        Manager byMId = mangerService.findByMId(mId);
        System.out.println(byMId);
        if(mId!=null){
            byMId.setPwd("");
            return ApiResult.success(byMId);
        }
        else{
            return ApiResult.failure(ResultCode.USER_NOT_EXIST,null);
        }
    }



}
