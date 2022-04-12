#NoRepeat

__接口防重复提交组件__

通过对接口请求进行旧值对比，过滤掉重复提交的请求，实现接口等幂性。

## 使用

1. 添加`EnableNoRepeat`

    通过`EnableNoRepeat`来开启重复提交过滤服务。

2. 在`API接口`或是`Controller`上使用`@NoRepeat`注解

    ```java
    @NoRepeat(judgment = NoRepeatImpl.class, interval = 10000, message = "禁止重复提交")
    @GetMapping("/norepeat1")
    public BaseResult<String> norepeat1(String test) {
        return new OkResult<>(test);
    }
    ```

    实际上，`@NoRepeat`有四个参数：

   * `interval` - 重复请求间隔时长，单位毫秒。
   * `isIgnored` - 当判定重复时，当此值为`true`时，则忽略请求；否则返回`message()`的值。
   * `message` - 当判定重复且`isIgnored`为`false`时，返回此字符串到客户端。
   * `judgment` - 指定自定义判定类。

    这四个参数都有默认值，也就是说可以直接`@NoRepeat`，不做任何处理。

## 自定义

### NoRepeatCache

`NoRepeatCache`是请求旧值的存储方式，默认是通过`ConcurrentHashMap`进行存值，然后进行懒删除。  
开发者可以通过实现`NoRepeatCache`来管理请求旧值，并可以在`genKey`方法中区分用户（默认仅判定url），例如：

```java
@Component
public class NoRepeatCacheImpl implements NoRepeatCache {

    private final Map<String, RequestFlag> flagMap;

    public NoRepeatCacheImpl() {
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

   /**
    * 通过请求来生成对应缓存的key
    *
    * @param request 请求
    * @return 缓存key
    */
   @Override
   public String genKey(HttpServletRequest request) {
      return request.getRequestURI() + request.getHeader("user");
   }
}
```

请注意，自定义的实现类请添加`@Component`注解，以便组件识别。

### RepeatJudgment

`RepeatJudgment`是用于做请求重复判定的类，其中只有一个方法。

```java
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
```

与`NoRepeatCache`类似，自定义的`RepeatJudgment`需要加上`@Component`注解。开发者可以制定多个`RepeatJudgment`以便不同接口使用不同策略。

__注意：请尽量使用自定义的请求缓存，默认的缓存使用的是HashMap作为的缓存。__

## 引入依赖

1. 添加Jitpack仓库源

> maven
> ```xml
> <repositories>
>    <repository>
>        <id>jitpack.io</id>
>        <url>https://jitpack.io</url>
>    </repository>
> </repositories>
> ```

2. 添加依赖

> maven
> ```xml
>    <dependencies>
>        <dependency>
>            <groupId>com.github.Verlif</groupId>
>            <artifactId>norepeat-spring-boot-starter</artifactId>
>            <version>2.6.6-1.1</version>
>        </dependency>
>    </dependencies>
> ```
