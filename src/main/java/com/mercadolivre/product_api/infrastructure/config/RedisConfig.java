package com.mercadolivre.product_api.infrastructure.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisConfig {

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Registrar módulo para suporte a Java 8 date/time (LocalDateTime, LocalDate, etc)
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configurar para incluir tipo de classe na serialização
        objectMapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        );
        
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, 
                                         GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        // Configuração padrão com TTL de 2 horas para alta performance
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(2))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer)
                )
                .disableCachingNullValues();

        // Configurações específicas por cache para otimizar performance
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Cache de produto individual - 3 horas (dados que mudam menos)
        cacheConfigurations.put("products", defaultConfig.entryTtl(Duration.ofHours(3)));
        
        // Cache de listagem de produtos - 30 minutos (dados que podem mudar mais)
        cacheConfigurations.put("allProducts", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Cache de produtos por categoria - 1 hora
        cacheConfigurations.put("productsByCategory", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Cache de recomendados - 1 hora
        cacheConfigurations.put("recommendedProducts", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // Cache de imagens - 6 horas (dados estáticos)
        cacheConfigurations.put("productImages", defaultConfig.entryTtl(Duration.ofHours(6)));
        cacheConfigurations.put("allProductImages", defaultConfig.entryTtl(Duration.ofHours(6)));
        
        // Cache de categorias - 12 horas (dados muito estáticos)
        cacheConfigurations.put("categories", defaultConfig.entryTtl(Duration.ofHours(12)));
        cacheConfigurations.put("allCategories", defaultConfig.entryTtl(Duration.ofHours(12)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

}

