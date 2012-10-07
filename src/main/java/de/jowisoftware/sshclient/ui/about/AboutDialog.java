package de.jowisoftware.sshclient.ui.about;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import de.jowisoftware.sshclient.util.ApplicationUtils;

public class AboutDialog extends JDialog {
    private static final long serialVersionUID = -7714987053526548717L;
    private UpdateLabel updateLabel;

    public AboutDialog(final JFrame parent) {
        super(parent);
        createContents();
        setupListeners();
        setupWindow(parent);
    }

    private void setupListeners() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(final WindowEvent e) {
                updateLabel.stopThread();
            }
        });
    }

    private void setupWindow(final JFrame parent) {
        setTitle("About...");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(new Dimension(660, 500));
        setModal(true);
        setLocationRelativeTo(parent);
    }

    private void createContents() {
        setLayout(new BorderLayout());

        addContent();
        addButtonBar();
    }

    private void addContent() {
        final JPanel contentPanel = new JPanel();
        add(contentPanel, BorderLayout.CENTER);

        setupLayout(contentPanel);

        int offset = 0;

        addImage(contentPanel);
        addTitle(contentPanel, offset++);
        addVersionLabel(contentPanel, offset++);
        addUpdateLabel(contentPanel, offset++);
        addLicenseLabel(contentPanel, offset++);
        addThanksLabel(contentPanel, offset++);
    }

    private void setupLayout(final JPanel contentPanel) {
        final GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[] { 80, 0 };
        contentPanel.setLayout(layout);
    }

    private void addImage(final JPanel contentPanel) {
        final JLabel lblImage = new JLabel("Image");

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 0;
        constraints.gridheight = 5;
        contentPanel.add(lblImage, constraints);
    }

    private void addTitle(final JPanel contentPanel, final int offset) {
        final JLabel titleLabel = new JLabel("<html><b>SSH-Client</b></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(titleLabel, createDefaultConstrains(offset));
    }

    private void addVersionLabel(final JPanel contentPanel, final int offset) {
        final JLabel versionLabel = new JLabel(ApplicationUtils.getVersion());
        versionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(versionLabel, createDefaultConstrains(offset));
    }

    private void addLicenseLabel(final JPanel contentPanel, final int offset) {
        contentPanel.add(createLicenseControl(), createMaxHeightConstraints(offset));
    }

    private JScrollPane createLicenseControl() {
        final JLabel label = new JLabel(
                "<html><p>\r\nCopyright (c) 2011-2012, Jochen Wierum<br />"
                        + "All rights reserved.<br /></p>"
                        + "<p>Redistribution and use in source and binary forms, with or without<br />"
                        + "modification, are permitted provided that the following conditions are met:</p>"
                        + "<ul>"
                        + "<li>Redistributions of source code must retain the above copyright<br />"
                        + "    notice, this list of conditions and the following disclaimer.</li>"
                        + "<li>Redistributions in binary form must reproduce the above copyright<br />"
                        + "    notice, this list of conditions and the following disclaimer in the<br />"
                        + "    documentation and/or other materials provided with the distribution.</li>"
                        + "<li>Neither the name JoWiSoftware nor the<br />"
                        + "    names of its contributors may be used to endorse or promote products<br />"
                        + "    derived from this software without specific prior written permission.</li>"
                        + "</ul>"
                        + "<p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND<br />"
                        + "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED<br />"
                        + "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE<br />"
                        + "DISCLAIMED. IN NO EVENT SHALL JOCHEN WIERUM / JOWISOFTWARE BE LIABLE FOR ANY<br />"
                        + "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES<br />"
                        + "(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;<br />"
                        + "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND<br />"
                        + "ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT<br />"
                        + "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS<br />"
                        + "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
                        + "</p></html>");
        return new JScrollPane(label);
    }

    private void addThanksLabel(final JPanel contentPanel, final int offset) {
        contentPanel.add(createThanksLabel(), createDefaultConstrains(offset));
    }

    private JComponent createThanksLabel() {
        final JLabel label = new JLabel(
                "<html><p>This software is possible because the following people are working on it:</p>"
                        + "<ul>"
                        + "<li>Jochen Wierum (JoWiSoftware): Development</li>"
                        + "<li>Manuel Stamm: Tests and Ideas</li>"
                        + "<li>JCraft, Inc.: The SSH library</li>"
                        + "</ul></html>");
        label.setMaximumSize(label.getPreferredSize());
        return label;
    }

    private void addUpdateLabel(final JPanel contentPanel, final int offset) {
        updateLabel = createUpdateLabel();
        updateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(updateLabel, createDefaultConstrains(offset));
    }

    private UpdateLabel createUpdateLabel() {
        return new UpdateLabel();
    }

    private void addButtonBar() {
        final JPanel buttonPane = createButtonBar();
        add(buttonPane, BorderLayout.SOUTH);
    }

    private JPanel createButtonBar() {
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        final JButton okButton = createOkButton();

        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        return buttonPane;
    }

    private JButton createOkButton() {
        final JButton okButton = new JButton(t("ok", "OK"));
        okButton.setMnemonic(m("ok", 'o'));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        });

        return okButton;
    }

    private GridBagConstraints createDefaultConstrains(final int y) {
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.gridy = y;
        constraints.weightx = 1;
        constraints.weighty = 0;
        return constraints;
    }

    private GridBagConstraints createMaxHeightConstraints(final int y) {
        final GridBagConstraints constraints = createDefaultConstrains(y);
        constraints.weighty = 1;
        return constraints;
    }
}
