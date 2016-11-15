Targeted Topic Model (TTM)
----
A package for targeted topic modeling for focused analysis.

1. A JAVA implementation for targeted topic modeling;
2. Used for focused analysis purpose;
3. By specifying the target (aspect) word to obtain its target-related topics

We are glad if the package helps your projects or research. Please cite our paper with the following information. You are welcome to contact Shuai Wang (shuaiwanghk@gmail.com) if you have any problem.


## Publication
Shuai Wang, Zhiyuan Chen, Geli Fei, Bing Liu and Sherry Emery, "**[Targeted Topic Modeling for Focused Analysis](http://www.kdd.org/kdd2016/papers/files/rfp0017-wangA.pdf)**", SIGKDD 2016.

## Table of Content
- [Input Data Format](#input)
- [Model and Program Entry](#model)
- [Output File](#output)
- [Run Demo/Entry File](#run)

<a name="input"/>
## Input Data Format

<b>(1) docs.txt</b><br />
Every single file in a given (domain) corpus is arranged in the following format.<br />
Line 1: #numOfSent. The number of sentences for one review (always one for a single tweet)<br />
Line 2: dummy field (Just a placeholder. Not useful for modeling but still need to place it in the raw data. It was used for my debugging.) <br />
Line 3 to (3+#numOfSent): content/text of a sentence. <br />
(repeating the above format for all files)<br />
A value is a word index from the wordlist.txt file.<br />

<b>Example:</b><br />
3 // number of sentence for review 1;<br />
0 // dummy field<br />
1 2 3 // sentence 1 (in review 1)<br />
4 5	// sentence 2 (in review 1)<br />
6 7 8 // sentence 3 (in review 1)<br />
2 // number of sentence for review 2<br />
0 // dummy field<br />
3 4 // sentence 1 (in review 2)<br />
5 8// sentence 2 (in review 2)<br />
....

<b>(2) wordlist.txt</b><br />
a. This is a vocabulary file, which indexes words in a given domain.<br />
b. The stop words and infrequent words have been removed.


<a name="model"/>
## Model and Program Entry
<b>(1) Parameters and settings</b><br \>
A corresponding model will be saved.The parameters and argument settings are set in argument -> ProgramArgument.java file.<br \>

Among them, the most important settings are:<br \>
a. domainName (the domain/dataset name)<br \>
b. targetWord (the keyword of the targeted aspect)<br \>
c. tTopicNum (targeted topic number)<br \>
Please refer to ProgramArgument.java for details.
	
<b>(2) Single task</b><br \>
The task file locates in task -> Execute TTMwithOneSingleTask, which is for running a single task. A corresponding model will be also saved.
	
<b>(3) Multiple tasks/threads</b><br \>
We also provide a multiple tasks/threads entry so that we can target at different aspects parallelly.The task file locates in task -> RunTTMwithMultiTasks, which is for running multiple tasks. 

<a name="output"/>
## Output File
An output file with targeted topic-word distribution will be generated in a file under data/output folder.

Note that I have rewritten my codes with some code optimization and reconstruction so the final produced results might be slightly different from my previous ones.

<a name="run"/>
## Run Demo/Entry File
<b>(1) Run in IDE.</b><br />
Two files are provided. You should be able to run them (with libraries in the lib folder added). They are:<br />

	src -> launcher -> TTMSingleTaskEntry
	src -> launcher -> TTMMultipleTasksEntry

<b>(2) Run in Terminal by command lines.</b><br />
Under the ttm root directory in Windows:

	java -cp bin;lib/* launcher.TTMSingleTaskEntry

Under the ttm root directory in Unix/Linux:

	java -cp bin:lib/* launcher.TTMSingleTaskEntry

Have fun!

## Contact Information
* Author: Shuai Wang
* Affiliation: University of Illinois at Chicago
* Email: shuaiwanghk@gmail.com
