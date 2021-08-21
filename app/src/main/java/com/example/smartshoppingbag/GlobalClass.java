package com.example.smartshoppingbag;

import android.app.Application;

public class GlobalClass extends Application {
    public String globaluseremail;
    public String globallistID;

    public String getGlobaluseremail(){
        return globaluseremail;
    }
    public String getGloballistID(){
        return globallistID;
    }
    public void setGlobaluseremail(String globaluseremail) {
        this.globaluseremail = globaluseremail;
    }
    public void setGloballistID(String globallistID) {
        this.globallistID = globallistID;
    }
}
