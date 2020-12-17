package qcdownloader.data;

public class Scene {
    public String ID;
    public String Title;
    public String SafeTitle () { //for scene folders
        return Title.replaceAll("\\W+","");
    }
    public String Description;
    public String Description2;
    public String PublishDate;
    public String Special;
    public String Folder; //seems to be a uuid
    public String PreviewImage; //usually a single item from Photos[]
    public boolean VideosImage;
    public boolean VideosGif;
    public String Photos;
    public String[] GetPhotos () {
        if (Photos == null || Photos.equals("false")) return new String[]{};
        return Photos.split(";");
    }
    public String Videos;
    public String[] GetVideos () {
        if (Videos == null || Videos.equals("false")) return new String[]{};
        return Videos.split(";");
    }
    public String Models;
    public boolean VideoHint;
}
