package com.rt.miaosha.controller;

import com.rt.miaosha.controller.viewobject.UserVO;
import com.rt.miaosha.dao.UserDOMapper;
import com.rt.miaosha.dataobject.UserDO;
import com.rt.miaosha.error.BusinessException;
import com.rt.miaosha.error.EmBusinessError;
import com.rt.miaosha.response.CommonReturnType;
import com.rt.miaosha.service.UserService;
import com.rt.miaosha.service.impl.UserServiceImpl;
import com.rt.miaosha.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Controller
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户注册接口
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes ={CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telephone")String telephone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")Byte gender,
                                     @RequestParam(name="password")String password,
                                     @RequestParam(name="age")Integer age) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证otp
        String inSessionOtpCode=(String)this.httpServletRequest.getSession().getAttribute(telephone);
        if (!otpCode.equals(inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }

        //用户注册流程
        UserModel userModel=new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setTelephone(telephone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    //用户登录接口
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telephone")String telephone,
                                  @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //参数校验
        if (StringUtils.isEmpty(telephone)||
            StringUtils.isEmpty(password)){
            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //判断登录是否合法
        UserModel userModel= userService.validateLogin(telephone,EncodeByMd5(password));

        //将登录凭证加入到用户登录成功的session
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
        return CommonReturnType.create(null);
    }


    public String EncodeByMd5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5=MessageDigest.getInstance("MD5");
        //加密字符串
        Base64.Encoder encoder=Base64.getEncoder();
        String newstr=encoder.encodeToString(md5.digest(str.getBytes("utf-8")));
        return newstr;

    }

    //用户获取otp短信接口
    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name="telephone")String telephone){
        //生成OTP验证码
        Random random=new Random();
        int randomInt=random.nextInt(99999);
        randomInt+=10000;
        String optCode=String.valueOf(randomInt);

        //将OTP验证码与手机号关联
        httpServletRequest.getSession().setAttribute(telephone,optCode);

        //将OTP验证码发送给用户
        System.out.println(telephone+" "+optCode);
        return CommonReturnType.create(null);
    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType findUser(@RequestParam(name="id") Integer id) throws BusinessException {
        UserModel userModel=userService.getUserById(id);
        if (userModel==null)
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        UserVO userVO=convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }
    private UserVO convertFromModel(UserModel userModel){
        if (userModel==null)
            return null;
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(userModel,userVO);
        return userVO;
    }
}
