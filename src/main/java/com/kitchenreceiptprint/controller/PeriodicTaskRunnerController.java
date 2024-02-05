package com.kitchenreceiptprint.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Class PeriodicTaskRunnerController
 *
 * Manages the scheduling and execution of periodic tasks within the Kitchen Receipt Printing application.
 * Utilizes a single-threaded ScheduledExecutorService to manage task execution at fixed intervals.
 * Provides functionality to start, pause, resume, and kill the scheduled task, ensuring that tasks
 * can be managed dynamically at runtime.
 *
 * Responsibilities:
 * - Schedule and execute a given Runnable task at a specified interval.
 * - Provide control over the task execution, allowing it to be paused, resumed, or stopped.
 * - Track the execution state of the task to allow for querying its status.
 *
 * Usage:
 * This class is utilized by components that require periodic task execution, such as periodic checks
 * for new print jobs or other recurring operations. It abstracts the complexity of task scheduling
 * and state management.
 *
 * Author: Artisan Webmaster
 * Creation Date: 05/02/2024
 * Last Modification: 05/02/2024
 *
 * Notes:
 * - The class ensures that only one instance of the task is running at any given time by cancelling
 *   any previous instance before scheduling a new one.
 * - It provides a mechanism to safely stop the task execution, clearing any references to the runnable
 *   task and the ScheduledFuture to prevent memory leaks.
 */
public class PeriodicTaskRunnerController {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private static ScheduledFuture<?> futureTask = null;

    private static Runnable currentTask = null;

    private static int currentInterval;

    private static volatile boolean isRunning = false;

    static boolean messageDisplayed = false;

    public static void task(Runnable originalTask, int intervalInSeconds) {
        currentTask = () -> {
            isRunning = true;
            originalTask.run();
        };

        currentInterval = intervalInSeconds;

        if (futureTask != null && !futureTask.isCancelled()) {
            futureTask.cancel(true); // Annule la tâche précédente si elle est encore en cours
        }

        futureTask = executor.scheduleAtFixedRate(currentTask, 0, intervalInSeconds, TimeUnit.SECONDS);

    }

    public static void pause() {
        if (futureTask != null) {
            futureTask.cancel(false); // N'interrompt pas la tâche si elle est en cours d'exécution
            isRunning = false;
        }
    }

    public static void resume() {
        if (currentTask != null && (futureTask == null || futureTask.isCancelled())) {
            futureTask = executor.scheduleAtFixedRate(currentTask, 0, currentInterval, TimeUnit.SECONDS);
        }
    }

    public static void killTask() {
        if (futureTask != null) {
            futureTask.cancel(true);
            futureTask = null;
            currentTask = null;
            isRunning = false;
            System.out.println("Tâche tuée et réinitialisée");
        }
    }

    public static boolean isTaskRunning() {
        return isRunning;
    }
}
