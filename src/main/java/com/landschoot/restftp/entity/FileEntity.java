package com.landschoot.restftp.entity;

/**
 * Created by landschoot on 14/05/17.
 * Entité représentant un fichier
 */
public class FileEntity {
    private String name;
    private String path;
    private long size;
    private long lastModified;
    private String user;
    private String group;
    private String userPermission;
    private String groupPermission;
    private String otherPermission;
    private String pathParent;
    private boolean directory;

    public FileEntity(){}

    public FileEntity(String name, String path, long size, long lastModified, String pathParent, boolean isDirectory) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.lastModified = lastModified;
        this.pathParent = pathParent;
        this.directory = isDirectory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(String userPermission) {
        this.userPermission = userPermission;
    }

    public String getGroupPermission() {
        return groupPermission;
    }

    public void setGroupPermission(String groupPermission) {
        this.groupPermission = groupPermission;
    }

    public String getOtherPermission() {
        return otherPermission;
    }

    public void setOtherPermission(String otherPermission) {
        this.otherPermission = otherPermission;
    }

    public String getPathParent() {
        return pathParent;
    }

    public void setPathParent(String pathParent) {
        this.pathParent = pathParent;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}

