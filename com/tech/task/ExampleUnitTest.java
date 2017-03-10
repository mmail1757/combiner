package com.tech.task;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class ExampleUnitTest {

    @Test
    public void testEven2() throws Exception {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        CombinerImpl<String> combiner = new CombinerImpl<>(synchronousQueue);

        final LinkedBlockingQueue<String> queueA = new LinkedBlockingQueue<>(1);
        final LinkedBlockingQueue<String> queueB = new LinkedBlockingQueue<>(1);

        ProducerThread.produce(queueA, "A");
        ProducerThread.produce(queueB, "B");

        combiner.addInputQueue(queueA, 0.5, 1, TimeUnit.SECONDS);
        combiner.addInputQueue(queueB, 0.5, 1, TimeUnit.SECONDS);

        int count = 0;
        while (count++ < 1000) {
            String string = synchronousQueue.take();
            String thread = Thread.currentThread().getName();
            System.out.printf("%s consumed %s%n", thread, string);
        }
    }

    @Test
    public void testUneven2() throws Exception {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        CombinerImpl<String> combiner = new CombinerImpl<>(synchronousQueue);

        final LinkedBlockingQueue<String> queueA = new LinkedBlockingQueue<>(1);
        final LinkedBlockingQueue<String> queueB = new LinkedBlockingQueue<>(1);

        ProducerThread.produce(queueA, "A");
        ProducerThread.produce(queueB, "B");

        combiner.addInputQueue(queueA, 0.05, 1, TimeUnit.SECONDS);
        combiner.addInputQueue(queueB, 0.95, 1, TimeUnit.SECONDS);

        int count = 0;
        while (count++ < 1000) {
            String string = synchronousQueue.take();
            String thread = Thread.currentThread().getName();
            System.out.printf("%s consumed %s%n", thread, string);
        }
    }

    @Test
    public void testUneven3() throws Exception {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        CombinerImpl<String> combiner = new CombinerImpl<>(synchronousQueue);

        final LinkedBlockingQueue<String> queueA = new LinkedBlockingQueue<>(1);
        final LinkedBlockingQueue<String> queueB = new LinkedBlockingQueue<>(1);
        final LinkedBlockingQueue<String> queueC = new LinkedBlockingQueue<>(1);

        ProducerThread.produce(queueA, "A");
        ProducerThread.produce(queueB, "B");
        ProducerThread.produce(queueC, "C");

        combiner.addInputQueue(queueA, 0.1, 1, TimeUnit.SECONDS);
        combiner.addInputQueue(queueB, 0.3, 1, TimeUnit.SECONDS);
        combiner.addInputQueue(queueC, 0.6, 1, TimeUnit.SECONDS);

        int count = 0;
        while (count++ < 1000) {
            String string = synchronousQueue.take();
            String thread = Thread.currentThread().getName();
            System.out.printf("%s consumed %s%n", thread, string);
        }
    }

    private static class ProducerThread extends Thread {

        private final LinkedBlockingQueue<String> linkedBlockingQueue;
        private final String string;

        private ProducerThread(LinkedBlockingQueue<String> linkedBlockingQueue, String string) {
            this.linkedBlockingQueue = linkedBlockingQueue;
            this.string = string;
        }

        private static void produce(LinkedBlockingQueue<String> linkedBlockingQueue, String string) {
            ProducerThread producerThread = new ProducerThread(linkedBlockingQueue, string);
            producerThread.start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    linkedBlockingQueue.put(string);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
