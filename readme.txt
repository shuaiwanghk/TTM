Targeted Topic Model (TTM)
A package for targeted topic modeling for focused analysis.

1. A JAVA implementation for targeted topic modeling;
2. Used for focused analysis purpose;
3. By specifying the target (aspect) word to obtain its target-related topics

We are glad if the package helps your projects or research. Please cite our paper with the following information.

Publication
Shuai Wang, Zhiyuan Chen, Geli Fei, Bing Liu and Sherry Emery, "Targeted Topic Modeling for Focused Analysis", SIGKDD 2016.

BibTex
@inproceedings{wang2016targeted,
  title={Targeted Topic Modeling for Focused Analysis},
  author={Wang, Shuai and Chen, Zhiyuan and Fei, Geli and Liu, Bing and Emery, Sherry},
  booktitle={Proceedings of the 22th ACM SIGKDD international conference on Knowledge discovery and data mining},
  year={2016},
  organization={ACM}
}

Table of Content
1. Input Data Format
2. Model and Program Entry
	2.1 Parameters and settings
	2.2 Single task
	2.3 Multiple tasks/threads
3. Output File
4. Run Demo/Entry File
	4.1 Run in IDE.
	4.2 Run by command line.
	
Input Data Format:
(1) docs.txt
Every single file in a (domain) corpus is arranged in the following format.
---
line 1: #numOfSent. The number of sentences for one review (always one for a single tweet)
line 2: dummy field (Just a placeholder. Not useful for modeling but still need to place it in the raw data. It was used for my debugging.) 
line 3 to (3+#numOfSent): content/text of a sentence. A value is a word index from the wordlist.txt file.
---
(repeated this for all files)

Example:
3 // number of sentence for review 1
0 // dummy field
1 2 3 // sentence 1 (in review 1)
4 5	// sentence 2 (in review 1)
6 7 8 // sentence 3 (in review 1)
2 // number of sentence for review 2
0 // dummy field
3 4 // sentence 1 (in review 2)
5 8// sentence 2 (in review 2)
....

(2) wordlist.txt
a. This is a vocabulary file, which indexes words in a given domain.
b. The stop words and infrequent words have been removed.

2. Model and Program Entry
2.1 Parameters and settings
	A corresponding model will be saved.
	The parameters and argument settings are set in argument -> ProgramArgument.java file.
	Among them, the most important settings are:
		a. domainName (the domain/dataset name)
		b. targetWord (the keyword of the targeted aspect)
		c. tTopicNum (targeted topic number)
	Please refer to ProgramArgument.java for details.
	
2.2 Single task
	The task file locates in task -> RunTTMwithOneSingleTask, which is for running a single task.
	A corresponding model will be also saved.
	
2.3 Multiple tasks/threads
	We also provide a multiple tasks/threads entry so that we can target at different aspects parallelly.
	The task file locates in task -> RunTTMwithMultiTasks, which is for running multiple tasks.  

3. Output File
An output file with targeted topic-word distribution will be generated in a file under data/output

Note that I have rewritten my codes with some code optimization and reconstruction so the final produced results might be slightly different from my previous ones.

4. Run Demo/Entry File
4.1 Run in IDE.
	Two files are provided. You should be able to run them (with libraries in the lib folder added). They are:
	4.1.1 src->launcher->TTMSingleTaskEntry
	4.1.2 src->launcher->TTMMultipleTasksEntry
4.2  Run in Terminal by command lines.
	Under the ttm root directory.
	4.2.1 In Windows:
	java -cp bin;lib/* launcher.TTMSingleTaskEntry
	4.2.2 In Unix/Linux:
	java -cp bin:lib/* launcher.TTMSingleTaskEntry


Have fun!

Contact Information
Author: Shuai Wang
Affiliation: University of Illinois at Chicago
Email: shuaiwanghk@gmail.com


