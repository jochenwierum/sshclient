package de.jowisoftware.sshclient.ui.filetransfer;

import de.jowisoftware.sshclient.filetransfer.FileSystemChildrenProvider;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class Main extends JFrame {
    public Main() {
        super("TreeTest");

        addComponents();
        setupWindow();
    }

    private void addComponents() {
        final FilePanel filePanel = new FilePanel<>(new FileSystemChildrenProvider());
        //final FilePanel filePanel2 = new FilePanel<>(new FileSystemChildrenProvider());

        setLayout(new BorderLayout());
        add(filePanel, BorderLayout.CENTER);
        //add(filePanel2, BorderLayout.EAST);
        add(createUpdateButton(filePanel), BorderLayout.SOUTH);
    }

    private JButton createUpdateButton(final FilePanel panel) {
        final JButton button = new JButton("Update");
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                panel.updateSelected();
            }
        });
        return button;
    }

    private void setupWindow() {
        setSize(640, 480);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new Main();
    }
}
