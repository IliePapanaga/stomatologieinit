package com.cl.mdd.server.core.data.persistent.model.settings;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.StringTokenizer;

@Embeddable
public class SystemSettingKey implements Serializable {

    private static final String SEPARATOR = ".";

    @Column(name = "area", nullable = false, updatable = false)
    private String area;

    @Column(name = "\"group\"", nullable = false, updatable = false)
    private String group;

    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    public SystemSettingKey() {
    }

    public SystemSettingKey(String area, String group, String name) {
        this.area = area;
        this.group = group;
        this.name = name;
    }

    public SystemSettingKey(String setting) {
        if (setting == null || setting.isEmpty()) {
            throw new IllegalArgumentException("Cannot build system setting key from empty string");
        }

        StringTokenizer tokenizer = new StringTokenizer(setting, SEPARATOR);

        if (tokenizer.countTokens() != 3) {
            throw new IllegalArgumentException("Cannot build system setting key from string that does not contain exactly 3 \"" + SEPARATOR + "\" separated words");
        }

        area = tokenizer.nextToken();
        group = tokenizer.nextToken();
        name = tokenizer.nextToken();
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return area + SEPARATOR + group + SEPARATOR + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SystemSettingKey that = (SystemSettingKey) o;
        return Objects.equals(area, that.area) &&
                Objects.equals(group, that.group) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(area, group, name);
    }
}
