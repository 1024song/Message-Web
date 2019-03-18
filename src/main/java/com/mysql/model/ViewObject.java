package com.mysql.model;

import java.util.*;

/**
 * Created by rainday on 16/6/30.
 */
public class ViewObject {//试图展示对象。
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
