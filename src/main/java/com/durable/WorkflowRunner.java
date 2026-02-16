package com.durable;

public class WorkflowRunner {
    private final StepStore store;

    public WorkflowRunner() throws Exception {
        this.store = new StepStore();
    }

    public void run(String workflowId, WorkflowFunction workflow) throws Exception {
        DurableContext ctx = new DurableContext(workflowId, store);
        workflow.execute(ctx);
        System.out.println("Workflow " + workflowId + " completed successfully.");
    }
}