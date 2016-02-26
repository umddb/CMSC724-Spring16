# Assignment 3

Assignment 3 focuses on using Apache Spark for doing large-scale data analysis tasks. For this assignment, we will use relatively small datasets and  we won't run anything in distributed mode; however Spark can be easily used to run the same programs on much larger datasets.

## Getting Started with Spark

This guide is basically a summary of the excellent tutorials that can be found at the [Spark website](http://spark.apache.org).

[Apache Spark](https://spark.apache.org) is a relatively new cluster computing framework, developed originally at UC Berkeley. It significantly generalizes
the 2-stage Map-Reduce paradigm (originally proposed by Google and popularized by open-source Hadoop system); Spark is instead based on the abstraction of **resilient distributed datasets (RDDs)**. An RDD is basically a distributed collection 
of items, that can be created in a variety of ways. Spark provides a set of operations to transform one or more RDDs into an output RDD, and analysis tasks are written as
chains of these operations.

Spark can be used with the Hadoop ecosystem, including the HDFS file system and the YARN resource manager. 

### Installing Spark

1. Download the Spark package at http://spark.apache.org/downloads.html. We will use **Version 1.6.0, Pre-built for CDH 4**.
2. Move the downloaded file to the `Assignment3` directory (or somewhere else), and uncompress it using: 
`tar zxvf spark-1.6.0-bin-cdh4.tgz`
3. This will create a new directory: `spark-1.6.0-bin-cdh4`. 
4. Set the SPARKHOME variable: `export SPARKHOME=XXX/Assignment3/spark-1.6.0-bin-cdh4` (modify according to where you uncompressed it)
5. If you want to reduce the amount of output that SPARK is producing, copy the `log4j.properties` file into `$SPARKHOME/conf`.

We are ready to use Spark. 

### Installing SBT

You should also install `sbt`. You can do the assignment without it, but it will make things easier (if you are familiar with `mvn`, `sbt` is similar).
Follow the directions here: [SBT](http://www.scala-sbt.org/release/docs/Setup.html)

### Using Spark

Spark provides support for three languages: Scala (Spark is written in Scala), Java, and Python. We will use Scala here -- you can follow the instructions at the tutorial
and quick start (http://spark.apache.org/docs/latest/quick-start.html) for other languages. 

Scala is a JVM-based functional programming language, but it's syntax and functionality is quite different from Java. 
The [Wikipedia Article](http://en.wikipedia.org/wiki/Scala_%28programming_language%29) is a good start to learn about Scala, 
and there are also quite a few tutorials out there. For this assignment, we will try to minimize the amount of Scala you have
to learn and try to provide sufficient guidance.


1. `$SPARKHOME/bin/spark-shell`: This will start a Scala shell (it will also output a bunch of stuff about what Spark is doing). The relevant variables are initialized in this shell, but otherwise it is just a standard Scala shell.

2. `> val textFile = sc.textFile("README.md")`: This creates a new RDD, called `textFile`, by reading data from a local file. The `sc.textFile` commands create an RDD
containing one entry per line in the file.

3. You can see some information about the RDD by doing `textFile.count()` or `textFile.first()`, or `textFile.take(5)` (which prints an array containing 5 items from the
        RDD).

4. We recommend you follow the rest of the commands in the quick start guide (http://spark.apache.org/docs/latest/quick-start.html). Here we will simply do the Word Count
application.

### Word Count Application

`val counts = textFile.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey((a, b)  => a + b)`

The `flatmap` splits each line into words, and the following `map` and `reduce` basically do the word count (in a similar fashion to standard MapReduce wordcount -- see, e.g., [link](http://kickstarthadoop.blogspot.com/2011/04/word-count-hadoop-map-reduce-example.html)).

### SimpleApp.scala

The file `src/main/scala/SimpleApp.scala` contains the code to construct a few more RDDs (using the data files in the `Assignment3` directory), and some examples of operations on them. You can try running the code using `spark-shell` as above (cut-n-paste). Start with the code in the `main` function.

You can also directly execute the file. For this, you must first assemble a jar file using `sbt package` command.
Then the following command executes the Spark job in a local manner (a simple change to the command can do this on a cluster, assuming you have
the cluster already running).

`$SPARKHOME/bin/spark-submit --class "SimpleApp" --master "local[4]" target/scala-2.10/simple-project_2.10-1.0.jar`

Copy the `log4j.properties` file to the $SPARKHOME/conf directory to avoid spurious logging output.



### More...

We encourage you to look at the [Spark Programming Guide](https://spark.apache.org/docs/latest/programming-guide.html) and play with the other RDD manipulation commands. 



## Assignment Details

You have to fill in the five `task_` functions in the `SimpleApp.scala` file.

The `SimpleApp.scala` code constructs:
* An RDD consisting of lines from a Shakespeare play (`play.txt`)
* An RDD consisting of lines from a log file (`NASA_logs_sample.txt`)
* An RDD consisting of 2-tuples indicating friends relationships from a Social Network (`livejournal_sample.txt`)

The file also contains 3 examples of operations on these RDDs. 

Your tasks are to fill out the 5 functions that are defined (starting with `task`). The amount of code that you 
write would be very small.

* **Task 1**: This takes as input the playRDD and a list of words, and should count the number of different lines in which each of those words appeared.

* **Task 2**:  [Bigrams](http://en.wikipedia.org/wiki/Bigram) are sequences of two consecutive words. For example, the previous sentence contains the following bigrams: "Bigrams
are", "are simply", "simply sequences", "sequences of", etc.
Your task is to write a **Bigram Counting** application that can be composed as a two stage Map-Reduce job. 
	- The first stage counts bigrams.
	- The second stage MapReduce job takes the output of the first stage (bigram counts) and computes for each word the top 5 bigrams by count that it is a part of, and the bigram count associated with each.
Only count bigrams that appear within a single line (i.e., don't worry about bigrams where one word is the end of one line and the second word is the beginning of next.
The return value should be an PairRDD where the key is a word, and the value is the top 5 bigrams by count that it is a part of.

* **Task 3**: Here the goal is to count the [Jaccard Index](https://en.wikipedia.org/wiki/Jaccard_index) between two given hosts. In other words, find the set of the URLs visited by each of the two hosts, count the size of the
intersection of the two sets and divide by the size of the union of the two sets (don't forget to use `distinct()` to remove duplicates from the sets). The format of the log entries should be self-explanatory, but here are more details if you need: [NASA Logs](http://ita.ee.lbl.gov/html/contrib/NASA-HTTP.html)

* **Task 4**: For the logsRDD, count the top 5 URLs that have been visited.  You will first need to figure out how to parse the log lines appropriately to extract the URLs. The return should just be the list of 5 URLs.

* **Task 5**: `task5_pagerank` should implement one iteration of the standard algorithm for computing PageRanks (see [Wikipedia Page](https://en.wikipedia.org/wiki/PageRank) for more details on PageRank). This is an iterative algorithm that uses the values computed in the previous iteration to re-compute the values for the next iteration. Specifically, you should implement the formula described in the above Wikipedia page in the section `Damping Factor`. Use the damping factor of 0.85. The result should be an RDD that looks very similar to `initial_pageranks` but with new values (note that since RDDs are immutable, you have to construct a new RDD and return it).

### Submission

Submit the `SimpleApp.scala` file.
