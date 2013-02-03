package de.jowisoftware.sshclient.ui.settings.profile;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import de.jowisoftware.sshclient.terminal.gfx.CursorStyle;
import de.jowisoftware.sshclient.terminal.gfx.awt.AWTGfxInfo;
import de.jowisoftware.sshclient.ui.settings.AbstractColorButton;
import de.jowisoftware.sshclient.ui.settings.AbstractGridBagOptionPanel;
import de.jowisoftware.sshclient.util.FontUtils;
import de.jowisoftware.sshclient.util.KeyValue;
import de.jowisoftware.sshclient.util.StringUtils;

class GraphicsPanel extends AbstractGridBagOptionPanel {
    private static final long serialVersionUID = -2187556102454714257L;

    private final AWTGfxInfo gfxSettings;

    private final JComboBox<String> fontBox = createFontSelectionBox();
    private final JTextField fontSizeTextField = new JTextField(2);
    private final JComboBox<String> antiAliasingBox = createAntiAliasingBox();
    private final JComboBox<String> cursorStyleBox = createCursorStyleBox();

    public GraphicsPanel(final AWTGfxInfo gfxSettings, final Window parent) {
        super(parent);
        this.gfxSettings = gfxSettings;

        addFontControls(0);
        addVerticalSpacing(3);
        addCursorColorChooser(4);
        addVerticalSpacing(5);
        addCursorStyle(6);
        addVerticalSpacing(8);
        addColorChooserMatrix(9);
        fillToBottom(10);
    }

    private void addVerticalSpacing(final int offset) {
        add(new JLabel(" "), makeConstraints(2, offset + 1));
    }

    private void addFontControls(final int offset) {
        add(label("profiles.colors.font", "Font:", 'f', fontBox),
                makeLabelConstraints(offset + 1));

        fontBox.setSelectedItem(gfxSettings.getFontName());
        add(fontBox, makeConstraints(2, offset + 1));

        add(label("profiles.colors.font.size", "Size:", 'i', fontSizeTextField),
                makeLabelConstraints(offset + 2));

        fontSizeTextField.setText(Integer.toString(gfxSettings.getFontSize()));
        add(fontSizeTextField, makeConstraints(2, offset + 2));

        antiAliasingBox.setSelectedIndex(gfxSettings.getAntiAliasingMode());
        add(label("profiles.color.antialiasing", "Antialiasing:", 'a', antiAliasingBox),
                makeLabelConstraints(offset + 3));
        add(antiAliasingBox, makeConstraints(2, offset + 3));
    }

    private JComboBox<String> createFontSelectionBox() {
        final String names[] = FontUtils.getCachedMonospacedFonts();
        Arrays.sort(names);
        return new JComboBox<String>(names);
    }

    private void addColorChooserMatrix(final int offset) {
        final GridBagConstraints constraints = makeConstraints(1, offset + 1);
        constraints.gridwidth = 2;
        add(new ColorPanel(gfxSettings), constraints);
    }

    private void addCursorColorChooser(final int offset) {
        final JButton colorButton = createCursorColorButton();
        add(label("profiles.colors.cursor", "Cursor color:",
                'r', colorButton), makeLabelConstraints(offset + 1));
        add(colorButton, makeConstraints(2, offset + 1));
    }

    private void addCursorStyle(final int offset) {
        add(label("profiles.cursor.style", "Cursor Style:", 'c', cursorStyleBox),
                makeLabelConstraints(offset + 1));
        cursorStyleBox.setSelectedIndex(gfxSettings.getCursorStyle().ordinal());
        add(cursorStyleBox, makeConstraints(2, offset + 1));

        final JCheckBox checkbox = createBlinkingCheckBox();
        checkbox.setSelected(gfxSettings.cursorBlinks());
        add(checkbox, makeConstraints(2, offset + 2));
    }

    private JButton createCursorColorButton() {
        return new AbstractColorButton(gfxSettings.getCursorColor()) {
            private static final long serialVersionUID = -4068617458848972294L;

            @Override
            protected void saveColor(final Color newColor, final ActionEvent e) {
                gfxSettings.setCursorColor(newColor);
            }
        };
    }

    private JComboBox<String> createCursorStyleBox() {
        final CursorStyle[] styles = CursorStyle.values();
        final String names[] = new String[styles.length];

        int i = 0;
        for (final CursorStyle style : styles) {
            names[i++] = t("profiles.cursor.style." + style.name().toLowerCase(),
                    style.name().toLowerCase());
        }

        return new JComboBox<String>(names);
    }

    private JCheckBox createBlinkingCheckBox() {
        final JCheckBox checkBox = new JCheckBox(t("profiles.cursor.blink",
                "enable blinking cursor"));
        checkBox.setMnemonic(m("profiles.cursor.blink", 'b'));
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                gfxSettings.setCursorBlinks(e.getStateChange() == ItemEvent.SELECTED);
            }
        });

        return checkBox;
    }

    private JComboBox<String> createAntiAliasingBox() {
        final List<KeyValue<String, Object>> hints = FontUtils.getRenderingHintMap();
        final String names[] = new String[hints.size()];

        int i = 0;
        for (final KeyValue<String, Object> hint : hints) {
            names[i++] = t("profiles.color.antialiasing." + hint.key, hint.key);
        }

        return new JComboBox<String>(names);
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
        gfxSettings.setCursorStyle(CursorStyle.values()[cursorStyleBox.getSelectedIndex()]);
    }

}
