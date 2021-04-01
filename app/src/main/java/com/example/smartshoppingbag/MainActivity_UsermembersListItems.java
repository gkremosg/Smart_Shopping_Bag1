package com.example.smartshoppingbag;

public class MainActivity_UsermembersListItems {

        public String email; //email
        private boolean isSelected = false;

        public MainActivity_UsermembersListItems(String email)
        {
            this.email = email;
        }

        public String getName() {
            return email;
        }

        public void setSelected(boolean selected) {
        isSelected = selected;
        }

        public boolean isSelected() {
        return isSelected;
        }

}
