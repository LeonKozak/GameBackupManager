# Game Backup Manager

A Java console application for creating, managing, and restoring timestamped game save backups.

## Features

- Add and save game profiles
- Create timestamped backups of a game's whole save folder, including subfolders
- View available backups, sorted from oldest to newest
- Restore a chosen backup to the game's original save location
- Persist game profiles between program runs
- Input validation for the console menu and backup names

## Technologies

- Java
- Java NIO (`Path`, `Files`, `Files.walk`)
- File I/O
- Object-Oriented Programming

## Running the Project

Requires Java 11 or later.

```bash
git clone https://github.com/LeonKozak/GameBackupManager.git
cd GameBackupManager
javac *.java
java Main
```

Backups are stored in a `backups/` folder and profiles in `profiles.txt`, both created next to where you run the program.

## Example

```text
=== Game Save Backup Manager ===
1. Add game profile
2. View profiles
3. Create backup
4. View backups
5. Restore backup
6. Exit
3
Which game would you like to backup?:
MyGame
Backup created successfully:
backups/MyGame/2026-10-21_15-19-50
```

To restore, choose **View backups** to see the folder names, then **Restore backup** and enter one of them.

## How It Works

| Class | Responsibility |
| --- | --- |
| `Main` | Console menu and user input |
| `GameProfile` | A game's name and save folder path |
| `GameSaveManager` | Creates, lists and restores backups; saves and loads profiles |

Backup and restore both use a recursive copy (`Files.walk`) so nested save folders keep their structure. If two backups are made within the same second, the second is given a numbered suffix instead of failing.
