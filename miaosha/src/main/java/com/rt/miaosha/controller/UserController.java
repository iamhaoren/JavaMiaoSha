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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public CommonReturnType register(@RequestParam(name="telephone")String telephone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")String gender,
                                     @RequestParam(name="age")String age) throws BusinessException {
        //验证otp
        String inSessionOtpCode=(String)this.httpServletRequest.getSession().getAttribute(telephone);
        if (otpCode.equals(inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }
        return null;
        //用户注册流程
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
