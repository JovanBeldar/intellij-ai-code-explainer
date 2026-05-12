package com.github.jovanbeldar.intellijaicodeexplainer.model;

import java.util.List;

public class ChatResponse {
    private List<Choice> choices;

    public ChatResponse() {
    }

    public ChatResponse(List<Choice> choices) {
        this.choices = choices;
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
