import java.util.Scanner;
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
                    name = sc.nextLine();
                    GameProfile existingProfile = mngr.findProfile(name);

                    if(existingProfile != null)
                    {
                        System.out.println("Profile already exists");
                    }
                    else
                    {
                        System.out.println("Copy game path here: ");
                        path = sc.nextLine();
                        GameProfile profile = new GameProfile(name, path);
                        mngr.addProfile(profile);
                        mngr.saveProfiles();
                    }
                    break;
                case 2:
                    mngr.displayProfiles();
                    break;
                case 3:
                    System.out.println("Which game would you like to backup?: ");
                    gameName = sc.nextLine();
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
                    gameName = sc.nextLine();
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
                    gameName = sc.nextLine();
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
}
