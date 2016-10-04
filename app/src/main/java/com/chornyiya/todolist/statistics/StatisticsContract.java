package com.chornyiya.todolist.statistics;

import com.chornyiya.todolist.BasePresenter;
import com.chornyiya.todolist.BaseView;

/**
 * Created by y.chornyi on 04.10.2016.
 */

public interface StatisticsContract {

    interface View extends BaseView<Presenter> {

        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}
