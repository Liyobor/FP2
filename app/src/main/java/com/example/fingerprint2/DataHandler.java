package com.example.fingerprint2;

import java.util.ArrayList;

public class DataHandler {
    ArrayList<String> info = new ArrayList<>();
    int caseOID;
    int account;

    void resetData(){
        info.clear();
        caseOID = -1;
        account = -1;
    }
}
