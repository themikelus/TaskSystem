package com.challenge.taskdemo.util;

import com.challenge.taskdemo.dto.TaskDto;
import com.challenge.taskdemo.entity.Task;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class Mapper {
    private ModelMapper modelMapper;

    public TaskDto convertToDto(Task task) {
        return this.modelMapper.map(task, TaskDto.class);
    }

    public Task convertToEntity(TaskDto taskDto) {
        return this.modelMapper.map(taskDto, Task.class);
    }
}
