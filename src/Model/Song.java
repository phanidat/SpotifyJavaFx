package Model;

public class Song {
    private final String id;
    private final String songName;
    private final String artistName;
    private boolean isFav;

    public Song (String id,String songName,String artistName,boolean isFav){
        this.id = id;
        this.songName = songName;
        this.artistName = artistName;
        this.isFav = isFav;
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
    public boolean isFav() {
        return isFav;
    }
}
