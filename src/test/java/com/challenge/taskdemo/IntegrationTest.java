package com.challenge.taskdemo;

import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.repository.TaskRepository;
import com.challenge.taskdemo.entity.Task;
import com.challenge.taskdemo.util.Status;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void getAllTasks_shouldReturnAllTasks() {
        //arrange
        List<Task> tasks = getTasks();
        taskRepository.saveAll(tasks);

        //act
        ResponseEntity<TaskDto[]> responseEntity = testRestTemplate.getForEntity("/tasks", TaskDto[].class);

        //assert API response
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody().length).isGreaterThanOrEqualTo(2);
        Assertions.assertThat(responseEntity.getBody()[0].getName()).isEqualTo(tasks.get(0).getName());
        Assertions.assertThat(responseEntity.getBody()[0].getDescription()).isEqualTo(tasks.get(0).getDescription());
        Assertions.assertThat(responseEntity.getBody()[0].getStatus()).isEqualTo(tasks.get(0).getStatus());
        Assertions.assertThat(responseEntity.getBody()[1].getName()).isEqualTo(tasks.get(1).getName());
        Assertions.assertThat(responseEntity.getBody()[1].getDescription()).isEqualTo(tasks.get(1).getDescription());
        Assertions.assertThat(responseEntity.getBody()[1].getStatus()).isEqualTo(tasks.get(1).getStatus());
    }

    @Test
    public void getTaskById_shouldReturnATask() {
        //arrange
        Task task = getFullDataTask();
        task = taskRepository.save(task);

        //act
        ResponseEntity<TaskDto> responseEntity = testRestTemplate.getForEntity("/tasks/" + task.getId(), TaskDto.class);

        //assert API response
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo(task.getName());
        Assertions.assertThat(responseEntity.getBody().getDescription()).isEqualTo(task.getDescription());
        Assertions.assertThat(responseEntity.getBody().getStatus()).isEqualTo(task.getStatus());
    }

    @Test
    public void addTask_shouldReturnATask() {
        //arrange
        TaskDto taskDto = getFullDataTaskDto();

        //act
        HttpEntity<TaskDto> request = new HttpEntity<>(taskDto);
        ResponseEntity<TaskDto> responseEntity = testRestTemplate.postForEntity("/tasks", request, TaskDto.class);

        //assert API response
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo(taskDto.getName());
        Assertions.assertThat(responseEntity.getBody().getDescription()).isEqualTo(taskDto.getDescription());
        Assertions.assertThat(responseEntity.getBody().getStatus()).isEqualTo(Status.TODO);
        Assertions.assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        //get id of new resource
        URI location = responseEntity.getHeaders().getLocation();
        Long id = new Long(location.getPath().substring(location.getPath().lastIndexOf("/") + 1));

        //assert environment state
        Assertions.assertThat(taskRepository.findById(id).isPresent()).isEqualTo(true);
        Assertions.assertThat(taskRepository.findById(id).get().getName()).isEqualTo(taskDto.getName());
        Assertions.assertThat(taskRepository.findById(id).get().getDescription()).isEqualTo(taskDto.getDescription());
        Assertions.assertThat(taskRepository.findById(id).get().getStatus()).isEqualTo(Status.TODO);
    }

    @Test
    public void updateTask_shouldReturnATask() {
        //arrange
        Task task = getFullDataTask();
        task = taskRepository.save(task);

        TaskDto taskDto = new TaskDto();
        taskDto.setName("Updated name");
        taskDto.setDescription("Updated description");
        taskDto.setStatus(Status.DONE);

        //act
        HttpEntity<TaskDto> request = new HttpEntity<>(taskDto);
        ResponseEntity<TaskDto> responseEntity = testRestTemplate.exchange("/tasks/" + task.getId(), HttpMethod.PATCH, request, TaskDto.class);

        //assert API response
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody().getName()).isEqualTo(taskDto.getName());
        Assertions.assertThat(responseEntity.getBody().getDescription()).isEqualTo(taskDto.getDescription());
        Assertions.assertThat(responseEntity.getBody().getStatus()).isEqualTo(taskDto.getStatus());

        //assert environment state
        Optional<Task> persistedTask = taskRepository.findById(task.getId());
        Assertions.assertThat(persistedTask.isPresent()).isEqualTo(true);
        Assertions.assertThat(persistedTask.get().getName()).isEqualTo(taskDto.getName());
        Assertions.assertThat(persistedTask.get().getDescription()).isEqualTo(taskDto.getDescription());
        Assertions.assertThat(persistedTask.get().getStatus()).isEqualTo(taskDto.getStatus());
    }

    @Test
    public void deleteTask() {
        //arrange
        Task task = getFullDataTask();
        task = taskRepository.save(task);

        //act
        ResponseEntity responseEntity = testRestTemplate.exchange("/tasks/" + task.getId(), HttpMethod.DELETE, null, ResponseEntity.class);

        //assert API response
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        //assert environment state
        Assertions.assertThat(taskRepository.findById(task.getId()).isPresent()).isEqualTo(false);
    }

    private Task getFullDataTask() {
        Task task = new Task();
        task.setName("My new task");
        task.setDescription("Task's description");
        task.setStatus(Status.TODO);

        return task;
    }

    private TaskDto getFullDataTaskDto() {
        TaskDto taskDto = new TaskDto();
        taskDto.setName("My new task");
        taskDto.setDescription("Task's description");

        return taskDto;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<Task>();
        tasks.add(getFullDataTask());
        tasks.add(getFullDataTask());

        return tasks;
    }
}
