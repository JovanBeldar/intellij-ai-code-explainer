package com.github.jovanbeldar.intellijaicodeexplainer.actions;

import com.github.jovanbeldar.intellijaicodeexplainer.exceptions.AiServiceException;
import com.github.jovanbeldar.intellijaicodeexplainer.services.AiService;
import com.github.jovanbeldar.intellijaicodeexplainer.services.PromptBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class ExplainCodeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if(editor == null || project == null) {
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if(selectedText == null || selectedText.isBlank()) {
            return;
        }

        String prompt = PromptBuilder.buildExplanationPrompt(selectedText);

        new Task.Backgroundable(project, "Explaining code...", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    indicator.setText("Sending request to OpenAI...");

                    String explanation = AiService.explainCode(prompt);

                    indicator.setText("Preparing explanation...");

                    ApplicationManager.getApplication().invokeLater(()->{
                        Messages.showMessageDialog(explanation, "AI Code Explanation", Messages.getInformationIcon());
                    });
                } catch (AiServiceException e) {
                    ApplicationManager.getApplication().invokeLater(()->{
                        Messages.showErrorDialog(e.getMessage(), "AI Error");
                    });
                } catch (Exception e) {
                    ApplicationManager.getApplication().invokeLater(()->{
                        Messages.showErrorDialog("Unexpected error occurred.", "AI Error");
                    });
                }
            }
        }.queue();
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean enabled = editor != null && editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
