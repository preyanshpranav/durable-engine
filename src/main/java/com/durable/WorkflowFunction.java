package com.durable;

@FunctionalInterface
public interface WorkflowFunction {
    void execute(DurableContext ctx) throws Exception;
}