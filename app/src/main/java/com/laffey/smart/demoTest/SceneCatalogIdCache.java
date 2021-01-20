package com.laffey.smart.demoTest;

import java.util.HashMap;
import java.util.Map;

public class SceneCatalogIdCache {
    private static SceneCatalogIdCache instance;
    private Map<String,String> mCacheMap = new HashMap<>();

    public static SceneCatalogIdCache getInstance(){
        if (instance == null){
            synchronized(SceneCatalogIdCache.class){
                instance = new SceneCatalogIdCache();
            }
        }
        return instance;
    }

    public void put(String key, String value){
        mCacheMap.put(key, value);
    }

    public String getValue(String key){
        return mCacheMap.get(key);
    }
}
