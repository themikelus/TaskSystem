package com.challenge.taskdemo.service;

import com.challenge.taskdemo.util.Mapper;
import com.challenge.taskdemo.util.Status;
import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.entity.Task;
import com.challenge.taskdemo.exception.TaskNotFoundException;
import com.challenge.taskdemo.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TaskService {

    private TaskRepository taskRepository;
    private Mapper mapper;

    public List<TaskDto> getAll() {
        List<TaskDto> tasksDto = new ArrayList<TaskDto>();

        Iterable<Task> tasks = taskRepository.findAll();

        if (tasks != null) {
            for (Task task : tasks) {
                tasksDto.add(mapper.convertToDto(task));
            }
        }

        return tasksDto;
    }

    public TaskDto get(Long id) {
        return mapper.convertToDto(getById(id));
    }

    public TaskDto add(TaskDto taskDto) {
        taskDto.setStatus(Status.TODO);

        return mapper.convertToDto(taskRepository.save(mapper.convertToEntity(taskDto)));
    }

    public TaskDto update(Long id, TaskDto taskDto) {
        Task task = getById(id);

        if (taskDto.getName() != null && !taskDto.getName().isEmpty()) {
            task.setName(taskDto.getName());
        }

        if (taskDto.getDescription() != null && !taskDto.getDescription().isEmpty()) {
            task.setDescription(taskDto.getDescription());
        }

        if (taskDto.getStatus() != null) {
            task.setStatus(taskDto.getStatus());
        }

        return mapper.convertToDto(taskRepository.save(task));
    }

    public void delete(Long id) {
        getById(id);
        taskRepository.deleteById(id);
    }

    private Task getById(Long id) {
        Optional<Task> optional = taskRepository.findById(id);

        if (!optional.isPresent()) {
            throw new TaskNotFoundException();
        }

        return optional.get();
    }
}
