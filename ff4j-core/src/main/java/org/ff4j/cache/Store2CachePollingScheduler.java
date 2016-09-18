package org.ff4j.cache;

import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.ff4j.core.FeatureStore;
import org.ff4j.property.store.PropertyStore;
import org.ff4j.utils.Util;

/**
 * Poll target stores on a fixed delay basis and fill cache to avoid reaching TTL of key.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Store2CachePollingScheduler implements Serializable{
    
    /** Serial. */
    private static final long serialVersionUID = -1198719730422859724L;

    /** polling delay. */
    private long pollingDelay = 10000;
    
    /** initial delay at start. */
    private long initialDelay = 0;
    
    /** Scheduler for the worker. */
    private ScheduledExecutorService executor;
    
    /** Current runnable. */
    private Store2CachePollingWorker worker;
    
    /**
     * Parameterized constructor.
     *
     * @param sf
     *      source feature store
     * @param sp
     *      source property store
     * @param cp
     *      current cache manager
     */
    public Store2CachePollingScheduler(FeatureStore sf, PropertyStore sp, FF4JCacheManager cp) {
        Util.assertNotNull(sf, sp, cp);
        worker = new Store2CachePollingWorker(sf, sp, cp);
        executor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "FF4j_Store2CachePollingWorker");
                t.setDaemon(true);
                return t;
            }
        });
    }
    
    /**
     * Start polling with a polling
     */
    public void start(long delay) {
        this.pollingDelay = delay;
        start();
    }
    
    /**
     * Start polling.
     */
    public void start() {
        executor.scheduleWithFixedDelay(worker, initialDelay, pollingDelay, TimeUnit.MILLISECONDS);
    }
    
    /** Stop Polling. */
    public void stop() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    /**
     * Getter accessor for attribute 'pollingDelay'.
     *
     * @return
     *       current value of 'pollingDelay'
     */
    public long getPollingDelay() {
        return pollingDelay;
    }

    /**
     * Setter accessor for attribute 'pollingDelay'.
     * @param pollingDelay
     * 		new value for 'pollingDelay '
     */
    public void setPollingDelay(long pollingDelay) {
        this.pollingDelay = pollingDelay;
    }

    /**
     * Getter accessor for attribute 'initialDelay'.
     *
     * @return
     *       current value of 'initialDelay'
     */
    public long getInitialDelay() {
        return initialDelay;
    }

    /**
     * Setter accessor for attribute 'initialDelay'.
     * @param initialDelay
     * 		new value for 'initialDelay '
     */
    public void setInitialDelay(long initialDelay) {
        this.initialDelay = initialDelay;
    }
    
    

}
