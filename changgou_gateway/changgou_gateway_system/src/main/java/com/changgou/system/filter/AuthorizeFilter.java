package com.changgou.system.filter;

import com.changgou.system.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //获取相应对象
        ServerHttpResponse response = exchange.getResponse();
        //放行登陆请求
        if (request.getURI().getPath().contains("/admin/login")){
            return chain.filter(exchange);
        }
        //获取请求头
        HttpHeaders headers = request.getHeaders();
        //获取jwtToken
        String jwtToken = headers.getFirst("token");
        //判断jwtToken是否为空，为空则无权访问
        if (StringUtils.isEmpty(jwtToken)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //解析jwtToken，正常则放行，解析失败无权访问
        try {
            JwtUtil.parseJWT(jwtToken);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
