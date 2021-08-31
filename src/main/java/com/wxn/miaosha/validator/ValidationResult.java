package com.wxn.miaosha.validator;

import com.alibaba.druid.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ValidationResult {
    private boolean hasErrors = false;

    private Map<String,String> errorMsgMap = new HashMap<>();

    public boolean isHasErrors() {
        return hasErrors;
    }

    public Map<String, String> getErrorMsgMap() {
        return errorMsgMap;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public void setErrorMsgMap(Map<String, String> errorMsgMap) {
        this.errorMsgMap = errorMsgMap;
    }

    public String getErrMsg(){
        return org.apache.commons.lang3.StringUtils.join(errorMsgMap.values().toArray(),',');
    }
}
