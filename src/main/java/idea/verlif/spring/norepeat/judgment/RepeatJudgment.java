package idea.verlif.spring.norepeat.judgment;

import idea.verlif.spring.norepeat.entity.RequestFlag;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 15:07
 */
public interface RepeatJudgment {

    /**
     * 判断两个请求标识是否相同
     *
     * @param oldFlag 旧的请求标识
     * @param newFlag 新的请求标识
     * @return true - 两个请求属于重复请求；false - 两个请求不属于同个请求
     */
    boolean isRepeat(RequestFlag oldFlag, RequestFlag newFlag);
}
