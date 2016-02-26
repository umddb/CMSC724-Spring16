/* SimpleApp.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD

import scala.util.matching.Regex

object SimpleApp {

    def example1(rdd: RDD[String]): Unit = {
        val numAs = rdd.filter(line => line.contains("a")).count()
        val numBs = rdd.filter(line => line.contains("b")).count()

        println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
    }

    // Word Count Application -- write out the output 
    def example2(playRDD: RDD[String]): Unit = {
        val counts = playRDD.flatMap(line => line.split(" ")).map(word => (word, 1)).reduceByKey(_ + _)
        counts.sortByKey().take(99).foreach(println)
    }

    def example3(logsRDD: RDD[String]): Unit = {
        val hosts: RDD[String] = logsRDD.map(logline => "^(\\S+) ".r.findFirstIn(logline)).collect{case Some(t) => t}
        val hosts_distinct: RDD[String] = hosts.distinct()

        println("In the %s log entries, there are %s unique hosts".format(hosts.count(), hosts_distinct.count()))
    }

    // The following shows how to find the list of neighbors of each node, and also the degree of each node
    def example4(socialnetRDD: RDD[(Int, Int)]): Unit = {
        // To compute degrees, we first use a map to output (u, 1) for every edge (u, v). Then we can compute the degrees using a reduceByKey and a sum reducer
        val degrees = socialnetRDD.map(x => (x._1, 1)).reduceByKey(_ + _)

        // Using groupByKey instead gives us a list of neighbors
        val neighbors_1 = socialnetRDD.groupByKey()

        // We can convert that into a list of neighbors as follows
        val neighbors_2 = socialnetRDD.groupByKey().map(x => (x._1, x._2.toList))

        degrees.take(10).foreach(println)
        neighbors_2.take(10).foreach(println)
    }

    // Given the playRDD and a list of words: for each word, count the number of lines that contain it -- the return should be a list 
    // with the same length as list_of_words containing the counts
    def task1_count_lines(playRDD: RDD[String], list_of_words: List[String]) : List[Int] = {
        return List(0)
    }

    // The following function should solve the bigram counting problem; the return should be an RDD
    def task2_count_bigrams(playRDD: RDD[String]) : RDD[(String, String)] = {
        return playRDD.map(r => (r, r))
    }

    // Given two hosts (see example below), find the Jaccard Similarity Coefficient between them based on
    // the URLs they visited. i.e., count the number of URLs that they both visited (ignoring duplicates),
    // and divide by the total number of unique URLs visited by either
    // The return value should be a Double
    def task3_find_similarity(logsRDD: RDD[String], host1: String, host2: String) : Int = {
        return -1
    }

    // The following function should find the top 5 URLs that were accessed most frequently in the provided logs
    // The result should be simply a list with 5 strings, denoting the URLs
    def task4_top_5_URLs(logsRDD: RDD[String]) : List[String] = {
        return List()
    }

    // Implement one iteration of PageRank on the socialnetRDD. Specifically, the input is 2 RDDs, the socialnetRDD and another RDD containing the 
    // current PageRank for the vertices
    // Compute the new PageRank for the vertices and return that RDD
    def task5_pagerank(socialnetRDDs: RDD[(Int, Int)], pagerankPreviousIterationRDD: RDD[(Int, Double)]): RDD[(Int, Double)] = pagerankPreviousIterationRDD


    def main(args: Array[String]) {
        val conf = new SparkConf().setAppName("Simple Application")
        val sc = new SparkContext(conf)

        // Load data into RDDs
        val playRDD: RDD[String] = sc.textFile("play.txt")
        val logsRDD: RDD[String] = sc.textFile("NASA_logs_sample.txt")
        val socialnetinputRDD: RDD[String] = sc.textFile("livejournal_sample.txt")

        // The following converts the socialnetRDD into 2-tuples with integers
        val socialnetRDD: RDD[(Int, Int)] = socialnetinputRDD.map(x => x.split("\t")).map(x => (x(0).toInt, x(1).toInt))

        println("============================ Executing example1 function....")
        example1(playRDD)
        println("============================ Executing example2 function....")
        example2(playRDD)
        println("============================ Executing example3 function....")
        example3(logsRDD)
        println("============================ Executing example4 function....")
        example4(socialnetRDD)

        // Below are the commands to execute the functions you will be writing
        println("============================ Executing task1_count_lines function....")
        println(task1_count_lines(playRDD, List("LEONATO", "Messenger", "BENEDICK", "Arragon")))

        println("============================ Executing task2_count_bigrams function....")
        task2_count_bigrams(playRDD)

        println("============================ Executing task3_find_similarity function....")
        val host1 = "199.120.110.21"
        val host2 = "d104.aa.net"
        println("The Jaccard Similarity between hosts %s and %s is %s".format(host1, host2, task3_find_similarity(logsRDD, host1, host2)))

        println("============================ Executing task4_top_5_URLs function....")
        println(task4_top_5_URLs(logsRDD))

        // The following creates an initial set of PageRanks to pass to the iterative function
        println("============================ Executing task5_pagerank function....")
        val vertices = socialnetRDD.flatMap(x => List(x._1, x._2)).distinct()
        val count_vertices = vertices.count()
        val initial_pageranks = vertices.map(x => (x, 1.0/count_vertices))
        task5_pagerank(socialnetRDD, initial_pageranks).take(10).foreach(println)
    }
}
