package com.example.fingerprint2;

import java.util.ArrayList;

public class DataHandler {
    ArrayList<String> info = new ArrayList<>();
    int caseOID;
    String account;

    ArrayList<Integer> caseOIDList = new ArrayList<>();
    ArrayList<String> accountList = new ArrayList<>();


//    ArrayList<String> displayList = new ArrayList<>();

    ArrayList<DataItem> dataSelectDisplayList = new ArrayList<>();



    void resetData(){
        info.clear();
        caseOID = -1;
        account = "-1";
    }
}


