package in.hypernation.quizo.Models;

public class Topic {
    private String icon;
    private int id;
    private String title;
    private String description;
    private int popularity;

    public Topic(String icon, int id, String title, int popularity) {
        this.icon = icon;
        this.id = id;
        this.title = title;
        this.popularity = popularity;
    }

    public Topic(int id, String icon, String title, int popularity, String description) {
        this.icon = icon;
        this.id = id;
        this.title = title;
        this.popularity = popularity;
        this.description = description;
    }

    public Topic(){}

    public String getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

}
