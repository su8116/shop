package org.zerock.shop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.shop.service.MemberService;

@Configuration
@EnableWebSecurity //SpringSecurityFilterChain 자동 포함
public class SecurityConfig extends WebSecurityConfigurerAdapter { //보안설정 커스터마이징

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //http 보안 설정 (페이지 권한 설정, 로그인 페이지 설정 등을 추가)
        http.formLogin().loginPage("/members/login")//로그인페이지
                .defaultSuccessUrl("/")//로그인 성공 시 이동할 페이지
                .usernameParameter("email")//로그인 시 사용할 파라미터 이름
                .failureForwardUrl("/members/login/error")//로그인 실패 시 이동할 페이지
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃
                .logoutSuccessUrl("/");//로그아웃 성공 시 이동할 페이지

        http.authorizeRequests() //시큐리티 처리에 HttpServletRequest 이용
                .mvcMatchers("/", "/members/**", "/item/**", "/image/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated(); //mvcMatchers로 설정하지 않은 경로 외에는 인증 요구

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        //인증되지 않은 사용자가 접근한 경우 수행되는 핸들러 등록
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/image/**");
        //static 디렉터리의 하위파일은 인증 무시하도록 설정
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception { //인메모리?
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //비밀번호 암호화
        return new BCryptPasswordEncoder();
    }
}
