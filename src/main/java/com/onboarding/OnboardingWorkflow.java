package com.onboarding;

import java.util.concurrent.CompletableFuture;

import com.durable.DurableContext;

public class OnboardingWorkflow {

    public void run(DurableContext ctx, String employeeName) throws Exception {
        // Step 1 - Sequential
        EmployeeRecord record = ctx.step("createRecord", () -> {
            System.out.println("Creating employee record for " + employeeName);
            Thread.sleep(800);
            return new EmployeeRecord(employeeName, "EMP-" + System.currentTimeMillis() % 10000);
        }, EmployeeRecord.class);

        // Steps 2 & 3 - Parallel
        CompletableFuture<Laptop> laptopFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ctx.step("provisionLaptop", () -> {
                    System.out.println("Provisioning laptop...");
                    Thread.sleep(1200);
                    return new Laptop("LAP-" + record.empId());
                }, Laptop.class);
            } catch (Exception e) { throw new RuntimeException(e); }
        });

        CompletableFuture<AccessCard> accessFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return ctx.step("provisionAccess", () -> {
                    System.out.println("Provisioning access card...");
                    Thread.sleep(900);
                    return new AccessCard("ACC-" + record.empId());
                }, AccessCard.class);
            } catch (Exception e) { throw new RuntimeException(e); }
        });

        Laptop laptop = laptopFuture.get();
        AccessCard access = accessFuture.get();

        // Step 4 - Sequential
        ctx.step("sendWelcomeEmail", () -> {
            System.out.println("Sending welcome email to " + record.name() +
                    " | Laptop: " + laptop.serial() + " | Access: " + access.code());
            Thread.sleep(500);
            return "SENT";
        }, String.class);
    }
}