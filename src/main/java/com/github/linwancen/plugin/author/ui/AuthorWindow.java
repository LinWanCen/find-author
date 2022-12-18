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
        UiUtils.onChange(input, inputState.getInput(), (e, s) -> inputState.setInput(s));
        UiUtils.onChange(leaveUser, ignoreState.getUser(), (e, s) -> ignoreState.setUser(s));
        UiUtils.onChange(ignoreMsg, ignoreState.getMsg(), (e, s) -> ignoreState.setMsg(s));
        UiUtils.onChange(ignoreRev, ignoreState.getRev(), (e, s) -> ignoreState.setRev(s));
        toRev.addActionListener(e -> ignoreRev.setText(GitLog.revs(project)));

        // region option
        OptionState optionState = state.getOption();
        UiUtils.onChange(fileEnds, optionState.getFileEnds(), (e, s) -> optionState.setFileEnds(s));

        logHelp.addActionListener(e -> GitRepos.run(project, GitCommand.LOG, "--help"));
        blameHelp.addActionListener(e -> GitRepos.run(project, GitCommand.BLAME, "--help"));

        resetGitFormat.addActionListener(e -> gitFormat.setText(OptionState.GIT_FORMAT));
        gitFormat.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                gitFormatDemo.setText(GitLog.demo(project, gitFormat.getText()));
            }
        });
        UiUtils.onChange(gitFormat, optionState.getGitFormat(), (e, s) -> {
            optionState.setGitFormat(s);
            gitFormatDemo.setText(GitLog.demo(project, s));
        });

        resetFindFormat.addActionListener(e -> findFormat.setText(OptionState.FIND_FORMAT));
        UiUtils.onChange(findFormat, optionState.getFindFormat(), (e, s) -> optionState.setFindFormat(s));
        findParams.setText(OptionState.getParams());
        // endregion option
    }

    public boolean beforeRun() {
        tab.setSelectedComponent(outTab);
        // for before 201.6668.113
        toolWindow.activate(null);
        if (!gitAuthor.isEnabled()) {
            return true;
        }
        gitAuthor.setEnabled(false);
        output.setText("");
        return false;
    }

    public void finallyForRun() {
        gitAuthor.setEnabled(true);
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
    private JTextArea fileEnds;

    private JButton logHelp;
    private JButton blameHelp;

    private JButton resetGitFormat;
    JTextArea gitFormat;
    private JTextArea gitFormatDemo;

    private JButton resetFindFormat;
    private JTextArea findFormat;
    private JTextArea findParams;
    // endregion option
}
