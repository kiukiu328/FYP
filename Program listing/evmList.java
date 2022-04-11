package com.fyp.evhelper.reminder;

// evmDate     --> electric vehicle maintenance Date
// evmService  --> electric vehicle maintenance Service

public class evmList {
    String evmDate;
    String evmService;

    public evmList() {
    }

    public evmList(String evmDate, String evmService) {
        this.evmDate = evmDate;
        this.evmService = evmService;
    }

    public String getEvmDate() {
        return evmDate;
    }

    public void setEvmDate(String evmDate) {
        this.evmDate = evmDate;
    }

    public String getEvmService() {
        return evmService;
    }

    public void setEvmService(String evmService) {
        this.evmService = evmService;
    }
}
