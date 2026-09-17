import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.util.Scanner;
/**
 * Manages game profiles and handles creating, displaying,
 * restoring, saving, and loading game save backups.
 */
public class GameSaveManager 
{
    private ArrayList<GameProfile> profiles;

    public GameSaveManager()
    {
        this.profiles = new ArrayList<GameProfile>();
    }
    public void addProfile(GameProfile profile)
    {
        profiles.add(profile);
    }
    public void displayProfiles()
    {
        for(int i = 0; i < profiles.size(); i++)
        {
            System.out.println(profiles.get(i).getName());
            System.out.println(profiles.get(i).getPath());
        }
    }
    public GameProfile findProfile(String name)
    {
        for(int i = 0; i < profiles.size(); i++)
        {
            if(name.equalsIgnoreCase(profiles.get(i).getName()))
            {
                return profiles.get(i);
            }
        }
        return null;
    }
    /**
     * Creates a timestamped backup of the selected games save files.
     */
    public void createBackup(GameProfile profile)
    {
        if(profile.savePathExists())
        {
            // Create a unique timestamp so each backup has its own folder
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = now.format(formatter);
            String backup = "backups/" + profile.getName() + "/" + timestamp;
            Path backupPath = Path.of(backup);
            try
            {
                Files.createDirectories(backupPath);
                File folder = new File(profile.getPath());
                File [] files = folder.listFiles();
                if(files == null)
                {
                    System.out.println("Could not read save folder");
                    return;
                }

                // Copy each save file into the timestamped backup folder
                for(int i = 0; i < files.length; i++)
                {
                    File sourceFile = files[i];
                    Path destination = backupPath.resolve(sourceFile.getName());
                    Files.copy(sourceFile.toPath(), destination);
                }
                System.out.println("Backup created successfully: ");
                System.out.println(backupPath);
            }
            catch(IOException e)
            {
                System.out.println("Could not create backup");
            }
        }
        else
        {
            System.out.println("Save folder does not exist");
        }
    }
    /**
     * Displays all available backup folders for a game profile.
     */
    public void displayBackups(GameProfile profile)
    {
        String backupFolder = "backups/" + profile.getName();
        File folder = new File(backupFolder);
        File [] backupFolders = folder.listFiles();
        if(backupFolders == null)
        {
            System.out.println("No backups found for this game");
            return;
        }

        for(int i = 0; i < backupFolders.length; i++)
        {
            if(backupFolders[i].isDirectory())
            {
                System.out.println(backupFolders[i].getName());
            }
        }
    }
    /**
     * Restores save files from a selected backup folder
     * to the games original save location.
     */
    public void restoreBackup(GameProfile profile, String backupName)
    {
        String backupFolderPath = "backups/" + profile.getName() + "/" + backupName;
        try
        {
            File backupFolder = new File(backupFolderPath);

            if(!backupFolder.exists())
            {
                System.out.println("Backup folder does not exist");
                return;
            }
            File [] files = backupFolder.listFiles();
            if(files == null)
            {
                System.out.println("Could not read backup folder");
                return;
            }
            Path saveFolder = Path.of(profile.getPath());

            for(int i = 0; i < files.length; i++)
            {
                File sourceFile = files[i];
                Path destination = saveFolder.resolve(sourceFile.getName());
                Files.copy(sourceFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Backup restored successfully");
        }
        catch(IOException e)
        {
            System.out.println("Could not restore backup");
        }
    }
    /**
     * Saves all game profiles to profiles.txt so they persist
     * after the application closes.
     */
    public void saveProfiles()
    {
        try
        {
            FileWriter writer = new FileWriter("profiles.txt");

            for(int i = 0; i < profiles.size(); i++)
            {
                // Store each profile as: gameName|savePath
                String fileNamePath = profiles.get(i).getName() + "|" + profiles.get(i).getPath() + "\n";
                writer.write(fileNamePath);
            }
            writer.close();
        }
        catch(IOException e)
        {
            System.out.println("Could not save profiles");
        }
    }
    /**
     * Loads saved profiles from profiles.txt and rebuilds
     * the applications GameProfile objects.
     */
    public void loadProfiles()
    {
        try
        {
            File file = new File("profiles.txt");

            if(!file.exists())
            {
                System.out.println("No saved profiles found");
                return;
            }
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine();
                // Split the saved line into game name and save path
                String [] parts = line.split("\\|");
                if(parts.length < 2)
                {
                    continue;
                }
                String name = parts[0];
                String path = parts[1];
                GameProfile loadProfile = new GameProfile(name, path);
                profiles.add(loadProfile);
            }
            sc.close();

        }
        catch(IOException e)
        {
            System.out.println("Could not load profile");
        }
    }
}