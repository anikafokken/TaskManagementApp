package TaskManagementApp;
import java.time.LocalDateTime;

import TaskManagementApp.TaskManager.ManagerState;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        System.out.println(LocalDateTime.now());

        while (taskManager.getCurrentState() == TaskManager.ManagerState.RUNNING) {
            taskManager.runSystem();
        }
    }
}
