package sampleextractor

import java.io.File
import resource._
import scala.io.Source
import java.io.FileWriter
import java.io.Writer
import scala.collection.mutable.Stack
import java.util.regex.Pattern

/**
 * Extract samples from 'src' directory and put the sample files in 'target/samples' directory.
 */
object SampleExtractorMain extends App {
    
    main(new File("src"), new File("target/samples"))
    
    def main(sourceDir: File, targetDir: File) {
        tree(sourceDir).filter(_.isFile()).foreach(f => SampleExtractor.extractSamples(f, targetDir))
    }

    // #@!>> tree.txt
    def tree(root: File, skipHidden: Boolean = true): Stream[File] =
        if (!root.exists || (skipHidden && root.isHidden)) Stream.empty
        else root #:: (
            root.listFiles match {
                case null  => Stream.empty
                case files => files.toStream.flatMap(tree(_, skipHidden))
            })
    // #@!<<
}

class SampleExtractor 

object SampleExtractor {

    val startTag = "#@" + "!>>"
    val endTag = "#@" + "!<<"

    def extractSamples(inputFile: File, destDir: File) {
        destDir.mkdirs
        if (!inputFile.exists()) {
            sys.error(s"Cannot read input file: ${inputFile}")
        }
        for {
            in <- managed(Source.fromFile(inputFile))
        } extractSamples(in.getLines, destDir)
    }

    def extractSamples(lines: Iterator[String], destDir: File) {
        val pattern = Pattern.compile(".*" + startTag + "\\s*([^\\s]+).*")

            def extractSample(lines: Iterator[String], filename: String) {
                val output = new File(destDir, filename)
                if (output.exists) output.delete
                println(s"Writing ${output}")
                managed(new FileWriter(output)) acquireAndGet {
                    writer =>

                        var continue = true
                        while (lines.hasNext && continue) {
                            val line = lines.next
                            if (line.contains(endTag))
                                continue = false
                            else {
                                writer.write(line)
                                writer.write('\n')
                            }
                        }
                }
            }
        
        while (lines.hasNext) {
            val line = lines.next
            val matcher = pattern.matcher(line)
            if (matcher.matches()) {
                extractSample(lines, matcher.group(1))
            }
        }
    }
}
