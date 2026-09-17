import java.nio.file.Path;
import java.nio.file.Files;
public class GameProfile 
{
    private String name;
    private String path;

    public GameProfile(String name, String path)
    {
        this.name = name;
        this.path = path;
    }

    public String getName()
    {
        return this.name;
    }
    public String getPath()
    {
        return this.path;
    }
    public boolean savePathExists()
    {
        Path savePath = Path.of(path);
        return Files.exists(savePath);
    }
}
