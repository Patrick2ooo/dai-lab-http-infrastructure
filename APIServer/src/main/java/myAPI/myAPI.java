package myAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.net.InetAddress;

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
        app.put("/todos/update/{index}/{text}", updateTodoHandler());
        app.delete("/todos/delete/{index}", deleteTodoHandler());
    }

    private static Handler getTodosHandler() {
        return ctx -> {        
        try {
            String hostname = InetAddress.getLocalHost().getHostName();
            ctx.json(Map.of(
                "hostname", hostname,
                "todos", todos
            ));
        } catch (Exception e) {
            ctx.status(500).result("Error retrieving hostname");
        }};
    }

    private static Handler addTodoHandler() {
        return ctx -> {
            todos.add(ctx.pathParam("text"));
            ctx.status(201); // HTTP status code for "Created"
            ctx.json(todos);
        };
    }

    private static Handler updateTodoHandler() {
        return ctx -> {
            try {
                int index = Integer.parseInt(ctx.pathParam("index"));
                String newText = ctx.pathParam("text");
                if (index >= 0 && index < todos.size()) {
                    todos.set(index, newText);
                    ctx.status(200); // HTTP status code for "OK"
                    ctx.json(todos);
                } else {
                    ctx.status(404); // Not Found
                }
            } catch (NumberFormatException e) {
                ctx.status(400); // Bad Request
            }
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