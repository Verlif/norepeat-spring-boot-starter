package idea.verlif.spring.norepeat.cache.impl;

import idea.verlif.spring.norepeat.cache.NoRepeatCache;
import idea.verlif.spring.norepeat.entity.RequestFlag;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:36
 */
public final class DefaultNoRepeatCache implements NoRepeatCache {

    private final Map<String, RequestFlag> flagMap;

    public DefaultNoRepeatCache() {
        flagMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(String key, RequestFlag flag) {
        flagMap.put(key, flag);
    }

    @Override
    public synchronized RequestFlag get(String key) {
        RequestFlag flag = flagMap.get(key);
        if (flag != null && (flag.getTime() + flag.getInterval()) < System.currentTimeMillis()) {
            flagMap.remove(key);
            return null;
        }
        return flag;
    }

    @Override
    public String genKey(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
