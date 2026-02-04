package com.example.musicapi.dto;

public class RegionalSyncResult {

    private int inserted;
    private int inactivated;
    private int recreated;
    private int totalExternal;

    public RegionalSyncResult() {}

    public RegionalSyncResult(int inserted, int inactivated, int recreated, int totalExternal) {
        this.inserted = inserted;
        this.inactivated = inactivated;
        this.recreated = recreated;
        this.totalExternal = totalExternal;
    }

    public int getInserted() {
        return inserted;
    }

    public int getInactivated() {
        return inactivated;
    }

    public int getRecreated() {
        return recreated;
    }

    public int getTotalExternal() {
        return totalExternal;
    }

    public void setInserted(int inserted) {
        this.inserted = inserted;
    }

    public void setInactivated(int inactivated) {
        this.inactivated = inactivated;
    }

    public void setRecreated(int recreated) {
        this.recreated = recreated;
    }

    public void setTotalExternal(int totalExternal) {
        this.totalExternal = totalExternal;
    }
}
