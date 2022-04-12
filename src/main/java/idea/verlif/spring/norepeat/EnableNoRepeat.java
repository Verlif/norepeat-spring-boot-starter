package idea.verlif.spring.norepeat;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:23
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration
@Documented
@Import({NoRepeatConfig.class, NoRepeatHandler.class})
public @interface EnableNoRepeat {
}
