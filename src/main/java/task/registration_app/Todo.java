package task.registration_app;

public class Todo {
    /* FIELDS */
    private final String id;
    private String userId;
    private String todo;
    private final String createTime;

    /* CONSTRUCTOR */
    public Todo(String id, String userId, String todo, String createTime) {
        this.id = id;
        this.userId = userId;
        this.todo = todo;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTodo() {
        return todo;
    }
    public void setTodo(String todo) {
        this.todo = todo;
    }

    public String getCreateTime() {
        return createTime;
    }
}
