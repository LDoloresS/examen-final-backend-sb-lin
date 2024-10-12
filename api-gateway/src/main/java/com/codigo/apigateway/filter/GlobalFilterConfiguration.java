package com.codigo.apigateway.filter;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@Log4j2
public class GlobalFilterConfiguration {
    @Component
    public class AuthenticationFilter implements GlobalFilter, Ordered {

        @Autowired
        private WebClient.Builder webClientBuilder;
        @Autowired
        private EurekaClient eurekaClient;

        private final AntPathMatcher antPathMatcher = new AntPathMatcher();
        private static final List<String> EXCLUDE_PATHS = List.of("/api/examensbss_lin/authentication/v1/**");

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            String path = exchange.getRequest().getURI().getPath();

            if (isExcludePath(path)) {
                return chain.filter(exchange);
            }

            String token = "Bearer " + exchange.getRequest().getHeaders().getFirst("validate");

            if (token == null || token.isEmpty()) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String serviceUri = getServiceUri("examen-ss", "api/examensbss_lin/authentication/v1/key/validateToken");
            log.info("URI COMPLETA: " + serviceUri);

            return webClientBuilder.build()
                    .post()
                    .uri(serviceUri)
                    .header("validate", token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            return chain.filter(exchange);
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    })
                    .onErrorResume(error -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    });
        }

        @Override
        public int getOrder() {
            return 0;
        }

        private boolean isExcludePath(String path) {
            return EXCLUDE_PATHS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
        }

        private String getServiceUri(String serviceName, String path) {
            InstanceInfo instanceInfo = eurekaClient.getNextServerFromEureka(serviceName, false);
            return instanceInfo.getHomePageUrl() + path;
        }
    }
}
