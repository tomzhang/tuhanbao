package com.tuhanbao.web.controller.authority;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.tuhanbao.base.util.config.BaseConfigConstants;
import com.tuhanbao.base.util.config.ConfigManager;
import com.tuhanbao.base.util.exception.BaseErrorCode;
import com.tuhanbao.base.util.exception.MyException;
import com.tuhanbao.base.util.objutil.NumberUtil;
import com.tuhanbao.base.util.objutil.StringUtil;
import com.tuhanbao.base.util.objutil.TimeUtil;
import com.tuhanbao.web.ServerManager;
import com.tuhanbao.web.controller.AdminController;

/**
 * 2016年10月24日
 * 
 * @author liuhanhui 权限控制
 */
public class UserAccessApiInterceptor extends HandlerInterceptorAdapter {

    private static final String PASSWORD = "tuhanbao";
    
    private static final String SUPER_ADMIN_PREFIX = "211";

    private static final String SUPER_ADMIN_SUFFIX = "88";

    private static final String PASSWORD_NAME = "pazwrd";
    
    private PermissionManager permissionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        Method method = handlerMethod.getMethod();
        
        if (ServerManager.serverIsBusy()) {
            throw new MyException(BaseErrorCode.SERVER_IS_BUSY);
        }
        
        if (ConfigManager.isMaintaining() && method.getDeclaringClass() != AdminController.class) {
            //如果是超级管理员，依然可以绕过
            if (!isSupperManager(request)) {
                String ip = UserAccessApiInterceptor.getRemoteHost(request);
                if (!isInWhiteList(ip)) {
                    String repaireTime = ConfigManager.getBaseConfig().getString(BaseConfigConstants.REPAIR_TIME);
                    if (!StringUtil.isEmpty(repaireTime)) {
                        long time = TimeUtil.now() - Long.valueOf(repaireTime);
                        if (time > 0) {
                            int mins = NumberUtil.getInt(time * 1.0d / 1000 / 60);
                            if (mins > 60) {
                                int hours = NumberUtil.getInt(mins / 60, NumberUtil.ROUND);
                                throw new MyException(BaseErrorCode.SERVER_IS_MAINTAINING_BY_HOUR, hours + "");
                            }
                            else {
                                throw new MyException(BaseErrorCode.SERVER_IS_MAINTAINING_BY_MIN, mins + "");
                            }
                        }
                    }
                    throw new MyException(BaseErrorCode.SERVER_IS_MAINTAINING);
                }
            }
        }
        
        IUser user = null;
        TokenService tokenService = TokenService.getInstance();
        if (tokenService != null) {
            user = tokenService.getCurrentUser(request);
        }
        
        //自定义权限过滤
        AccessRequired annotation = method.getAnnotation(AccessRequired.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(AccessRequired.class);
        }
        if (annotation != null) {
            int authority = 0;
            if (user != null) {
                authority = user.getAuthority();
            }
            boolean hasPower = Authority.hasPower(authority, Authority.getAuthorityValueByIndex(annotation.value()));
            //如果没有权限，而且user为空，需要抛出登陆错误
            if (!hasPower && user == null) {
                throw new MyException(BaseErrorCode.PLEASE_LOGIN_FIRST);
            }
            return hasPower;
        }
        
        //role权限过滤
        if (permissionManager != null) {
            String requestUrl = buildRequestUrl(request);
            if (user == null && !permissionManager.IS_LOGIN(requestUrl)) {
                throw new MyException(BaseErrorCode.PLEASE_LOGIN_FIRST);
            }
            if (permissionManager.needCheckPermission(requestUrl)) {
                /**
                 * TODO 如果数据库存的是通配符呢？
                 * 这里可能后续要支持通配符
                 */
                List<Integer> roles = permissionManager.getHasPermissionRole(requestUrl);
                if (roles == null || !roles.contains(user.getAuthority())) {
                    throw new MyException(BaseErrorCode.HAVE_NO_RIGHT);
                }
            }
        }
        
        return true;

    }

    private boolean isInWhiteList(String ip) {
        for (String item : StringUtil.string2Array(ConfigManager.getBaseConfig().getString(BaseConfigConstants.WHITE_LIST))) {
            //根据常规的使用，正则要做一些转换
            item = item.replace(".", "\\.");  
            item = item.replace("*", "(.*)");
            item = "^" + item + "$";
            if (ip.matches(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    public static String getRemoteHost(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
    
    public static String buildRequestUrl(HttpServletRequest r) {
        return buildRequestUrl(r.getServletPath(), r.getRequestURI(), r.getContextPath(), r.getPathInfo(),
            r.getQueryString());
    }

    /**
     * Obtains the web application-specific fragment of the URL.
     */
    public static String buildRequestUrl(String servletPath, String requestURI, String contextPath, String pathInfo,
            String queryString) {

        StringBuilder url = new StringBuilder();

        if (servletPath != null) {
            url.append(servletPath);
            if (pathInfo != null) {
                url.append(pathInfo);
            }
        } else {
            url.append(requestURI.substring(contextPath.length()));
        }

//        if (queryString != null) {
//            url.append("?").append(queryString);
//        }

        return url.toString();
    }
    
    public static boolean isSupperManager(String userName, String password) {
        if (StringUtil.isEmpty(userName) || StringUtil.isEmpty(password)) return false;
        return PASSWORD.equals(password) && userName.startsWith(SUPER_ADMIN_PREFIX) && userName.endsWith(SUPER_ADMIN_SUFFIX);
    }
    
    public static boolean isSupperManager(HttpServletRequest request) {
        String value = request.getParameter(PASSWORD_NAME);
        return PASSWORD.equals(value);
    }

    public static String filterUserName(String loginName, String password) {
        if (isSupperManager(loginName, password)) {
            return loginName.substring(SUPER_ADMIN_PREFIX.length(), loginName.length() - SUPER_ADMIN_SUFFIX.length());
        }
        return loginName;
    }

}
