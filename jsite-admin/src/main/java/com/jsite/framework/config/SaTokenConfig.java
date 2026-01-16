package com.jsite.framework.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 *
 * @author jsite
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 登录校验 -- 排除不需要校验的路由
            SaRouter.match("/**")
                    .notMatch(
                            // 静态资源
                            "/",
                            "/favicon.ico",
                            "/static/**",
                            "/webjars/**",
                            // 登录注册
                            "/auth/login",
                            "/auth/logout",
                            "/auth/captcha",
                            "/auth/register",
                            // 接口文档
                            "/doc.html",
                            "/swagger-ui.html",
                            "/swagger-ui/**",
                            "/swagger-resources/**",
                            "/v3/api-docs/**",
                            "/webjars/**",
                            // Druid监控
                            "/druid/**",
                            // 健康检查
                            "/actuator/**"
                    )
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
