package com.chornyiya.todolist.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.UUID;

/**
 * Created by y.chornyi on 27.09.2016.
 */

public final class Task {


    private String id;

    private String title;

    private String description;

    private String key;
    private boolean completed;

    public Task(String title, String description) {
        this(title, description, UUID.randomUUID().toString(), false);
    }

    public Task(String title,  String description, boolean completed) {
        this(title, description, UUID.randomUUID().toString(), completed);
    }

    public Task(String title, String description,String id) {
        this(title, description, id, false);
    }

    public Task(String title, String description, String id, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    public Task(){}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    @JsonIgnore
    public String titleForList() {
        if (!Strings.isNullOrEmpty(title)) {
            return title;
        } else {
            return description;
        }
    }


    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    @JsonIgnore
    public boolean active() {
        return !completed;
    }
    @JsonIgnore
    public boolean empty() {
        return Strings.isNullOrEmpty(title) &&
                Strings.isNullOrEmpty(description);
    }
    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equal(id, task.id) &&
                Objects.equal(title, task.title) &&
                Objects.equal(description, task.description);
    }
    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hashCode(id, title, description);
    }
    @JsonIgnore
    @Override
    public String toString() {
        return "Task with title: " + title;
    }
}
