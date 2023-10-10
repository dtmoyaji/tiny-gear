package org.tiny.gear.panels.crud.ColumnView;

import java.io.Serializable;

/**
 *
 * @author dtmoyaji
 */
public class KeyValueContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private int key;
    private String value;

    public KeyValueContainer(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
