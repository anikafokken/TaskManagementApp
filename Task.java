package TaskManagementApp;
import java.io.Serializable;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String description;
    private String dueDate;
    private int priority;
    private String status;

    public Task (int id, String title, String description, String dueDate, int priority, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }
    
    // public boolean contains(CharSequence chars) {
    //     return indexOf(chars.toString()) > -1;
    // }

    public int getID() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getDueDate() { return dueDate; }

    public int getPriority() { return priority; }

    public String getStatus() { return status; }


    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setDueDate(String newDueDate) {
        this.dueDate = newDueDate;
    }
    
    public void setPriority(int newPriority) {
        this.priority = newPriority;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }
}
