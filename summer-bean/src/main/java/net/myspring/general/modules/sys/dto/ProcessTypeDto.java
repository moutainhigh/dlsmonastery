package net.myspring.general.modules.sys.dto;

import jdk.internal.org.objectweb.asm.commons.SerialVersionUIDAdder;

import java.io.Serializable;

/**
 * Created by wangzm on 2017/4/25.
 */
public class ProcessTypeDto {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
