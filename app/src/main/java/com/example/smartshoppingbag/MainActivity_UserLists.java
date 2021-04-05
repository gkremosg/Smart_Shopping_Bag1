package com.example.smartshoppingbag;

public class MainActivity_UserLists {

    public String listname, listdate;

    private boolean isSelected = false;

    public MainActivity_UserLists(String listname, String listdate)
    {
        this.listname = listname;
        this.listdate = listdate;
    }

    public String getListName() {
        return listname;
    }

    public String getListDate() {
        return listdate;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}

