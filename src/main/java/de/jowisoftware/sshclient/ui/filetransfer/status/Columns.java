package de.jowisoftware.sshclient.ui.filetransfer.status;

enum Columns {
    ICON_COLUMN(0),
    FILE_COLUMN(1),
    STATUS_COLUMN(2),
    BUTTON_COLUMN(3);

    public final int id;
    Columns(final int id) {
        this.id = id;
    }

    public static Columns getByColumn(final int columnIndex) {
        for (final Columns column : values()) {
            if (column.id == columnIndex) {
                return column;
            }
        }
        return null;
    }
}
