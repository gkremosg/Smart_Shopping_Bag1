package com.example.smartshoppingbag;

public class ListItemsActivity_RecyclerView {

    public String editTextListItem;
    public String itemCategory;
    public Integer cost_before_comma;
    public Integer cost_after_comma;

    private boolean isSelected = false;

    public ListItemsActivity_RecyclerView(String editTextListItem, String itemCategory, Integer cost_before_comma, Integer cost_after_comma){
        this.editTextListItem = editTextListItem;
        this.itemCategory = itemCategory;
        this.cost_before_comma = cost_before_comma;
        this.cost_after_comma = cost_after_comma;
    }

    public String getEditTextListItem() {
        return editTextListItem;
    }

    public String getItemCategory() {
        return itemCategory;
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
