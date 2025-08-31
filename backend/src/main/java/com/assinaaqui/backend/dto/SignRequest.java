package com.assinaaqui.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignRequest {

    @NotBlank(message = "Texto é obrigatório")
    @Size(min = 1, max = 10000, message = "Texto deve ter entre 1 e 10000 caracteres")
    private String text;

    // Constructors
    public SignRequest() {}

    public SignRequest(String text) {
        this.text = text;
    }

    // Getters and Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}