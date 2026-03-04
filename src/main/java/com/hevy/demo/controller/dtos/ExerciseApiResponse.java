package com.hevy.demo.controller.dtos;

public record ExerciseApiResponse<T>(boolean success, T data) {

}
