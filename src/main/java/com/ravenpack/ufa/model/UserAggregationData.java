package com.ravenpack.ufa.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

public class UserAggregationData {

    private final AtomicInteger totalMessages = new AtomicInteger(0);
    private final DoubleAdder totalScore = new DoubleAdder();

    public void addMessage(double score) {
        totalMessages.incrementAndGet();
        totalScore.add(score);
    }

    public int getTotalMessages() {
        return totalMessages.get();
    }

    public double getAverage() {
        return totalMessages.get() == 0 ? 0.0 : totalScore.doubleValue() / totalMessages.get();
    }
}
