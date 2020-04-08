package com.moon.core.io;

import java.io.Serializable;

/**
 * @author benshaoye
 */
public class SerializeObject implements Serializable {

    private final static long serialVersionUID = 1L;

    public Object data;

    public SerializeObject() { }

    public SerializeObject(Object data) { this.data = data; }

    public Object getData() { return data; }

    public void setData(Object data) { this.data = data; }
}
