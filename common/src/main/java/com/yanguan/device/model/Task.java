package com.yanguan.device.model;

import java.io.Serializable;

/**
 * Created by panrui on 2016/4/12.
 */
public interface Task {
   public static class TaskType implements Serializable{
        private String name;
        private Serializable data;


        public TaskType(String name) {
            this.name=name;
        }
        public Serializable getData() {
            return data;
        }

       public String getName() {
           return name;
       }

       public TaskType setData(Serializable data) {
            this.data = data;
            return this;
        }
    }
    public void process(TaskType taskType);
}
