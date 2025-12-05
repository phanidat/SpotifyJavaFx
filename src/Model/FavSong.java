package Model;

public class FavSong {
    private String id;
    private String songName;
    private String artistName;
    private String favDate;
    private String comments;

    public FavSong(String id, String songName, String artistName, String date, String comments) {
        this.id = id;
        this.songName = songName;
        this.artistName = artistName;
        this.favDate = date;
        this.comments = comments;
    }
    public String getId() {
        return id;
    }
    public String getSongName() {
        return songName;
    }
    public String getArtistName() {
        return artistName;
    }
    public String getFavDate() {
        return favDate;
    }
    public String getComments() { return comments;}
    public void setComments(String comments) { this.comments = comments;}
}
