package es.ibm.usermanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisCacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateRedisCacheManager() {
        assertThat(cacheManager).isInstanceOf(RedisCacheManager.class);
    }

    @Test
    void shouldSerializeLocalDateTime() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String json = objectMapper.writeValueAsString(now);
        LocalDateTime parsed = objectMapper.readValue(json, LocalDateTime.class);

        assertThat(parsed).isEqualTo(now);
    }
}
