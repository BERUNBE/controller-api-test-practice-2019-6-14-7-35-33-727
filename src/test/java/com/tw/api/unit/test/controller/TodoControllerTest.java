package com.tw.api.unit.test.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    public void todoController_getAll_should_return_all_todos() throws Exception {
        Todo todo1 = new Todo(1, "title", true, 1);
        Todo todo2 = new Todo(2, "title2", true, 2);
        when(todoRepository.getAll()).thenReturn(Arrays.asList(todo1, todo2));

        ResultActions result = mvc.perform(get("/todos"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[1].title").value("title2"));
    }

    @Test
    public void todoController_getTodo_should_return_one_todo() throws Exception {
        Todo todo = new Todo(1, "title", true, 1);
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));

        ResultActions result = mvc.perform(get("/todos/{todo-id}", "1"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    public void todoController_saveTodo_should_return_one_todo() throws Exception {
        Todo todo1 = new Todo(1, "title", true, 1);
        String inputJson = mapToJson(todo1);

        ResultActions result = mvc.perform(post("/todos")
                .contentType(APPLICATION_JSON)
                .content(inputJson));

        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("title"));
    }

    @Test
    public void todoController_deleteOneTodo_should_delete_one_existing_todo() throws Exception {
        Todo todo = new Todo(1, "title", true, 1);
        when(todoRepository.findById(1)).thenReturn(Optional.of(todo));

        ResultActions resultDelete = mvc.perform(delete("/todos/{todo-id}", "1"));

        resultDelete.andExpect(status().isOk());
    }

    @Test
    public void todoController_deleteOneTodo_should_return_404_todo_does_not_exist() throws Exception {
        ResultActions resultDelete = mvc.perform(delete("/todos/{todo-id}", "1"));

        resultDelete.andExpect(status().isNotFound());
    }


    @Test
    public void todoController_updateTodo_should_update_one_todo_and_return_updated_todo() throws Exception {
        Todo oldTodo = new Todo(1, "oldtitle", true, 1);
        when(todoRepository.findById(1)).thenReturn(Optional.of(oldTodo));
        Todo updatedTodo = new Todo(1, "updatedTitle", true, 1);
        String inputJson = mapToJson(updatedTodo);

        ResultActions result = mvc.perform(patch("/todos/{todo-id}", "1")
                .contentType(APPLICATION_JSON)
                .content(inputJson));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("updatedTitle"));
    }

    public String mapToJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
}
