package com.dave.android.router_compile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rendawei
 * @date 2018/8/6
 */
public class PageServiceMeta implements Serializable {

    public String clazzName;
    public String name;
    public String path;
    public String desc;
    public String version;
    public String type;

    public List<PageFieldMeta> params = new ArrayList<>();

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PageFieldMeta> getParams() {
        return params;
    }

    public void setParams(List<PageFieldMeta> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "PageServiceMeta{" +
                "clazzName='" + clazzName + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", desc='" + desc + '\'' +
                ", version='" + version + '\'' +
                ", type='" + type + '\'' +
                ", params=" + params +
                '}';
    }
}
