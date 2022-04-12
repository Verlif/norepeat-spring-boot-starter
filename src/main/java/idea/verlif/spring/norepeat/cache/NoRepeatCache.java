package idea.verlif.spring.norepeat.cache;

import idea.verlif.spring.norepeat.entity.RequestFlag;

import javax.servlet.http.HttpServletRequest;

/**
 * 去重复
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:30
 */
public interface NoRepeatCache {

    /**
     * 添加请求标识。
     *
     * @param key  请求key
     * @param flag 请求标识
     */
    void add(String key, RequestFlag flag);

    /**
     * 获取请求标识
     *
     * @param key 请求key
     * @return 请求标识
     */
    RequestFlag get(String key);

    /**
     * 通过请求来生成对应缓存的key
     *
     * @param request 请求
     * @return 缓存key
     */
    String genKey(HttpServletRequest request);
}
