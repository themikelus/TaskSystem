package com.challenge.taskdemo.dto;

import com.challenge.taskdemo.util.Status;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class TaskDto {

    private Long id;

    @NotEmpty(message = "{validation.name.notEmpty}")
    private String name;

    @NotEmpty(message = "{validation.description.notEmpty}")
    private String description;

    private Status status;
}
