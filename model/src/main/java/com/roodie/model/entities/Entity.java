package com.roodie.model.entities;

import java.io.Serializable;

/**
 * Created by Roodie on 11.03.2016.
 */
public class Entity implements Serializable {
    private static final long serialVersionUID = 907671509045298947L;
    private String id;
    private String parentId;

    public Entity(String parentId) {
        this.id = parentId;
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}

