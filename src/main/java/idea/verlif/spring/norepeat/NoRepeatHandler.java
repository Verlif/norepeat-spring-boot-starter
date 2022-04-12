package idea.verlif.spring.norepeat;

import idea.verlif.spring.norepeat.cache.NoRepeatCache;
import idea.verlif.spring.norepeat.entity.RequestFlag;
import idea.verlif.spring.norepeat.judgment.RepeatJudgment;
import idea.verlif.spring.norepeat.judgment.impl.DefaultRepeatJudgment;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 14:24
 */
@Aspect
@Component
public class NoRepeatHandler {

    private final NoRepeatCache noRepeatCache;

    private final RepeatJudgment defaultJudgment;
    private final Map<Class<?>, RepeatJudgment> judgmentMap;

    public NoRepeatHandler(@Autowired NoRepeatCache cache, @Autowired ApplicationContext appContext) {
        this.noRepeatCache = cache;
        this.defaultJudgment = new DefaultRepeatJudgment();
        this.judgmentMap = new HashMap<>();
        Map<String, RepeatJudgment> judgmentMap = appContext.getBeansOfType(RepeatJudgment.class);
        for (RepeatJudgment value : judgmentMap.values()) {
            this.judgmentMap.put(value.getClass(), value);
        }
    }

    @Around("@within(idea.verlif.spring.norepeat.NoRepeat) || @annotation(idea.verlif.spring.norepeat.NoRepeat)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature sig = joinPoint.getSignature();
        Method method = ((MethodSignature) sig).getMethod();

        // 检测方法上的注解
        NoRepeat noRepeat = method.getAnnotation(NoRepeat.class);
        if (noRepeat == null) {
            noRepeat = method.getDeclaringClass().getAnnotation(NoRepeat.class);
        }
        RepeatJudgment repeatJudgment = judgmentMap.get(noRepeat.judgment());
        if (repeatJudgment == null) {
            repeatJudgment = defaultJudgment;
        }
        // 进行重复请求判定
        HttpServletRequest request = getRequest();
        if (request != null) {
            String key = noRepeatCache.genKey(request);
            synchronized (noRepeatCache) {
                RequestFlag flag = noRepeatCache.get(key);
                RequestFlag nowFlag = new RequestFlag(request, noRepeat.interval());
                if (flag != null) {
                    if (repeatJudgment.isRepeat(flag, nowFlag)) {
                        if (!noRepeat.isIgnored()) {
                            sendTip(noRepeat.message());
                        }
                        return null;
                    }
                }
                noRepeatCache.add(key, nowFlag);
            }
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    private void sendTip(String tip) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletResponse response = attributes.getResponse();
            if (response == null) {
                return;
            }
            response.setHeader("Content-type", "application/json;charset=" + StandardCharsets.UTF_8);
            response.setCharacterEncoding("utf-8");
            try {
                Writer writer = response.getWriter();
                writer.write(tip);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
