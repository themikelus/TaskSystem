package com.challenge.taskdemo.service;

import com.challenge.taskdemo.exception.TaskNotFoundException;
import com.challenge.taskdemo.util.Mapper;
import com.challenge.taskdemo.util.Status;
import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.repository.TaskRepository;
import com.challenge.taskdemo.entity.Task;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;

    @Before
    public void setUp() {
        taskService = new TaskService(taskRepository, new Mapper(new ModelMapper()));
    }

    @Test
    public void getAll() {
        given(taskRepository.findAll()).willReturn(getTasks());

        List<TaskDto> tasks = taskService.getAll();

        verify(taskRepository, times(1)).findAll();
        Assertions.assertThat(tasks.size()).isEqualTo(2);
        Assertions.assertThat(tasks.get(0).getName()).isEqualTo(tasks.get(0).getName());
        Assertions.assertThat(tasks.get(0).getDescription()).isEqualTo(tasks.get(0).getDescription());
        Assertions.assertThat(tasks.get(0).getStatus()).isEqualTo(tasks.get(0).getStatus());
        Assertions.assertThat(tasks.get(1).getName()).isEqualTo(tasks.get(1).getName());
        Assertions.assertThat(tasks.get(1).getDescription()).isEqualTo(tasks.get(1).getDescription());
        Assertions.assertThat(tasks.get(1).getStatus()).isEqualTo(tasks.get(1).getStatus());
    }

    @Test
    public void get_whenTaskExist_thenReturnTask() {
        Task task = getFullDataTask();
        given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));

        TaskDto taskDto = taskService.get(anyLong());

        verify(taskRepository, times(1)).findById(anyLong());
        Assertions.assertThat(taskDto.getName()).isEqualTo(task.getName());
        Assertions.assertThat(taskDto.getDescription()).isEqualTo(task.getDescription());
        Assertions.assertThat(taskDto.getStatus()).isEqualTo(task.getStatus());
    }

    @Test(expected = TaskNotFoundException.class)
    public void get_whenTaskDoesNotExist_thenReturnEmpty() {
        given(taskRepository.findById(anyLong())).willReturn(Optional.empty());

        taskService.get(anyLong());
    }

    @Test
    public void add() {
        Task task = getFullDataTask();
        given(taskRepository.save(any(Task.class))).willReturn(task);

        TaskDto taskDto = taskService.add(getFullDataTaskDto());

        verify(taskRepository, times(1)).save(any());
        Assertions.assertThat(taskDto.getName()).isEqualTo(task.getName());
        Assertions.assertThat(taskDto.getDescription()).isEqualTo(task.getDescription());
        Assertions.assertThat(taskDto.getStatus()).isEqualTo(task.getStatus());
    }

    @Test
    public void update_whenChanges_thenReturnNewDto() {
        Task persistedTask = getFullDataTask();
        given(taskRepository.findById(anyLong())).willReturn(Optional.of(persistedTask));

        Task updatedTask = getFullDataTask();
        updatedTask.setName("Updated name");
        updatedTask.setDescription("Updated description");
        updatedTask.setStatus(Status.DONE);

        given(taskRepository.save(any(Task.class))).willReturn(updatedTask);

        TaskDto requestTaskDto = new TaskDto();
        requestTaskDto.setName(updatedTask.getName());
        requestTaskDto.setDescription(updatedTask.getDescription());
        requestTaskDto.setStatus(updatedTask.getStatus());

        TaskDto taskDto = taskService.update(persistedTask.getId(), requestTaskDto);

        verify(taskRepository, times(1)).findById(persistedTask.getId());
        verify(taskRepository, times(1)).save(persistedTask);
        Assertions.assertThat(taskDto.getName()).isEqualTo(updatedTask.getName());
        Assertions.assertThat(taskDto.getDescription()).isEqualTo(updatedTask.getDescription());
        Assertions.assertThat(taskDto.getStatus()).isEqualTo(updatedTask.getStatus());
    }

    @Test
    public void delete_whenExist() {
        Task task = getFullDataTask();
        given(taskRepository.findById(anyLong())).willReturn(Optional.of(task));

        taskService.delete(anyLong());

        verify(taskRepository, times(1)).deleteById(anyLong());
    }

    @Test(expected = TaskNotFoundException.class)
    public void delete_whenDoesNotExist() {
        given(taskRepository.findById(anyLong())).willReturn(Optional.empty());

        taskService.delete(anyLong());
    }

    private Task getFullDataTask() {
        Task task = new Task();
        task.setName("My new task");
        task.setDescription("Task's description");
        task.setStatus(Status.TODO);
        task.setId(1L);
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
