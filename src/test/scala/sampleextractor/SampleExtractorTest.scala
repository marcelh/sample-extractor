package sampleextractor

import org.scalatest.FunSpec
import java.io.File

class SampleExtractorTest extends FunSpec  {

    describe("tree") {
        it("should return files in this project when argument is 'src'") {
            val files = SampleExtractorMain.tree(new File("src")).toList
            assert(files.contains(new File("src/main/scala/sampleextractor/SampleExtractor.scala")))
            assert(files.contains(new File("src/test/scala/sampleextractor/SampleExtractorTest.scala")))
        }
        it("should return nothing when argument is a non-existing directory") {
            val files = SampleExtractorMain.tree(new File("pipo")).toList
            assert(files.isEmpty)
        }
    }
}