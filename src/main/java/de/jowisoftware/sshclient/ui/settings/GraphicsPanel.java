package de.jowisoftware.sshclient.ui.settings;

import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.ui.terminal.AWTGfxInfo;
import de.jowisoftware.sshclient.util.FontUtils;
import de.jowisoftware.sshclient.util.KeyValue;
import de.jowisoftware.sshclient.util.StringUtils;

class GraphicsPanel extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = -2187556102454714257L;

    private final JComboBox fontBox = createFontSelectionBox();
    private final JTextField fontSizeTextField = new JTextField(2);
    private final JComboBox antiAliasingBox = createAntiAliasingBox();
    private final AWTGfxInfo gfxSettings;

    public GraphicsPanel(final AWTGfxInfo gfxSettings) {
        this.gfxSettings = gfxSettings;

        addFontControls(0);
        addVerticalSpacing(3);
        addCursorColorChooser(4);
        addVerticalSpacing(5);
        addColorChooserMatrix(6);
        fillToBottom(7);
    }

    private void addVerticalSpacing(final int offset) {
        add(new JLabel(" "), makeConstraints(2, offset + 1));
    }

    private void addFontControls(final int offset) {
        add(new JLabel(t("profiles.colors.font", "font:")),
                makeConstraints(1, offset + 1));

        fontBox.setSelectedItem(gfxSettings.getFontName());
        add(fontBox, makeConstraints(2, offset + 1));

        add(new JLabel(t("profiles.colors.font.size", "size:")),
                makeConstraints(1, offset + 2));

        fontSizeTextField.setText(Integer.toString(gfxSettings.getFontSize()));
        add(fontSizeTextField, makeConstraints(2, offset + 2));

        antiAliasingBox.setSelectedIndex(gfxSettings.getAntiAliasingMode());
        add(new JLabel(t("profiles.color.antialiasing", "antialiasing:")),
                makeConstraints(1, offset + 3));
        add(antiAliasingBox, makeConstraints(2, offset + 3));
    }

    private JComboBox createFontSelectionBox() {
        final String names[] = FontUtils.getCachedMonospacedFonts();
        Arrays.sort(names);
        return new JComboBox(names);
    }

    private void addColorChooserMatrix(final int offset) {
        final GridBagConstraints constraints = makeConstraints(1, offset + 1);
        constraints.gridwidth = 2;
        add(new ColorPanel(gfxSettings), constraints);
    }

    private void addCursorColorChooser(final int offset) {
        add(new JLabel(t("profiles.colors.cursor",
                "cursor color:")), makeConstraints(1, offset + 1));
        add(createCursorColorButton(), makeConstraints(2, offset + 1));
    }


    private JButton createCursorColorButton() {
        return new AbstractColorButton(gfxSettings.getCursorColor()) {
            private static final long serialVersionUID = -4068617458848972294L;

            @Override
            void saveColor(final Color newColor, final ActionEvent e) {
                gfxSettings.setCursorColor(newColor);
            }
        };
    }


    private JComboBox createAntiAliasingBox() {
        final List<KeyValue<String, Object>> hints = FontUtils.getRenderingHintMap();
        final String names[] = new String[hints.size()];

        int i = 0;
        for (final KeyValue<String, Object> hint : hints) {
            names[i++] = t("profiles.color.antialiasing." + hint.key, hint.key);
        }

        return new JComboBox(names);
    }

    @Override
    public String getTitle() {
        return t("profiles.gfx.title", "graphics");
    }

    @Override
    public void save() {
        gfxSettings.setAntiAliasingMode(antiAliasingBox.getSelectedIndex());
        gfxSettings.setFont(
                (String) fontBox.getSelectedItem(),
                StringUtils.getInteger(fontSizeTextField.getText(), 10));

    }

}
