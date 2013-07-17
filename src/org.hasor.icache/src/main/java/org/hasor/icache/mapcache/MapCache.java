/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hasor.icache.mapcache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.hasor.context.AppContext;
import org.hasor.icache.Cache;
import org.hasor.icache.CacheDefine;
import org.hasor.icache.DefaultCache;
/**
 * ʹ��Map��Ϊ���棬MapCache�������Ϊ�����ṩ��һ��Ĭ��ʵ�֡�
 * @version : 2013-4-20
 * @author ������ (zyc@byshell.org)
 */
@DefaultCache
@CacheDefine(value = "MapCache", displayName = "InternalMapCache", description = "���õ�Map���棬ICache�ӿڵļ�ʵ�֡�")
public class MapCache<T> extends Thread implements Cache<T> {
    private MapCacheSettings                         settings       = null;
    private String                                   threadName     = "InternalMapCache-Daemon";
    private volatile boolean                         exitThread     = false;
    private volatile HashMap<String, CacheEntity<T>> cacheEntityMap = new HashMap<String, CacheEntity<T>>();
    //
    protected String getThreadName() {
        return this.threadName;
    }
    @Override
    public void run() {
        this.setName(this.threadName);
        while (!this.exitThread) {
            List<String> lostList = new ArrayList<String>();
            for (Entry<String, CacheEntity<T>> ent : this.cacheEntityMap.entrySet()) {
                CacheEntity<T> cacheEnt = ent.getValue();
                if (cacheEnt == null)
                    continue;
                if (cacheEnt.isLost())
                    lostList.add(ent.getKey());
            }
            //
            for (String lostKey : lostList)
                this.cacheEntityMap.remove(lostKey);
            //
            try {
                sleep(settings.getThreadSeep());
            } catch (InterruptedException e) {}
        }
    }
    @Override
    public synchronized void initCache(AppContext appContext) {
        this.settings = appContext.getInstance(MapCacheSettings.class);
        this.exitThread = false;
        this.setDaemon(true);
        this.start();
    }
    @Override
    public synchronized void destroy(AppContext appContext) {
        this.exitThread = true;
        this.clear();
    }
    @Override
    public boolean toCache(String key, T value) {
        return this.toCache(key, value, this.settings.getDefaultTimeout());
    }
    @Override
    public boolean toCache(String key, T value, long timeout) {
        synchronized (key) {
            if (key == null)
                return false;
            //
            if (this.settings.isEternal() == true) {
                timeout = Long.MAX_VALUE;
            } else if (timeout <= 0)
                timeout = this.settings.getDefaultTimeout();
            //
            CacheEntity<T> oldEnt = this.cacheEntityMap.put(key, new CacheEntity<T>(value, timeout));
            return true;
        }
    }
    @Override
    public T fromCache(String key) {
        synchronized (key) {
            CacheEntity<T> cacheEntity = this.cacheEntityMap.get(key);
            if (cacheEntity != null) {
                if (this.settings.isAutoRenewal() == true)
                    cacheEntity.refresh();
                return cacheEntity.get();
            }
            return null;
        }
    }
    @Override
    public boolean hasCache(String key) {
        synchronized (key) {
            return this.cacheEntityMap.containsKey(key);
        }
    }
    @Override
    public boolean remove(String key) {
        synchronized (key) {
            CacheEntity<T> cacheEntity = this.cacheEntityMap.remove(key);
            return cacheEntity != null;
        }
    }
    @Override
    public boolean refreshCache(String key) {
        synchronized (key) {
            CacheEntity<T> cacheEntity = this.cacheEntityMap.get(key);
            if (cacheEntity != null)
                cacheEntity.refresh();
            return cacheEntity != null;
        }
    }
    @Override
    public synchronized boolean clear() {
        this.cacheEntityMap.clear();
        return true;
    }
    /*-------------------------------------------------------------------------------------*/
    private static class CacheEntity<T> {
        private T    value    = null;
        private long timeout  = 0;
        private long lastTime = 0;
        //
        public CacheEntity(T value, long timeout) {
            this.value = value;
            this.timeout = timeout;
            this.lastTime = System.currentTimeMillis();
        }
        //
        public boolean isLost() {
            if (this.timeout == Long.MAX_VALUE)
                return false;
            return (lastTime + this.timeout) < System.currentTimeMillis();
        }
        //
        public void refresh() {
            this.lastTime = System.currentTimeMillis();
        }
        //
        public T get() {
            return this.value;
        }
    }
}