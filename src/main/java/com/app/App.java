package com.app;

import com.durable.WorkflowRunner;
import com.onboarding.OnboardingWorkflow;

public class App {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java App <workflowId> [employeeName]");
            System.exit(1);
        }

        String workflowId = args[0];
        String name = args.length > 1 ? args[1] : "Pranav Sharma";

        System.out.println("🚀 Starting/Resuming workflow: " + workflowId);
        System.out.println("Employee: " + name + "\n");

        WorkflowRunner runner = new WorkflowRunner();
        OnboardingWorkflow workflow = new OnboardingWorkflow();

        runner.run(workflowId, ctx -> workflow.run(ctx, name));
    }
}