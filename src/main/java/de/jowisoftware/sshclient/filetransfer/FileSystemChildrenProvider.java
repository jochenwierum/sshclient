package de.jowisoftware.sshclient.filetransfer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class FileSystemChildrenProvider implements ChildrenProvider<FileSystemTreeNodeItem> {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

    @Override
    public FileSystemTreeNodeItem[] getChildrenOf(final FileSystemTreeNodeItem parent) {
        final List<FileSystemTreeNodeItem> children = new ArrayList<>();
        for (final File file: getFiles(parent.getFile())) {
            if (file.isDirectory()) {
                children.add(new FileSystemTreeNodeItem(file));
            }
        }

        Collections.sort(children);
        return children.toArray(new FileSystemTreeNodeItem[children.size()]);
    }

    private File[] getFiles(final File baseDir) {
        final File[] files = baseDir.listFiles();
        if (files == null) {
            return new File[0];
        }
        return files;
    }


    @Override
    public FileSystemTreeNodeItem[] getRoots() {
        final List<FileSystemTreeNodeItem> roots = new ArrayList<>();

        for (final File rootFile : File.listRoots()) {
            roots.add(new FileSystemTreeNodeItem(rootFile));
        }

        return roots.toArray(new FileSystemTreeNodeItem[roots.size()]);
    }

    @Override
    public FileInfo[] getFiles(final FileSystemTreeNodeItem node) {
        final List<FileInfo> files = new ArrayList<>();
        for (final File file : getFiles(node.getFile())) {
            if (file.isFile()) {
                final Path path = Paths.get(file.getAbsolutePath());
                final String owner = getOwner(path);
                final String group = getGroup(path);
                final String permissions = getPermissions(path);
                final long size = getSize(path);
                final Date lastModifiedDate = getLastModifiedDate(path);

                files.add(new FileInfo(path.getFileName().toString(), size,
                        owner, group, permissions,
                        lastModifiedDate));
            }
        }

        Collections.sort(files);
        return files.toArray(new FileInfo[files.size()]);
    }

    private String getGroup(final Path path) {
        if (isWindows) {
            return "";
        }

        try {
            return ((GroupPrincipal) Files.getAttribute(path, "group")).getName();
        } catch (final Exception e) {
            return "";
        }
    }

    private Date getLastModifiedDate(final Path path) {
        try {
            return new Date(Files.getLastModifiedTime(path).toMillis());
        } catch(final Exception e) {
            return new Date();
        }
    }

    private long getSize(final Path path) {
        try {
            return Files.size(path);
        } catch (final Exception e) {
            return 0;
        }
    }

    private String getPermissions(final Path path) {
        final StringBuilder permissions = new StringBuilder();
        if (Files.isRegularFile(path)) {
            permissions.append('f');
        } else if (Files.isSymbolicLink(path)) {
            permissions.append('l');
        } else {
            permissions.append('?');
        }

        if (isWindows) {
            addPermission(permissions, Files.isReadable(path), 'r');
            addPermission(permissions, Files.isWritable(path), 'w');
        } else {
            try {
                final Set<PosixFilePermission> permissionSet = Files.getPosixFilePermissions(path);

                addPermission(permissions, permissionSet, PosixFilePermission.OWNER_READ, 'r');
                addPermission(permissions, permissionSet, PosixFilePermission.OWNER_WRITE, 'w');
                addPermission(permissions, permissionSet, PosixFilePermission.OWNER_EXECUTE, 'x');
                addPermission(permissions, permissionSet, PosixFilePermission.GROUP_READ, 'r');
                addPermission(permissions, permissionSet, PosixFilePermission.GROUP_WRITE, 'w');
                addPermission(permissions, permissionSet, PosixFilePermission.GROUP_EXECUTE, 'x');
                addPermission(permissions, permissionSet, PosixFilePermission.OTHERS_READ, 'r');
                addPermission(permissions, permissionSet, PosixFilePermission.OTHERS_WRITE, 'w');
                addPermission(permissions, permissionSet, PosixFilePermission.OTHERS_EXECUTE, 'x');
            } catch (final Exception e) {
                return "-----------";
            }
        }
        return permissions.toString();
    }

    private String getOwner(final Path path) {
        try {
            return Files.getOwner(path).getName();
        } catch(final Exception e) {
            return "";
        }
    }

    private void addPermission(final StringBuilder permissions, final Set<PosixFilePermission> permissionSet, final PosixFilePermission permission, final char character) {
        addPermission(permissions, permissionSet.contains(permission), character);
    }

    private void addPermission(final StringBuilder permissions, final boolean isGiven, final char character) {
        if (isGiven) {
            permissions.append(character);
        } else {
            permissions.append('-');
        }
    }
}
