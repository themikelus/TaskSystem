package com.challenge.taskdemo.controller;

import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.exception.TaskNotFoundException;
import com.challenge.taskdemo.service.TaskService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@AllArgsConstructor
public class TaskController {

    private TaskService taskService;

    @GetMapping("/tasks")
    private List<TaskDto> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/tasks/{id}")
    private TaskDto get(@PathVariable Long id) {
        return taskService.get(id);
    }

    @PostMapping("/tasks")
    private ResponseEntity<TaskDto> add(@Valid @RequestBody TaskDto taskDto) {
        TaskDto taskDtoNewResource = taskService.add(taskDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(taskDtoNewResource.getId()).toUri();

        return ResponseEntity.created(location).body(taskDtoNewResource);
    }

    @PatchMapping("/tasks/{id}")
    private TaskDto update(@RequestBody TaskDto taskDto, @PathVariable Long id) {
        return taskService.update(id, taskDto);
    }

    @DeleteMapping("/tasks/{id}")
    private void delete(@PathVariable Long id) {
        taskService.delete(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private void taskNotFoundHandler(TaskNotFoundException taskNotFoundException) {}
}
