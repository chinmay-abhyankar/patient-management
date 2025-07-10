package com.pm.patientservice.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuration class for Redis caching in the application.
 * This class sets up Redis as the caching provider with customized serialization
 * and cache settings.
 * <p>
 * Key features:
 * - Enables caching functionality via @EnableCaching
 * - Configures JSON serialization for cache values using Jackson
 * - Handles Java 8 date/time types
 * - Includes type information in serialized JSON for proper deserialization
 * - Sets 10 minute TTL for cache entries
 * - Uses string serialization for cache keys
 */
@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        // Creates a new ObjectMapper instance for JSON serialization/deserialization
        ObjectMapper objectMapper = new ObjectMapper();
        // Registers JavaTimeModule to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        // Configures dates to be serialized as ISO-8601 strings instead of timestamps
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Enables type information to be included in serialized JSON
        // This is needed for proper deserialization of complex objects
        // Uses NON_FINAL typing meaning all non-final types will include type info
        //  is included as a property in the JSON
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
// Creates a Redis serializer that uses Jackson for JSON conversion with our configured ObjectMapper
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Creates the Redis cache configuration:
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                // Sets entries to expire after 10 minutes
                .entryTtl(Duration.ofMinutes(60))
                // Prevents caching of null values
                .disableCachingNullValues()
                // Configures how cache keys are serialized - using simple string serialization
                .serializeKeysWith(RedisSerializationContext.
                        SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Configures how cache values are serialized - using our JSON serializer
                .serializeValuesWith(RedisSerializationContext.
                        SerializationPair.fromSerializer(serializer));

        // Creates and returns the Redis cache manager with our configuration
        return RedisCacheManager.builder(factory)
                // Sets the default configuration for all caches
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}