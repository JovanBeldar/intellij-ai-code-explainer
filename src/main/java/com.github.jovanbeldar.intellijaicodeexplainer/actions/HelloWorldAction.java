package com.github.jovanbeldar.intellijaicodeexplainer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class HelloWorldAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Messages.showMessageDialog("Hello from my plugin!", "Hello", Messages.getInformationIcon());
        /*ObjectMapper mapper = new ObjectMapper();
        Message message = new Message("user", "explain this code");
        ChatRequest request = new ChatRequest("gpt-4.1-mini", List.of(message));
        try {
            String json = mapper.writeValueAsString(request);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }*/
        System.out.println(System.getenv("OPENAI_API_KEY"));
    }
}
