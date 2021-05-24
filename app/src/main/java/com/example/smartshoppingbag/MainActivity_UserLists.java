package com.example.smartshoppingbag;

public class MainActivity_UserLists {

    public String listname, listdate, listID;

    private boolean isSelected = false;

    public MainActivity_UserLists(String listname, String listdate, String listID){
        this.listname = listname;
        this.listdate = listdate;
        this.listID = listID;
    }

    public String getListName() {
        return listname;
    }

    public String getListDate() {
        return listdate;
    }

    public String getListID() {
        return listID;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}

