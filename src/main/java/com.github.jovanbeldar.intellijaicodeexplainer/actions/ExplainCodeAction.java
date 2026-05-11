package com.github.jovanbeldar.intellijaicodeexplainer.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.jetbrains.annotations.NotNull;

public class ExplainCodeAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
//        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        if(editor == null) {
            return;
        }
        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if(selectedText == null || selectedText.isEmpty()) {
            return;
        }
        System.out.println(selectedText);
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        boolean enabled = editor != null && editor.getSelectionModel().hasSelection();

        e.getPresentation().setEnabledAndVisible(enabled);
    }
}
