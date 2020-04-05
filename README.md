#DrawingSchedule - demo version
Application for generating drawings schedules based on directory chosen by user.  
Schedules are saved in .xls file.   
Application divides for drawing groups according to subdirectories from directory chosen by user.   
To generate schedule important is name of files. Name should consistent with schema:  
#####Drawing number-Drawing name(-Rew_xx). Allowed type files: .dwg or .pdf.    
User can not only creates single schedule, but also add new files to earlier generated schedule.  
Application can read last modified Excel file from chosen directory ("Directory with schedules").  
Application recognizes drawing groups (equivalent subdirectory from files) from Excel file.  
To repository is included sample project where you can find directories with correctly named files.  
In case of errors user receives information window and then is created .txt with errors description.    
  
###Getting started

1. Clone project and add as Maven project. Update Maven dependencies.
2. This is Java 8 project. In case any error check your configuration.
3. Run Main.java or build executable jar and run java -jar DrawingsSchedule-1.0.jar
4. Then you can see main window of application:
![Alt text](ApplicationView.png?raw=true "Application window")

5. To try you can use sample project included to resources.
6. If schedule is generated correctly new Excel file is opening automatically. 
In case of errors should open file error.txt with errors description.
7. In case of any questions feel free to ask.

