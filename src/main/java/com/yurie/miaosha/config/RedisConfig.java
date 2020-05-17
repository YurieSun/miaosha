package com.yurie.miaosha.config;

import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;

@Component
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600) //将session过期时间设为3600s
public class RedisConfig {
}
