package com.github.jovanbeldar.intellijaicodeexplainer.services;

public final class PromptBuilder {
    public static String buildExplanationPrompt(String code) {
        return """
            You are an experienced software engineer helping a developer understand code.

            Explain the following code clearly and concisely.

            Focus on:
            - what the code does
            - important logic
            - potential pitfalls
            - relevant language-specific behavior

            Keep the explanation technically accurate and beginner-friendly.

            If the code snippet is incomplete, explain the visible portion as best as possible.

            Code:
            ```java
            %s
            ```
            """.formatted(code);
    }
}
