package com.dave.android.router_compile;

import java.io.Serializable;

/**
 * @author rendawei
 * @date 2018/8/6
 */
public class PageFieldMeta implements Serializable {

    public String clazzName;
    public String name;
    public boolean required;
    public String desc;
    public String version;

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "PageFieldMeta{" +
                "clazzName='" + clazzName + '\'' +
                ", name='" + name + '\'' +
                ", required=" + required +
                ", desc='" + desc + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
