package com.cicoding.controller;


import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cicoding.bean.ShiroUser;
import com.cicoding.constant.Constant;
import com.cicoding.log.LogManager;
import com.cicoding.log.LogTaskFactory;
import com.cicoding.utils.LinTools;
import com.cicoding.utils.Userinfo;

@Controller
public class LoginController {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private LinTools linTools;

	@RequestMapping(value = "/login", method = { RequestMethod.POST })
	public String login(HttpServletRequest request, ShiroUser user) {
		// 创建subject并保存在当前线程中
		Subject subject = SecurityUtils.getSubject();

		// 对登陆的密码进行加密处理【一般不这样做，密码传输时应该给密文，否则会在页面调试模式中被获取到账户和密码】
//		user.setPassword(MD5Util.encrypt(user.getPassword()));

		// 判断是否开启登陆验证码校验
		if (linTools.getKaptchaSwich()) {
			String kaptchaRecevied = request.getParameter("kaptcha");
			// 用户输入的验证码的值
			String kaptchaExpected = (String) request.getSession()
					.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
			// 校验验证码是否正确
			if (kaptchaRecevied == null || !kaptchaRecevied.equals(kaptchaExpected)) {
				request.setAttribute("msg", "验证码错误");
				request.setAttribute("status", Constant.ERROR_CODE_VERICATION_CODE_ERROR);
				return "error";// 返回验证码错误
			}

		}

		log.info("user{}",user);
		// 把用户和密码封装成 UsernamePasswordToken 对象
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
		log.info("token{}",token);

		try {
			// 执行登陆
			subject.login(token);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			log.warn("登陆失败");
			request.setAttribute("status", Constant.ERROR_CODE_USERNAME_PASSWORD_MISMATCH);
			request.setAttribute("msg", "账号或密码错误");
			LogManager.getInstance().saveLog(
					LogTaskFactory.getLoginFailTimerTask(user.getUsername(), "账号与密码不匹配", request.getRemoteHost()));
			return "error";
		}

		log.info("用户登陆成功！");
		LogManager.getInstance().saveLog(LogTaskFactory.getLoginSuccessTimerTask(Userinfo.getUser().getId(),
				user.getUsername(), request.getRemoteHost()));
		
		
		return "redirect:/";
	}
}
