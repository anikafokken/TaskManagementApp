package TaskManagementApp;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.Serializable;
import java.io.FileNotFoundException;

public class TaskManager {
    private ArrayList<Task> tasks;
    private ArrayList<Task> completedTasks;
    public int tasksCount = 0;
    public int completedTasksCount = 0;

    private static final String FILE_NAME = "tasks.ser";

    Scanner runScanner = new Scanner(System.in);
    Scanner titleScanner = new Scanner(System.in);
    Scanner descScanner = new Scanner(System.in);
    Scanner dueScanner = new Scanner(System.in);
    Scanner priorityScanner = new Scanner(System.in);
    Scanner searchScanner = new Scanner(System.in);
    Scanner editScanner = new Scanner(System.in);
    Scanner deleteScanner = new Scanner(System.in);
    Scanner statusScanner = new Scanner(System.in);

    String ANSI_PURPLE = "\u001B[35m";
    String ANSI_CYAN = "\u001B[36m";
    String ANSI_RESET = "\u001B[0m";

    String priorityLastDigit;

    enum ManagerState {
        RUNNING, OVER;
    }

    private ManagerState currentState = ManagerState.RUNNING;

    public TaskManager() {
        this.tasks = new ArrayList<>();
        this.completedTasks = new ArrayList<>();
        loadTasks();
        switchTasksByPriority();
    }

    public ManagerState getCurrentState() {
        return currentState;
    }

