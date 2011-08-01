package de.jowisoftware.sshclient.ui;

import static de.jowisoftware.sshclient.i18n.Translation.m;
import static de.jowisoftware.sshclient.i18n.Translation.t;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import de.jowisoftware.sshclient.terminal.DisplayType;

public class SessionMenu extends JMenu {
    private static final long serialVersionUID = -6737261270811373874L;
    private final SSHConsole console;
    private JRadioButtonMenuItem dynamicSizeItem;
    private JRadioButtonMenuItem fixedSize80Item;
    private JRadioButtonMenuItem fixedSize132Item;

    public SessionMenu(final SSHConsole console) {
        super(t("mainwindow.menu.session", "Session"));
        setMnemonic(m("mainwindow.menu.session", 's'));

        this.console = console;
        add(createSizeMenu());
    }

    private JMenu createSizeMenu() {
        final JMenu sizeMenu = new JMenu(
                t("mainwindow.menu.sessionsize", "Size"));
        sizeMenu.setMnemonic(m("mainwindow.menu.sessionsize", 's'));
        final ActionListener sizeListener = createSizeListener();

        final ButtonGroup buttonGroup = new ButtonGroup();
        dynamicSizeItem = new JRadioButtonMenuItem(
                t("mainwindow.menu.sessionsize.dynamic", "dynamic"));
        dynamicSizeItem.setMnemonic(m("mainwindow.menu.sessionsize.dynamic", 'd'));
        dynamicSizeItem.addActionListener(sizeListener);
        dynamicSizeItem.setActionCommand(DisplayType.DYNAMIC.toString());
        buttonGroup.add(dynamicSizeItem);
        sizeMenu.add(dynamicSizeItem);

        fixedSize80Item = new JRadioButtonMenuItem(
                t("mainwindow.menu.sessionsize.fixed80", "80 x 24"));
        fixedSize80Item.setMnemonic(m("mainwindow.menu.sessionsize.dynamic", '8'));
        fixedSize80Item.addActionListener(sizeListener);
        fixedSize80Item.setActionCommand(DisplayType.FIXED80X24.toString());
        buttonGroup.add(fixedSize80Item);
        sizeMenu.add(fixedSize80Item);

        fixedSize132Item = new JRadioButtonMenuItem(
                t("mainwindow.menu.sessionsize.fixed132", "132 x 24"));
        fixedSize132Item.setMnemonic(m("mainwindow.menu.sessionsize.dynamic", '1'));
        fixedSize132Item.addActionListener(sizeListener);
        fixedSize132Item.setActionCommand(DisplayType.FIXED132X24.toString());
        buttonGroup.add(fixedSize132Item);
        sizeMenu.add(fixedSize132Item);

        return sizeMenu;
    }

    private ActionListener createSizeListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.out.println(DisplayType.valueOf(e.getActionCommand()));

                System.out.println(dynamicSizeItem.getModel().isSelected() + " "
                        + fixedSize80Item.isSelected() + " "
                        + fixedSize132Item.isSelected());
                if (dynamicSizeItem.isSelected()) {
                    console.setDisplayType(DisplayType.DYNAMIC);
                } else if (fixedSize80Item.isSelected()) {
                    console.setDisplayType(DisplayType.FIXED80X24);
                } else if (fixedSize132Item.isSelected()) {
                    console.setDisplayType(DisplayType.FIXED132X24);
                }
            }
        };
    }

    public void updateMenuStates() {
        switch(console.getDisplayType()) {
        case DYNAMIC:
            dynamicSizeItem.setSelected(true);
            break;
        case FIXED132X24:
            fixedSize132Item.setSelected(true);
            break;
        case FIXED80X24:
            fixedSize80Item.setSelected(true);
            break;
        }
    }
}
