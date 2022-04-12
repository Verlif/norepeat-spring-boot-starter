package idea.verlif.spring.norepeat;

import idea.verlif.spring.norepeat.cache.NoRepeatCache;
import idea.verlif.spring.norepeat.cache.impl.DefaultNoRepeatCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:40
 */
@Configuration
public class NoRepeatConfig {

    @Bean
    @ConditionalOnMissingBean(NoRepeatCache.class)
    public NoRepeatCache noRepeatCache() {
        return new DefaultNoRepeatCache();
    }
}
