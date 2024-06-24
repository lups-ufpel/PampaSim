package org.pampasim.gui.model;

import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.PidAllocator;
import org.pampasim.Os.Process;
import org.pampasim.gui.listeners.EventListener;
import org.pampasim.gui.listeners.ProcessEventInfo;

import java.util.HashSet;
import java.util.Set;

public class ProcessMock extends Process {
    private final Set<EventListener<ProcessEventInfo>> onStartListeners;
    private final Set<EventListener<ProcessEventInfo>> onFinishListeners;
    private final Set<EventListener<ProcessEventInfo>> onUpdateListeners;
    private final Set<EventListener<ProcessEventInfo>> onCreateListeners;

    public ProcessMock(PidAllocator.Pid pid, int priority, int totalBurst, int arrivalTime) {
        super(pid, priority, totalBurst, arrivalTime);
        onStartListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateListeners = new HashSet<>();
        onCreateListeners = new HashSet<>();
    }
    public ProcessMock addOnCreateListener(EventListener<ProcessEventInfo> listener) {
        this.onCreateListeners.add(listener);
        return this;
    }

    public ProcessMock addOnStartListener(EventListener<ProcessEventInfo> listener) {
        this.onStartListeners.add(listener);
        return this;
    }
    public ProcessMock addOnFinishListener(EventListener<ProcessEventInfo> listener) {
        this.onFinishListeners.add(listener);
        return this;
    }
    public ProcessMock addOnUpdateListener(EventListener<ProcessEventInfo> listener) {
        this.onUpdateListeners.add(listener);
        return this;
    }
    public void notifyOnUpdate() {
        onUpdateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyOnCreate() {
        onCreateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    @Override
    public void submit() {
        if(getState() != State.NEW){
            throw new IllegalStateException("Process must be in NEW state to be submitted");
        }
        else{
            setState(State.READY);
            onStart();
        }
    }
    @Override
    public void dispatch() {
        if(getState() != State.READY){
            throw new IllegalStateException("Process must be in READY state to be dispatched");
        }
        else{
            setState(State.RUNNING);
            notifyOnUpdate();
        }
    }
    @Override
    public void finish() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("Process must be in RUNNING state to be finished");
        }
        else{
            setState(State.TERMINATED);
            onFinish();
        }
    }
    public void onStart() {
        onStartListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener,this)));
    }
    public void onFinish() {
        onFinishListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
        onFinishListeners.clear();
    }
}
