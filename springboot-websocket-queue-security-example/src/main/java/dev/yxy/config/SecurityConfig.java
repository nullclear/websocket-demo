package dev.yxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * spring-security需要了解，可以看spring-security-demo
 * Created by Nuclear on 2021/1/6
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 设置密码编码的配置参数，这里设置为 NoOpPasswordEncoder，不配置密码加密，方便测试。
     *
     * @return 密码编码实例
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * 设置权限认证参数，这里用于创建两个用于测试的用户信息。
     *
     * @param auth SecurityBuilder 用于创建 AuthenticationManager。
     * @throws Exception 抛出的异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("jack").password("123456").roles("ADMIN")
                .and().withUser("rose").password("123456").roles("ADMIN")
                .and().withUser("tony").password("123456").roles("ADMIN");
    }

    /**
     * 设置 HTTP 安全相关配置参数
     *
     * @param http HTTP Security 对象
     * @throws Exception 抛出的异常信息
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin().loginPage("/login")
                .loginProcessingUrl("/login")//登录表单的action地址 可以自定义
                .defaultSuccessUrl("/").failureUrl("/login?error=true")
                .permitAll()//以上路径全放行
                .and()
                .logout().logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .csrf().disable();//关闭跨域请求 不然登不上
    }
}
