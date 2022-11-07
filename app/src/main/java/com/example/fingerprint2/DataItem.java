package com.example.fingerprint2;

public class DataItem {
    private String caseNum;
    private String patient;
    private String birthday;

    public DataItem() {

    }

    public DataItem(String caseNum, String patient, String birthday) {
        this.caseNum = caseNum;
        this.patient = patient;
        this.birthday = birthday;
    }

    public String getCaseNum() {
        return caseNum;
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
