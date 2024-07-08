package com.example.inu.global.jwt;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

//jwt처리를 위한 filter custom
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER ="Authorization";
    private TokenProvider tokenProvider;//TokenProvider 주입
    public JwtFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override//실제 filtering 로직 ->jwt 토큰 인증정보를 SecurityContext에 저장하는 역할
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException{
        HttpServletRequest httpServletRequest =(HttpServletRequest) servletRequest;
        String jwt= resolveToken(httpServletRequest);//토큰정보 받아오고고
        String requestURI = httpServletRequest.getRequestURI();

        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){//받아온 token유효성 검사
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri :{}",authentication.getName(),requestURI);
        }//token이 정상이면 SecurityContext에 저장함.
        else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    //Request header에서 token정보를 꺼내오기 위한 resolveToken
    private String resolveToken(HttpServletRequest request){
        String bearerToken= request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return null;
    }
    //HTTP 요청의 헤더에서 JWT (JSON Web Token)를 추출하는 역할을 수행합니다.
    // 주어진 함수 resolveToken은 HttpServletRequest 객체에서 'Authorization' 헤더를 조회하여, 그 값이 "Bearer"로 시작하는 경우, 토큰 부분만을 반환합니다

}