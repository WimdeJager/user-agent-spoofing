# user-agent-spoofing
 
This is a Bachelor's project made at the end of the Bachelor Computing 
Science at the University of Groningen. 

I have built a tool that is given an APK file (or a dataset of APK files), 
and tries to extract the UA(s) that this APK might use, and then tries to 
determine whether this APK makes use of User-Agent Spoofing. For more 
information, I would like to refer to my Thesis, which I will post here once 
it is done.
 
## Instructions

Clone the repository, and navigate to the root directory of the project. Run 
```
mvn package
```

When this is done, run 
```
java -cp target/user-agent-spoofing-1.0.jar uaspoofing.Main <input file/dir>
```
