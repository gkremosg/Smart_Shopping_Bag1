package com.example.smartshoppingbag;

public class ListItemsActivity_RecyclerView {

    public String userlistitemID;
    public String listID;
    public String itemname;
    public String itemcategory;
    public String costbeforecomma;
    public String costaftercomma;

    private boolean isSelected = false;

    public ListItemsActivity_RecyclerView(String itemname, String userlistitemID, String listID, String itemcategory, String costbeforecomma, String costaftercomma){
        this.itemname = itemname;
        this.userlistitemID = userlistitemID;
        this.listID = listID;
        this.itemcategory = itemcategory;
        this.costbeforecomma = costbeforecomma;
        this.costaftercomma = costaftercomma;
    }
    public String getTextListItemName() {
        return itemname;
    }

    public String getTextUserListItemID() {
        return userlistitemID;
    }

    public String getTextListItemID() {
        return listID;
    }

    public String getTextItemCategory() {
        return itemcategory;
    }

    public String getTextCostBeforeComma() {
        return costbeforecomma;
    }

    public String getTextCostAfterComma() {
        return costaftercomma;
    }


    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

}
