package com.durable;

import com.google.gson.Gson;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class DurableContext {
    private final String workflowId;
    private final StepStore store;
    private final AtomicInteger sequence = new AtomicInteger(0);
    private final Gson gson = new Gson();

    public DurableContext(String workflowId, StepStore store) {
        this.workflowId = workflowId;
        this.store = store;
    }

    public <T> T step(String id, Callable<T> fn, Class<T> type) throws Exception {
        int seq = sequence.getAndIncrement();
        String stepKey = id + ":" + seq;

        Optional<String> cached = store.getResult(workflowId, stepKey);
        if (cached.isPresent()) {
            System.out.println("✅ Replaying step: " + id);
            return gson.fromJson(cached.get(), type);
        }

        System.out.println("▶️ Executing step: " + id);
        T result = fn.call();
        store.saveResult(workflowId, stepKey, gson.toJson(result));
        return result;
    }
}