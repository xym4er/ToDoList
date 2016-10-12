package com.chornyiya.todolist.data.source.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.chornyiya.todolist.data.Task;
import com.chornyiya.todolist.data.source.TasksDataSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by y.chornyi on 30.09.2016.
 */

public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private static Map<String, Task> cacheTasks = new HashMap<>();

    private static DatabaseReference mFirebaseDatabaseReference;

    public static TasksRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private TasksRemoteDataSource() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("todo");
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cacheTasks.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Task post = postSnapshot.getValue(Task.class);
                    post.setKey(postSnapshot.getKey());
                    cacheTasks.put(post.getId(),post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        Map<String, Task> updateTask = new HashMap<String, Task>();
        updateTask.put(newTask.getId(),newTask);
        mFirebaseDatabaseReference.setValue(updateTask);
    }

    @Override
    public void getTasks(final @NonNull LoadTasksCallback callback) {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TAG", "Value is: " + dataSnapshot.getChildrenCount());
                List<Task> tasks = new ArrayList<Task>();
                cacheTasks.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Task post = postSnapshot.getValue(Task.class);
                    post.setKey(postSnapshot.getKey());
                    tasks.add(post);
                    cacheTasks.put(post.getId(),post);
                    Log.d("TAG", "Value is: "+post.getId()+" -- " + post.getDescription() + " - " + post.getTitle());
                }
                callback.onTasksLoaded(tasks);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                callback.onTaskLoaded(cacheTasks.get(taskId));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        String key = mFirebaseDatabaseReference.push().getKey();
        task.setKey(key);
        Log.d(TAG, "saveTask: "+key);
        mFirebaseDatabaseReference.child(key).setValue(task);

//        Map<String, Object> updateTask = new HashMap<String, Object>();
//        updateTask.put(task.getId(),task);
//        mFirebaseDatabaseReference.updateChildren(updateTask);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Log.d("TAG", "completeTask: "+task.getId());
        mFirebaseDatabaseReference.child(task.getKey()).child("completed").setValue(true);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        completeTask(cacheTasks.get(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        mFirebaseDatabaseReference.child(task.getKey()).child("completed").setValue(false);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        activateTask(cacheTasks.get(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> entries = cacheTasks.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Task> entry = entries.next();
            if(entry.getValue().isCompleted()){
                mFirebaseDatabaseReference.child(entry.getValue().getKey()).removeValue();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("TAG", "Value is: " + dataSnapshot.getChildrenCount());
                cacheTasks.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Task post = postSnapshot.getValue(Task.class);
                    cacheTasks.put(post.getId(),post);
                    Log.d("TAG", "Value is: " + post.getDescription() + " - " + post.getTitle());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void deleteAllTasks() {
        cacheTasks.clear();
        mFirebaseDatabaseReference.removeValue();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        Iterator<Map.Entry<String, Task>> entries = cacheTasks.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Task> entry = entries.next();
            if(entry.getValue().getId().equals(taskId)){
                mFirebaseDatabaseReference.child(entry.getKey()).removeValue();
                return;
            }
        }

    }
}
