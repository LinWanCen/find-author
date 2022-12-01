package com.github.linwancen.plugin.author.ui;

import com.github.linwancen.plugin.author.git.GitLog;
import com.github.linwancen.plugin.author.git.GitRepos;
import com.github.linwancen.plugin.author.settings.IgnoreState;
import com.github.linwancen.plugin.author.settings.InputState;
import com.github.linwancen.plugin.author.settings.OptionState;
import com.github.linwancen.plugin.author.settings.State;
import com.github.linwancen.plugin.common.ui.UiUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import git4idea.commands.GitCommand;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class AuthorWindow {
    Project project;
    ToolWindow toolWindow;

    public AuthorWindow(@NotNull Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;
        State state = new State(project);

        gitAuthor.addActionListener(e -> FileLineAuthorController.getAuthor(this));

        // leave user and ignore msg/rev
        InputState inputState = state.getInput();
        IgnoreState ignoreState = state.getIgnore();
        UiUtils.onChange(input, inputState.getInput(), e -> inputState.setInput(input.getText()));
        UiUtils.onChange(leaveUser, ignoreState.getUser(), e -> ignoreState.setUser(leaveUser.getText()));
        UiUtils.onChange(ignoreMsg, ignoreState.getMsg(), e -> ignoreState.setMsg(ignoreMsg.getText()));
        UiUtils.onChange(ignoreRev, ignoreState.getRev(), e -> ignoreState.setRev(ignoreRev.getText()));
        toRev.addActionListener(e -> ignoreRev.setText(GitLog.revs(project)));

        // region option
        OptionState optionState = state.getOption();
        resetFormat.addActionListener(e -> format.setText(GitLog.DEFAULT_FORMAT));
        logHelp.addActionListener(e -> GitRepos.run(project, GitCommand.LOG, "--help"));
        blameHelp.addActionListener(e -> GitRepos.run(project, GitCommand.BLAME, "--help"));
        format.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                formatDemo.setText(GitLog.demo(project, format.getText()));
            }
        });
        UiUtils.onChange(format, optionState.getFormat(), e -> {
            optionState.setFormat(format.getText());
            formatDemo.setText(GitLog.demo(project, format.getText()));
        });
        // endregion option
    }

    JPanel mainPanel;
    JButton gitAuthor;

    JTabbedPane tab;

    JTextArea input;

    // region output
    JScrollPane outTab;
    JTextArea output;
    // endregion output

    private JTextArea leaveUser;

    // region ignore
    private JButton toRev;
    private JTextArea ignoreMsg;
    private JTextArea ignoreRev;
    // endregion ignore

    // region option
    private JButton logHelp;
    private JButton blameHelp;
    private JButton resetFormat;
    JTextField format;
    private JTextField formatDemo;
    // endregion option
}
