package com.cicoding.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.cicoding.annotation.BizLog;
import com.cicoding.bean.User;
import com.cicoding.log.LogManager;
import com.cicoding.log.LogTaskFactory;
import com.cicoding.utils.Userinfo;

/**
 * 业务切面
 */
@Aspect
@Component
public class LogAop {


    @Pointcut("@annotation(com.cicoding.annotation.BizLog)")
    public void logCut() {

    }

    @Around("logCut()")
    public Object test(ProceedingJoinPoint point) throws Throwable {

        // 执行方法
        Object result = point.proceed();

        try {
            handle(point);
        } catch (Exception e) {
            System.out.println("日志记录出错");
        }

        return result;
    }


    private void handle(ProceedingJoinPoint point) throws NoSuchMethodException, SecurityException {
        //如果用户未登录则不记录日志
        User user = Userinfo.getUser();
        if (user == null) {
            return;
        }

        // 获取方法名的：void com.cicoding.controller.UserController.addUser(HttpServletRequest)
        Signature sign = point.getSignature();
        MethodSignature msign = null;
        if (!(sign instanceof MethodSignature)) {
            throw new IllegalArgumentException("只可用于方法");
        }
        // 获取方法名void com.cicoding.controller.UserController.addUser(HttpServletRequest)
        msign = (MethodSignature) sign;
        Object target = point.getTarget();
        Method method = target.getClass().getMethod(msign.getName(), msign.getParameterTypes());
        String methodName = method.getName();
        // com.cicoding.controller.UserController
        String className = target.getClass().getName();
        // 获取注解：@com.cicoding.annotation.BizLog(value=添加用户)
        BizLog bizLog = method.getAnnotation(BizLog.class);
        // logName="添加用户"
        String logName = bizLog.value();

        LogManager.getInstance()
                .saveLog(LogTaskFactory.getOperationSuccessTimerTask(user.getId(), className, logName, methodName, Userinfo.getUsername()));

    }

}
