import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
        try
        {
            Path savePath = Path.of(path);
            return Files.exists(savePath);
        }
        catch(InvalidPathException e)
        {
            // A path with characters Windows does not allow can never exist
            return false;
        }
    }
}
