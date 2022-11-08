package com.example.fingerprint2;

public class DataItem {
    private String caseNum;
    private String patient;
    private String birthday;

    private boolean canDecrypt;

    public DataItem() {

    }

    public DataItem(String caseNum, String patient, String birthday,boolean canDecrypt) {
        this.caseNum = caseNum;
        this.patient = patient;
        this.birthday = birthday;
        this.canDecrypt = canDecrypt;
    }

    public String getCaseNum() {
        return caseNum;
    }

    public boolean canDecrypt(){
        return canDecrypt;
    }

    public void setCaseNum(String name) {
        this.caseNum = name;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}
