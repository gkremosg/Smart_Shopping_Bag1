package com.example.smartshoppingbag;

public class MainActivity_UsermembersListItems {

        public String memberemail; //email
        private boolean isSelected = false;

        public MainActivity_UsermembersListItems(String email){
                this.memberemail = email;
        }

        public String getName(){
                return memberemail;
        }

        public void setSelected(boolean selected) {
                isSelected = selected;
        }

        public boolean isSelected() {
                return isSelected;
        }

}
