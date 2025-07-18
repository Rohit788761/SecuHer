package com.dreamprogramming.secuher;

import java.util.ArrayList;

public class DataHolder {
    private static DataHolder instance;
    private ArrayList<String> listData;

    private DataHolder() {
        listData = new ArrayList<>();
    }

    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    public ArrayList<String> getListData() {
        return listData;
    }

    public void setListData(ArrayList<String> listData) {
        this.listData.clear();
        this.listData.addAll(listData);
    }
}

