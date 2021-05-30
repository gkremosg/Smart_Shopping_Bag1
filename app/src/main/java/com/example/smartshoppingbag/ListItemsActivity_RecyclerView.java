package com.example.smartshoppingbag;

public class ListItemsActivity_RecyclerView {

    public String listItem;
    public String category;
    public Integer cost_before_comma;
    public Integer cost_after_comma;

    private boolean isSelected = false;

    public ListItemsActivity_RecyclerView(String listItem, String category, Integer cost_before_comma, Integer cost_after_comma){
        this.listItem = listItem;
        this.category = category;
        this.cost_before_comma = cost_before_comma;
        this.cost_after_comma = cost_after_comma;
    }

    public String getEditTextListItem() {
        return listItem;
    }

    public String getItemCategory() {
        return category;
    }

    public Integer getCostBeforeComma() {
        return cost_before_comma;
    }

    public Integer getCostAfterComma() {
        return cost_after_comma;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

}
