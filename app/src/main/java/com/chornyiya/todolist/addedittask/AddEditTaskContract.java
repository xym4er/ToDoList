package com.chornyiya.todolist.addedittask;

import com.chornyiya.todolist.BasePresenter;
import com.chornyiya.todolist.BaseView;

/**
 * Created by y.chornyi on 04.10.2016.
 */

public interface AddEditTaskContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTaskError();

        void showTasksList();

        void setTitle(String title);

        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void saveTask(String title, String description);

        void populateTask();
    }
}
