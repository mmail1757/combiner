package com.tech.task;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

class CombinerImpl<T> extends Combiner<T> {

    private int count = 0;

    private final Comparator<Container> comparator = new Comparator<Container>() {
        @Override
        public int compare(Container o1, Container o2) {
            double count1 = o1.count / o1.priority;
            double count2 = o2.count / o2.priority;
            System.out.println(o1.hashCode() + ": " + o1.count + " / " + o1.priority + " = " + count1);
            System.out.println(o2.hashCode() + ": " + o2.count + " / " + o2.priority + " = " + count2);
            return Double.compare(count1, count2);
        }
    };

    private final PriorityQueue<Container> containers = new PriorityQueue<>(comparator);

    protected CombinerImpl(final SynchronousQueue<T> outputQueue) {
        super(outputQueue);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        synchronized (containers) {
                            if (containers.size() == 0) {
                                containers.wait();
                            }
                            Container container = containers.remove();
                            System.out.println(container.hashCode() + " selected");
                            T take = container.take();
                            System.out.println(Thread.currentThread() + " take " + take);
                            outputQueue.put(take);
                            count++;
                            containers.add(container);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void addInputQueue(BlockingQueue<T> blockingQueue, double priority, long isEmptyTimeout, TimeUnit timeUnit) throws CombinerException {
        synchronized (containers) {
            containers.add(new Container(blockingQueue, priority));
            containers.notify();
        }
    }

    private class Container {

        private final BlockingQueue<T> blockingQueue;
        private final double priority;
        private int count = 0;

        public Container(BlockingQueue<T> blockingQueue, double priority) {
            this.blockingQueue = blockingQueue;
            this.priority = priority;
        }

        public T take() throws InterruptedException {
            T t = blockingQueue.take();
            count++;
            return t;
        }
    }

    @Override
    public void removeInputQueue(BlockingQueue<T> queue) throws CombinerException {

    }

    @Override
    public boolean hasInputQueue(BlockingQueue<T> queue) {
        return false;
    }
}
