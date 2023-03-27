package in.hypernation.quizo.Models;

import org.json.JSONObject;

public class Pool {
    private int poolID;
    private String title;
    private int entryFee;
    private int prizePool;
    private int questions;
    private double bonus;
    private int totalPlayers;
    private int playersJoined;
    private String categoryIcon;
    private int totalWinners;
    private String category;
    private JSONObject prizeDistribution;

    public Pool(){

    }

    public Pool(int poolID, String title, int entryFee, int prizePool, int questions, double bonus, int totalPlayers, int playersJoined, String categoryIcon, int totalWinners, String category, JSONObject prizeDistribution) {
        this.poolID = poolID;
        this.title = title;
        this.entryFee = entryFee;
        this.prizePool = prizePool;
        this.questions = questions;
        this.bonus = bonus;
        this.totalPlayers = totalPlayers;
        this.playersJoined = playersJoined;
        this.categoryIcon = categoryIcon;
        this.totalWinners = totalWinners;
        this.category = category;
        this.prizeDistribution = prizeDistribution;
    }

    public int getPoolID() {
        return poolID;
    }

    public void setPoolID(int poolID) {
        this.poolID = poolID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public int getPrizePool() {
        return prizePool;
    }

    public void setPrizePool(int prizePool) {
        this.prizePool = prizePool;
    }

    public int getQuestions() {
        return questions;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getPlayersJoined() {
        return playersJoined;
    }

    public void setPlayersJoined(int playersJoined) {
        this.playersJoined = playersJoined;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public int getTotalWinners() {
        return totalWinners;
    }

    public void setTotalWinners(int totalWinners) {
        this.totalWinners = totalWinners;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public JSONObject getPrizeDistribution() {
        return prizeDistribution;
    }

    public void setPrizeDistribution(JSONObject prizeDistribution) {
        this.prizeDistribution = prizeDistribution;
    }
}
