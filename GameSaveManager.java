import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
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
     * Returns the new backup folder, or null if the backup failed.
     */
    public Path createBackup(GameProfile profile)
    {
        if(!profile.savePathExists())
        {
            System.out.println("Save folder does not exist");
            return null;
        }
        Path backupPath = null;
        boolean created = false;
        try
        {
            // Create a unique timestamp so each backup has its own folder
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = now.format(formatter);
            backupPath = Path.of("backups", profile.getName(), timestamp);

            // If a backup was already made this second, add a number so it is not overwritten
            int count = 2;
            while(Files.exists(backupPath))
            {
                backupPath = Path.of("backups", profile.getName(), timestamp + "_" + count);
                count++;
            }
            Files.createDirectories(backupPath);
            created = true;

            // Copy the whole save folder, including any subfolders
            copyFolder(Path.of(profile.getPath()), backupPath);
            System.out.println("Backup created successfully: ");
            System.out.println(backupPath);
            return backupPath;
        }
        catch(IOException | InvalidPathException e)
        {
            System.out.println("Could not create backup: " + e.getMessage());

            // Remove the half-finished backup so it is not mistaken for a good one
            if(created)
            {
                try
                {
                    deleteContents(backupPath);
                    Files.delete(backupPath);
                }
                catch(IOException cleanupError)
                {
                    System.out.println("Could not remove incomplete backup, please delete it: " + backupPath);
                }
            }
            return null;
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
        // Timestamped names sort from oldest to newest
        Arrays.sort(backupFolders);

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
     * to the games original save location. The current saves are
     * backed up first so the restore can be undone.
     */
    public void restoreBackup(GameProfile profile, String backupName)
    {
        Path safetyBackup = null;
        try
        {
            Path gameBackups = Path.of("backups", profile.getName());
            Path backupFolder = gameBackups.resolve(backupName.trim()).normalize();

            // Only accept the name of one backup folder, so a blank or invalid
            // name cannot restore every backup at once
            if(backupName.isBlank() || !gameBackups.equals(backupFolder.getParent()))
            {
                System.out.println("Please enter a backup folder name from View backups");
                return;
            }
            if(!Files.isDirectory(backupFolder))
            {
                System.out.println("Backup folder does not exist");
                return;
            }
            Path savePath = Path.of(profile.getPath());

            if(profile.savePathExists())
            {
                // Back up the current saves so nothing is lost if this was the wrong backup
                System.out.println("Backing up current saves before restoring...");
                safetyBackup = createBackup(profile);
                if(safetyBackup == null)
                {
                    System.out.println("Restore cancelled so your current saves are not lost");
                    return;
                }
                // Empty the save folder so files made after the backup do not stay behind
                deleteContents(savePath);
            }
            // Copy the whole backup back, including any subfolders
            copyFolder(backupFolder, savePath);
            System.out.println("Backup restored successfully");
        }
        catch(IOException | InvalidPathException e)
        {
            System.out.println("Could not restore backup: " + e.getMessage());
            if(safetyBackup != null)
            {
                System.out.println("Your saves from before the restore are in: " + safetyBackup);
            }
        }
    }
    /**
     * Deletes every file and subfolder inside folder, leaving
     * the folder itself in place.
     */
    private void deleteContents(Path folder) throws IOException
    {
        try(Stream<Path> paths = Files.walk(folder))
        {
            // Deepest paths come first, so each folder is empty when it is deleted
            Iterator<Path> iterator = paths.sorted(Comparator.reverseOrder()).iterator();
            while(iterator.hasNext())
            {
                Path current = iterator.next();
                if(!current.equals(folder))
                {
                    Files.delete(current);
                }
            }
        }
    }
    /**
     * Copies every file and subfolder from source into destination,
     * keeping the same folder structure. Existing files are replaced.
     */
    private void copyFolder(Path source, Path destination) throws IOException
    {
        try(Stream<Path> paths = Files.walk(source))
        {
            Iterator<Path> iterator = paths.iterator();
            while(iterator.hasNext())
            {
                Path current = iterator.next();
                Path target = destination.resolve(source.relativize(current).toString());

                if(Files.isDirectory(current))
                {
                    Files.createDirectories(target);
                }
                else
                {
                    Files.copy(current, target, StandardCopyOption.REPLACE_EXISTING);
                }
            }
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