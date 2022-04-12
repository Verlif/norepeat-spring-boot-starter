package idea.verlif.spring.norepeat;

import idea.verlif.spring.norepeat.judgment.RepeatJudgment;
import idea.verlif.spring.norepeat.judgment.impl.DefaultRepeatJudgment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeat {

    /**
     * 距上一次的可能重复内容间隔时间。默认2000毫秒。
     *
     * @return 间隔时间，单位毫秒。
     */
    long interval() default 2000;

    /**
     * 当发现重复数据时，是否丢弃。默认丢弃。
     *
     * @return true - 直接丢弃数据，不返回；false - 返回提示。
     */
    boolean isIgnored() default false;

    /**
     * 当 {@link #isIgnored()} 为true时，返回客户端的提示。
     *
     * @return 对重复提交的提示
     */
    String message() default "重复提交，已拒绝";

    /**
     * 使用的数据重复判定类。默认{@link DefaultRepeatJudgment}
     *
     * @return 数据重复判定类。
     */
    Class<? extends RepeatJudgment> judgment() default DefaultRepeatJudgment.class;
}
