package idea.verlif.spring.norepeat.judgment.impl;

import idea.verlif.spring.norepeat.entity.RequestFlag;
import idea.verlif.spring.norepeat.judgment.RepeatJudgment;

import java.util.Map;

/**
 * @author Verlif
 * @version 1.0
 * @date 2022/4/11 15:09
 */
public class DefaultRepeatJudgment implements RepeatJudgment {

    @Override
    public boolean isRepeat(RequestFlag oldFlag, RequestFlag newFlag) {
        if (oldFlag.getBody().equals(newFlag.getBody())) {
            // 判断param参数
            Map<String, String[]> oldParaMap = oldFlag.getParamMap();
            Map<String, String[]> newParaMap = newFlag.getParamMap();
            if (oldParaMap.size() != newParaMap.size()) {
                return false;
            }
            for (String key : oldParaMap.keySet()) {
                String[] oldParam = oldParaMap.get(key);
                String[] newParam = newParaMap.get(key);
                if ((oldParam == null && newParam != null)
                        || (oldParam != null && newParam == null)) {
                    return false;
                } else if (oldParam != null) {
                    if (oldParam.length == newParam.length) {
                        for (int i = 0; i < oldParam.length; i++) {
                            if (!oldParam[i].equals(newParam[i])) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
            // 判断header
            Map<String, String> oldHeaderMap = oldFlag.getHeaderMap();
            Map<String, String> newHeaderMap = newFlag.getHeaderMap();
            if (oldHeaderMap.size() != newHeaderMap.size()) {
                return false;
            }
            for (String s : oldHeaderMap.keySet()) {
                String oldHeader = oldHeaderMap.get(s);
                String newHeader = newHeaderMap.get(s);
                if (oldHeader == null && newHeader == null) {
                    continue;
                }
                if (oldHeader != null && !oldHeader.equals(newHeader)
                        || !newHeader.equals(oldHeader)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
