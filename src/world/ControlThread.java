package world;

import world.task.DiffusionTask;

/** 这个线程用来控制更新地图的后台任务 */
public class ControlThread extends Thread {
    private volatile WorldData worldData;
    private volatile boolean running = true;
    private final float intervalMillis;

    private static DiffusionTask diffusionTask;
    

    public ControlThread(float intervalMillis) {
        super("LoomingAbyss-ControlThread");
        this.intervalMillis = Math.max(0, intervalMillis);;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (this) {
                    diffusionTask.run();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (!running) {
                break;
            }
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }

    public boolean isRunning() {
        return running && !isInterrupted();
    }
}