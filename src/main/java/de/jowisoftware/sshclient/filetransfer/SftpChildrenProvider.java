package de.jowisoftware.sshclient.filetransfer;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SftpChildrenProvider implements ChildrenProvider<SftpTreeNodeItem> {
    private static final Logger LOGGER = Logger.getLogger(SftpChildrenProvider.class);
    private final ChannelSftp channel;

    public SftpChildrenProvider(final ChannelSftp channel) {
        this.channel = channel;
    }

    @Override
    public SftpTreeNodeItem[] getChildrenOf(final SftpTreeNodeItem node) {
        final List<SftpTreeNodeItem> result = new LinkedList<>();
        try {
            channel.ls(node.getPath(), new ChannelSftp.LsEntrySelector() {
                @Override
                public int select(final ChannelSftp.LsEntry entry) {
                    final String filename = entry.getFilename();
                    if (entry.getAttrs().isDir() && !filename.equals(".") && !filename.equals("..")) {
                        result.add(new SftpTreeNodeItem(filename, node.getPath() + filename + '/'));
                    }
                    LOGGER.trace(filename);
                    return CONTINUE;
                }
            });
        } catch (final SftpException e) {
            LOGGER.info(String.format("Could not read children of %s (%s): %s", node.getPath(), node, e.toString()));
        }

        Collections.sort(result);
        return result.toArray(new SftpTreeNodeItem[result.size()]);
    }

    @Override
    public SftpTreeNodeItem[] getRoots() {
        return new SftpTreeNodeItem[] {
            new SftpTreeNodeItem("/", "/")
        };
    }

    @Override
    public FileInfo[] getFiles(final SftpTreeNodeItem node) {
        final List<FileInfo> result = new LinkedList<>();
        try {
            channel.ls(node.getPath(), new ChannelSftp.LsEntrySelector() {
                @Override
                public int select(final ChannelSftp.LsEntry entry) {
                    final String filename = entry.getFilename();
                    if (!entry.getAttrs().isDir()) {
                        result.add(createFileInfo(entry));
                    }
                    return CONTINUE;
                }
            });
        } catch (final SftpException e) {
            LOGGER.info(String.format("Could not read children of %s (%s): %s", node.getPath(), node, e.toString()));
        }
        return result.toArray(new FileInfo[result.size()]);
    }

    private FileInfo createFileInfo(final ChannelSftp.LsEntry entry) {
        final String name = entry.getFilename();
        final long size = entry.getAttrs().getSize();
        final String permissions = entry.getAttrs().getPermissionsString();
        final Date modifiedDate = new Date(entry.getAttrs().getMTime() * 1000);
        final String owner = Integer.toString(entry.getAttrs().getUId());
        final String group = Integer.toString(entry.getAttrs().getGId());

        return new FileInfo(name, size, owner, group, permissions, modifiedDate);
    }
}
