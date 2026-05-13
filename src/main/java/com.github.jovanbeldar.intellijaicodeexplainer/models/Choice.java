package com.github.jovanbeldar.intellijaicodeexplainer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {
    private Message message;

    public Choice() {
    }

    public Choice(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
