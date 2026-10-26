import java.util.Scanner;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
/**
 * Runs the game save backup manager and handles the menu input
 */
public class Main 
{
    public static void main(String[] args) 
    {
        Scanner sc = new Scanner(System.in);
        int input = 0;
        String name = "";
        String path = "";
        String gameName = "";

        GameSaveManager mngr = new GameSaveManager();
        mngr.loadProfiles();

        while(input != 6)
        {
            showMenu();
            // Prevent non numeric menu input from crashing the application
            if(sc.hasNextInt())
            {
                input = sc.nextInt();
                sc.nextLine();
            }
            else
            {
                System.out.println("Please enter a number between 1 and 6");
                sc.nextLine();
                continue;
            }
            switch(input)
            {
                case 1:
                    System.out.println("Enter game name: ");
                    name = sc.nextLine().trim();
                    GameProfile existingProfile = mngr.findProfile(name);

                    if(!isValidName(name))
                    {
                        System.out.println("Game name cannot be empty, end with a dot, or contain \\ / : * ? \" < > |");
                    }
                    else if(existingProfile != null)
                    {
                        System.out.println("Profile already exists");
                    }
                    else
                    {
                        System.out.println("Copy game path here: ");
                        path = cleanPath(sc.nextLine());

                        if(!isValidPath(path))
                        {
                            System.out.println("That is not a valid folder path");
                        }
                        else
                        {
                            GameProfile profile = new GameProfile(name, path);
                            mngr.addProfile(profile);
                            mngr.saveProfiles();
                        }
                    }
                    break;
                case 2:
                    mngr.displayProfiles();
                    break;
                case 3:
                    System.out.println("Which game would you like to backup?: ");
                    gameName = sc.nextLine().trim();
                    GameProfile selectedProfile = mngr.findProfile(gameName);

                    if(selectedProfile != null)
                    {
                        mngr.createBackup(selectedProfile);
                    }
                    else
                    {
                        System.out.println("Game profile not found");
                    }
                    break;
                case 4:
                    System.out.println("Enter game name: ");
                    gameName = sc.nextLine().trim();
                    GameProfile selectedProfile2 = mngr.findProfile(gameName);

                    if(selectedProfile2 != null)
                    {
                        mngr.displayBackups(selectedProfile2);
                    }
                    else
                    {
                        System.out.println("Game profile not found");
                    }
                    break;  
                case 5:
                    System.out.println("Enter game name: ");
                    gameName = sc.nextLine().trim();
                    System.out.println("Enter backup folder: ");
                    String backupFolder = sc.nextLine();
                    GameProfile selectedProfile3 = mngr.findProfile(gameName);

                    if(selectedProfile3 != null)
                    {
                        mngr.restoreBackup(selectedProfile3, backupFolder);
                    }
                    else
                    {
                        System.out.println("Game profile not found");
                    }
                    break;
                case 6:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
        sc.close();
    }
    public static void showMenu()
    {
        System.out.println("=== Game Save Backup Manager ===");
        System.out.println(1 + ". Add game profile");
        System.out.println(2 + ". View profiles");
        System.out.println(3 + ". Create backup");
        System.out.println(4 + ". View backups");
        System.out.println(5 + ". Restore backup");
        System.out.println(6 + ". Exit");
    }
    /**
     * Checks a game name can be used as a Windows folder name,
     * since it is used for the backup folder.
     */
    public static boolean isValidName(String name)
    {
        // Windows drops trailing dots from folder names, which also rules out "." and ".."
        if(name.isEmpty() || name.endsWith("."))
        {
            return false;
        }
        String illegalChars = "\\/:*?\"<>|";
        for(int i = 0; i < illegalChars.length(); i++)
        {
            if(name.indexOf(illegalChars.charAt(i)) >= 0)
            {
                return false;
            }
        }
        return true;
    }
    /**
     * Removes extra spaces and the quotes added by Explorer's "Copy as path".
     */
    public static String cleanPath(String path)
    {
        path = path.trim();
        if(path.length() >= 2 && path.startsWith("\"") && path.endsWith("\""))
        {
            path = path.substring(1, path.length() - 1).trim();
        }
        return path;
    }
    public static boolean isValidPath(String path)
    {
        if(path.isEmpty())
        {
            return false;
        }
        try
        {
            Path.of(path);
            return true;
        }
        catch(InvalidPathException e)
        {
            return false;
        }
    }
}
