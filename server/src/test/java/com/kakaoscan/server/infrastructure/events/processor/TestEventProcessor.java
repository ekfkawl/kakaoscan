package com.kakaoscan.server.infrastructure.events.processor;

public class TestEventProcessor implements EventProcessor {
    private boolean processCalled = false;

    @Override
    public void process(String eventData) {
        processCalled = true;
        System.out.println("---------------------------------------------");
        System.out.println("processing event data: " + eventData);
        System.out.println("---------------------------------------------");
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isProcessCalled() {
        return processCalled;
    }
}