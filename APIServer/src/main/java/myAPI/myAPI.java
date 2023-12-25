package myAPI;

import java.util.ArrayList;
import java.util.List;

import io.javalin.Javalin;

import io.javalin.http.Handler;

public class myAPI {
    private static List<String> todos = new ArrayList<>();
    static {
        todos.add("Buy groceries");
        todos.add("Read a book");
        todos.add("Complete lab assignment");
    }
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/todos", getTodosHandler());
        app.post("/todos/add/{text}", addTodoHandler());
        app.delete("/todos/delete/{index}", deleteTodoHandler());
    }

    private static Handler getTodosHandler() {
        return ctx -> {ctx.json(todos);};
    }

    private static Handler addTodoHandler() {
        return ctx -> {
            todos.add(ctx.pathParam("text"));
            ctx.status(201); // HTTP status code for "Created"
            ctx.json(todos);
        };
    }

    private static Handler deleteTodoHandler() {
        return ctx -> {
            try {
                int index = Integer.parseInt(ctx.pathParam("index"));
                todos.remove(index);
                ctx.status(204); // No Content
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                ctx.status(400); // Bad Request
            }
        };
    }
}