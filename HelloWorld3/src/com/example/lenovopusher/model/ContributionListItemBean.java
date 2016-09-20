package com.example.lenovopusher.model;

/**
 * Created by ChenBy on 2016/8/31.
 */

public class ContributionListItemBean {
    private int ranking;
    private String userName;
    private String contributeSum;

    public ContributionListItemBean(int ranking, String userName, String contributeSum) {
        this.ranking = ranking;
        this.userName = userName;
        this.contributeSum = contributeSum;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContributeSum() {
        return contributeSum;
    }

    public void setContributeSum(String contributeSum) {
        this.contributeSum = contributeSum;
    }
}

