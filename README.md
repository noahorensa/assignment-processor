# assignment-processor
A simple Scala script to help with preparing student assignment submissions for grading.

### Build
Build using sbt to build the jar 
###
    sbt package
###

### Usage
Once you have the jar in target/, run using scala.
###
    scala -cp your-jar.jar preprocess <student-list.csv> <path-to-submissions-directory>
###
The specified directory must have a feedback-template.txt file to be used to initialize feedback files in the sub-directories. 

If you want to collect the feedback files as well, run
###
    scala -cp your-jar.jar postprocess <path-to-submissions-directory> <path-to-feedback-directory>
###
