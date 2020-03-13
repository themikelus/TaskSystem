package com.challenge.taskdemo.controller;

import com.challenge.taskdemo.config.MessageConfig;
import com.challenge.taskdemo.util.Status;
import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.exception.TaskNotFoundException;
import com.challenge.taskdemo.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void getAll() throws Exception {
        given(taskService.getAll()).willReturn(getTasksDto());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/tasks")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAll_ShouldReturnEmptyArray() throws Exception {
        given(taskService.getAll()).willReturn(new ArrayList<>());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/tasks")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
    }

    @Test
    public void get() throws Exception {
        TaskDto taskDto = getFullDataTaskDto();
        given(taskService.get(anyLong())).willReturn(taskDto);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/tasks/1")
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(taskDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(taskDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(Status.TODO.name()));
    }

    @Test
    public void get_ShouldReturnNotFoundException() throws Exception {
        given(taskService.get(anyLong())).willThrow(new TaskNotFoundException());

        mockMvc.perform(MockMvcRequestBuilders.get("/tasks/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void add() throws Exception {
        TaskDto taskDto = getFullDataTaskDto();
        given(taskService.add(any(TaskDto.class))).willReturn(taskDto);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDto));

        mockMvc.perform(builder)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(taskDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(taskDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(Status.TODO.name()));
    }

    @Test
    public void add_ShouldReturnExceptionMissingRequiredFields() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/tasks")
                .accept(MediaType.APPLICATION_JSON)
                .locale(Locale.ENGLISH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new TaskDto()));

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("timestamp", is(notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("errors").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasItem("{validation.name.notEmpty}")))
                .andExpect(MockMvcResultMatchers.jsonPath("errors", hasItem("{validation.description.notEmpty}")));
    }

    @Test
    public void update() throws Exception {
        TaskDto taskDto = getFullDataTaskDto();
        given(taskService.update(anyLong(), any(TaskDto.class))).willReturn(taskDto);

        taskDto.setDescription("Updated description");
        String taskDescriptionJson = "{\"description\":\"" + taskDto.getDescription() + "\"}";

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.patch("/tasks/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskDescriptionJson);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(taskDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("description").value(taskDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(Status.TODO.name()));
    }

    @Test
    public void delete() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete("/tasks/1");

        mockMvc.perform(builder)
                .andExpect(status().isOk());
    }

    private TaskDto getFullDataTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("My new task");
        taskDto.setDescription("Task's description");
        taskDto.setStatus(Status.TODO);

        return taskDto;
    }

    private List<TaskDto> getTasksDto() {
        List<TaskDto> tasks = new ArrayList<TaskDto>();
        tasks.add(getFullDataTaskDto());
        tasks.add(getFullDataTaskDto());

        return tasks;
    }
}
