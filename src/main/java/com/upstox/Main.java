package com.upstox;

import com.upstox.model.Trade;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class  Main {
    public static void main(String[] args) {

        String eventName = args[0];
        String stockName = args[1];
        String interval = args[2];

        BlockingQueue<Trade> queue = new ArrayBlockingQueue<>(1024);

        Reader reader = new Reader(queue, stockName);
        CalculateOHLC compute = new CalculateOHLC(queue, eventName, interval);

        new Thread(reader).start();
        new Thread(compute).start();
    }
}
