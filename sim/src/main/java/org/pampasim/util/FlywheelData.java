package org.pampasim.util;

import lombok.Getter;
import lombok.experimental.Delegate;
import org.pampasim.entity.schedulers.RespectsQuantum;
import org.pampasim.entity.schedulers.Scheduler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class FlywheelData {
    private static class FlywheelSchedulerMap<V> implements Map<String,V> {
        @Delegate
        private Map<String,V> innerMap = new HashMap<>();

        @Override
        public V get(Object o) {
            var containedAlready = this.innerMap.containsKey(o);
            if (containedAlready) { return this.innerMap.get(o); }
            else {
                try {
                    this.putScheduler((String) o);
                    return this.innerMap.get(o);
                } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                         InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public Scheduler putScheduler(String FQCN) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            var classLoader = Thread.currentThread().getContextClassLoader();
            var schedulerClass = classLoader
                    .loadClass(FQCN);
            Scheduler instance = (Scheduler) schedulerClass.getConstructor().newInstance();
            getSchedulerClassByFQCNMap().put(FQCN, (Class<? extends Scheduler>) schedulerClass);
            getSchedulerObjByFQCNMap().put(FQCN, instance);
            return instance;
        }
    }

    @Getter
    protected static Map<String, Class<? extends Scheduler>> schedulerClassByFQCNMap = new FlywheelSchedulerMap<>();
    @Getter
    protected static Map<String, Scheduler> schedulerObjByFQCNMap = new FlywheelSchedulerMap<>();
}