    public void saveTasks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(tasks);
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadTasks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            tasks = (ArrayList<Task>) in.readObject();
            tasksCount = tasks.size();
        } catch (FileNotFoundException e) {
            tasks = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            tasks = new ArrayList<>();
        }
    }

    public void runSystem() {
        System.out.println(
                ANSI_PURPLE
                        + "\nWelcome to the Task Management App!" + ANSI_RESET
                        + "\n\n1. Add task\n2. View Tasks\n3. Edit task\n4. Delete task\n5. Search task\n6. Filter task\n7. Mark as done\n8. Exit");
        int runChoice = runScanner.nextInt();
        switch (runChoice) {
            case 1:
                addTask();
                break;
            case 2:
                viewTasks();
                break;
            case 3:
                editTask();
                break;
            case 4:
                deleteTask();
                break;
            case 5:
                searchTask();
                break;
            case 6:
                filterTasks();
                break;
            case 7:
                markAsDone();
                break;
            case 8:
                currentState = ManagerState.OVER;
        }
    }

    public void addTask() {
        System.out.println("Enter a title: ");
        String title = titleScanner.nextLine();
        System.out.println("Enter a description of your task: ");
        String description = descScanner.nextLine();
        System.out.println("Enter a due date (MM/DD/YYYY): ");
        String dueDate = dueScanner.nextLine();
        String slash = "/";
        if (dueDate.charAt(2) == slash.charAt(0) || dueDate.charAt(5) == slash.charAt(0) || dueDate.length() == 10) {
            tasks.add(new Task(tasksCount + 1, title, description, dueDate, tasksCount + 1, "Incomplete"));
            saveTasks();
            tasksCount++;
        } else {
            System.out.println("That is not an accepted input.");
        }
    }

    public void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available. ");
            return;
        }
        if (!tasks.isEmpty()) {
            System.out.println(ANSI_PURPLE + "\nIncomplete Tasks: " + ANSI_RESET);
        }
        for (int i = 0; i < tasksCount; i++) {
            System.out.println("\n" + ANSI_CYAN + (i+1) + ". "
                    + tasks.get(i).getTitle() + ": " + tasks.get(i).getDescription() + ANSI_RESET
                    + "\nID: " + tasks.get(i).getID() + ", Due: " + tasks.get(i).getDueDate() + ", Priority: "
                    + tasks.get(i).getPriority() + getNumberSuffix(tasks.get(i).getPriority()));

        }
        if (!completedTasks.isEmpty()) {
            System.out.println(ANSI_PURPLE + "\nComplete Tasks: \n" + ANSI_RESET);
        }
        for (int i = 0; i < completedTasksCount; i++) {
            System.out.println((i+1) + ". "
                    + completedTasks.get(i).getTitle() + ": " + completedTasks.get(i).getDescription()
                    + "\nID: " + completedTasks.get(i).getID() + ", Due: " + completedTasks.get(i).getDueDate());
        }

    }

    public void editTask() {
        System.out.println("Enter the ID of a task to edit: ");
        int taskID = searchScanner.nextInt();

        Task task = getTaskByID(taskID);
        if (task != null) {
            System.out.println("What would you like to edit? \n1. Title\n2. Description\n3. Due Date\n4. Priority");
            int editChoice = editScanner.nextInt();
            switch (editChoice) {
                case 1:
                    System.out.println("Enter a title: ");
                    String title = titleScanner.nextLine();
                    task.setTitle(title);
                    saveTasks();
                    break;
                case 2:
                    System.out.println("Enter a description of your task: ");
                    String description = descScanner.nextLine();
                    task.setDescription(description);
                    saveTasks();
                    break;
                case 3:
                    System.out.println("Enter a due date: ");
                    String dueDate = dueScanner.nextLine();
                    task.setDueDate(dueDate);
                    saveTasks();
                    break;
                case 4:
                    System.out.println("Enter a place of priority: ");
                    int priority = priorityScanner.nextInt();
                    task.setPriority(priority);
                    updatePriorities(task);
                    switchTasksByPriority();
                    saveTasks();
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }

    }

    public void deleteTask() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks to delete. ");
            return;
        }

        boolean taskFound = false;
        System.out.println("Enter a task title, ID, or description to delete: ");
        String deleteValue = deleteScanner.nextLine();

        for (int i = 0; i < tasksCount; i++) {
            if (String.valueOf(tasks.get(i).getID()).equals(deleteValue) || tasks.get(i).getTitle().equals(deleteValue)
                    || tasks.get(i).getDescription().equals(deleteValue)) {
                tasks.remove(i);
                System.out.println("Task successfully deleted.");
                taskFound = true;
                saveTasks();
                break;
            }
        }
    }

    public void searchTask() {
        System.out.println("Enter a task title, ID, or description: ");
        String searchValue = searchScanner.nextLine();
        for (int i = 0; i < tasksCount; i++) {
            if (String.valueOf(tasks.get(i).getID()).equals(searchValue) || tasks.get(i).getTitle().equals(searchValue)
                    || tasks.get(i).getDescription().equals(searchValue)) {
                System.out.println(ANSI_PURPLE + "\nMatched task: \n" + ANSI_RESET + tasks.get(i).getTitle() + ": "
                        + tasks.get(i).getDescription()
                        + "\nID: " + tasks.get(i).getID() + ", Due: " + tasks.get(i).getDueDate() + ", Priority: "
                        + tasks.get(i).getPriority() + getNumberSuffix(tasks.get(i).getPriority()) + ", Status: "
                        + tasks.get(i).getStatus() + "\n");
            }
        }
    }

    public void filterTasks() {

    }

    public void markAsDone() {
        System.out.println("Enter a task title, ID, or description: ");
        String searchValue = searchScanner.nextLine();
        for (int i = 0; i < tasksCount; i++) {
            if (String.valueOf(tasks.get(i).getID()).equals(searchValue) || tasks.get(i).getTitle().equals(searchValue)
                    || tasks.get(i).getDescription().equals(searchValue)) {
                tasks.get(i).setStatus("Complete");
                completedTasks.add(tasks.get(i));
                tasks.remove(i);
                completedTasksCount++;
                tasksCount--;
                break;
            }
        }
    }

    public int getLastDigit(int inputNumber) {
        String stringNum = String.valueOf(inputNumber);
        int lastDigit = Integer.valueOf(stringNum.charAt(stringNum.length() - 1));
        return lastDigit;
    }

    public String getNumberSuffix(int endNumber) {
        if (endNumber == 1) {
            return "st";
        } else if (endNumber == 2) {
            return "nd";
        } else if (endNumber == 3) {
            return "rd";
        } else {
            return "th";
        }
    }

    private Task getTaskByID(int ID) {
        for (Task task : tasks) {
            if (task.getID() == ID) {
                return task;
            }
        }
        return null;
    }

    public void switchTasksByPriority() {
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
                Task task1 = tasks.get(i);
                Task task2 = tasks.get(j);
                if (task1.getPriority() > task2.getPriority()) {
                    // Swap tasks at index i and j
                    Collections.swap(tasks, i, j);
                }
            }
        }
        saveTasks();
    }

    public void updatePriorities(Task updatedTask) {
        int newPriority = updatedTask.getPriority();

        for (Task task : tasks) {
            if (task != updatedTask && task.getPriority() == newPriority) {
                task.setPriority(task.getPriority() + 1);
            }
        }
        saveTasks();
    }
}
