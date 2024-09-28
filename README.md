<h1>How to utilize PBPOrganizer</h1>

<h2> Main Class </h2>

**String[] fileNames**:
  - place <u>Complete</u> name of file you like to process specifically in array.
  - e.g.: "[03-05-2016]-[06-19-2016]-combined-stats.csv"

  **<h3>Removing incorrect Stat Sheet Matchups</h3>**
<p>These games will be considered when creating the matchups and are only considered if you decide on stat checking them.</p>

 - These games have correct stat lines, however the teambox.csv would tell you otherwise. to remove them from the processing when **comparing the generated stats to the stat sheet**, simply uncomment line 17.


**<h3>Handling incorrect Names</h3>**
 <p>A common issue is names not matching from the matchup csv and the PBP data.</p>

**Method In Charge of function**: CSVUtils.loadCorrectNamesMap()
- To stop the program from recognizing the correct name, which would not make any sense to do, comment out line 20

**Matchup Pathway**
- matchupPath should be the pathway to the corrected matchup file you have from the Cleaner program.

**Play By Play Path**
- This should be the pathway to the folder that contains the marked up CSVs.

**Organizing Matchups**
- Line 30
- Must be uncommented for pretty much all features of program
- Organizes and Stores All Matchup Objects needed to process

**Loading Player Stat checking**
- Line 33 
- Loads the teambox.csv for future stat comparison.
---------------------

**<h3>Generating Matchup Objects:</h3>**
**NOTE: PROGRAM WILL ONLY PROCESS CSV FILES**

**NOTE: LINE 30 must be UNCOMMENTED for any matchups to be processed**
- <h3>To Generate Matchups for all Files: </h3>
  - at Line 36, to generate all files it should look like this: **generatePlaysForMatchups(csvDataGather, playByPlayPath, fileNames, false);**
   

  <p>The playByPlayPath is the pathway to where the folder is that contains all the PBP data, the  fileNames is simply the String[] in the main class for processing specific files, and the false at the end tells the method to go through all files in the folder</p>

- <h3>To Generate Matchups for Specific Files:</h3>
  - at Line 36, to generate all files it should look like this: **generatePlaysForMatchups(csvDataGather, playByPlayPath, fileNames, true);**


  <p>The playByPlayPath is the pathway to where the folder is that contains all the PBP data, the  fileNames is simply the String[] in the main class for processing specific files, and the true at the end tells the method to go through the files that have a matching name with the strings in fileNames.</p>

- <h3>To Generate a Specific Matchup:</h3>
  - at  line  42, to generate a specific matchup's play by play, you can uncomment it. The method responsible is: **generateSpecificMatchup(playByPlayPath, csvDataGather, 22100641,"[10-24-2023]-[06-17-2024]-combined-stats.csv");**
<br></br>
  - This Method also returns a matchup object, so you can do with that as you want.

<p>The playByPlayPath is the pathway to where the folder is that contains all the PBP data; The Number '22100641' is to be changed based on whatever game you are looking to process. NOTE: This number is from the PBP data, it cannot be from the matchup data as they have two separate Ids. Finally the String value at the end is the PBP file that contains the game you are looking for.</p>

<h3>Printing In-depth Matchup Look</h3>
 - at line 39, to generate an in-depth look at a matchup, meaning printing out each play, comparing stats to the stat sheet, and printing each labeled Play, uncomment this line. The method in question should look like: **printSpecificMatchup( playByPlayPath,testPlayerStats, csvDataGather, 22301071, "[10-24-2023]-[06-17-2024]-combined-stats.csv", playLabellingManager, allMatchups);**

<p>The playByPlayPath is the pathway to where the folder is that contains all the PBP data, the Number '22301071' is to be changed based on whatever game you are looking to process. NOTE: This number is from the PBP data, it cannot be from the matchup data as they have two seperate ids.The String value is the PBP file that contains the game in question. the allMatchups is just the List of all processed Matchups.</p>

---------------------
**<h3>Stat Generation</h3>**

<h3>Compare Player Stats</h3>
  - to test player stats against the teambox.csv you can uncomment line 45.

<h3>Print Non-Matching Stats </h3>
  - When uncommenting line 48 you can print out information regarding a player and their stat not matching with the sheet.
  - Make sure line 40 is also uncommented, along with line 32 so that way it works.

---------------------
 - EXAMPLE OUTPUT:
   - **GAME ID:** 22300907
   - **PLAYER NAME AND ID:** {Onuralp Bitim} 4438552
   - **TYPE:** STEALS
   - **RECORDED AMOUNT:** 1
   - **SCORE SHEET AMOUNT:** 2
   - **GAME DATE:** 2024-03-07
   - **HOME TEAM:** Golden State Warriors
   - **AWAY TEAM:** Chicago Bulls
   - **SEARCH:** Golden State Warriors vs Chicago Bulls 2024-03-07
---------------------

<h4>Printing Statistics</h4>
 - to see the amount of incorrect stats, percentage, total plays that were went through, uncomment lines: 50, 45

<h4>Deploy Stat CSV</h4>
 - To deploy a csv that essentially outputs a matchup object into a csv, simply uncomment line 52.

---------------------

**<h2>Extraction from Matchups </h2>**


- **<u>Game ID:</u>** 
  -    42300405 
- **<u>Game Date:</u>** 
  -    2024-06-17
- **<u>Game Season:</u>**
  -  2024
- **<u>Game Type:</u>** 
  -    FINAL
- **<u>Home Team:</u>** 
  -    Boston Celtics
- **<u>Away Team:</u>** 
  -    Dallas Mavericks 
- **<u>Home Starters:</u>** 
  -     Jayson Tatum, Al Horford, Jrue Holiday, Derrick White, Jaylen Brown
- **<u>Away Starters:</u>**  
  -     P.J. Washington, Derrick Jones Jr., Daniel Gafford, Kyrie Irving, Luka Doncic 
- **<u>Home Bench:</u>** 
  -     Sam Hauser, Oshae Brissett, Luke Kornet, Kristaps Porzingis, Payton Pritchard, Svi Mykhailiuk, Xavier Tillman, Neemias Queta, Jaden Springer, Jordan Walsh 
- **<u>Away Bench:</u>** 
  -     Maxi Kleber, Olivier-Maxence Prosper, Tim Hardaway Jr., Dwight Powell, Dereck Lively II, Josh Green, Dante Exum, A.J. Lawson, Jaden Hardy, Markieff Morris
