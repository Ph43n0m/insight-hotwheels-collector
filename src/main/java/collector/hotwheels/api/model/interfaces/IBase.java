package collector.hotwheels.api.model.interfaces;

public interface IBase {

    String getId();

    void setId(String value);

    String getName();

    void setName(String value);

    public String getImageUrl();

    public void setImageUrl(String value);

    public String getImageFileName();

    public void setImageFileName(String value);

    boolean isValidBaseItem();
}
