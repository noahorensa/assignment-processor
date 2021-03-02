import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Files
import scala.io.Source

object preprocess extends processor {

  def readCSV(path: String): Seq[Array[String]] = {
    val f = Source.fromFile(path)
    val data = f.getLines()
      .filter(_.nonEmpty)
      .map(_.split(","))
      .toArray
    f.close()
    data
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: preprocess <student-db> <path-to-submissions-directory>")
      System.exit(1)
    }

    val studentDbFile = args(0)
    logger.info("Reading " + studentDbFile)
    val student = readCSV(studentDbFile)

    val submissionsDir = args(1)

    val feedbackTemplateFileName = submissionsDir + File.separator + "feedback-template.txt"
    logger.info("Reading " + feedbackTemplateFileName)
    val feedbackTemplateFile = Source.fromFile(feedbackTemplateFileName)
    val feedbackTemplate = feedbackTemplateFile.getLines().toList

    val previous =
      if (args.length > 2) new File(args(2)).listFiles().filter(_.isDirectory).toList
      else Nil

    logger.info("Processing submissions in " + submissionsDir)
    new File(submissionsDir).listFiles().filter(_.isDirectory).foreach(submissionDir => {
      val s = student.find(ss => submissionDir.getName.contains(ss(0)))

      if (s.nonEmpty) {
        val name = s.get(0)
        val nsid = s.get(1)

        val newDir = new File(submissionDir.getParentFile.getAbsolutePath + File.separator + nsid)
        logger.info(s"Renaming ${submissionDir.getName} to $nsid")
        submissionDir.renameTo(newDir)

        val feedbackFile = new File(newDir.getAbsolutePath + File.separator + "feedback_" + nsid + ".txt")
        logger.info(s"Creating feedback file ${feedbackFile.getName}")

        feedbackFile.createNewFile()
        val writer = new BufferedWriter(new FileWriter(feedbackFile))
        writer.write(feedbackTemplate.head + "\n\n")
        writer.write(s"Name: $name\n")
        writer.write(s"NSID: $nsid\n")
        feedbackTemplate.tail.foreach(l => writer.write(l + "\n"))
        writer.close()

        if (previous.nonEmpty) {
          val prevSubmissionDir = previous.find(_.getName == nsid)
          if (prevSubmissionDir.nonEmpty) {
            prevSubmissionDir.get.listFiles().filter(_.getName.startsWith("feedback")).foreach(f => {
              Files.copy(f.toPath, new File(newDir.getAbsolutePath + File.separator + "prev_" + f.getName).toPath)

              logger.info(s"Copied previous feedback file ${f.getName}")
            })
          }
        }

        newDir.listFiles().filter(_.getName.endsWith(".tar.gz")).foreach(f => {
          logger.info(s"Extracting ${f.getName}")

          Runtime.getRuntime.exec(s"tar -xvzf ${f.getName}", null, newDir)
        })

        newDir.listFiles().filter(_.getName.endsWith(".tar")).foreach(f => {
          logger.info(s"Extracting ${f.getName}")

          Runtime.getRuntime.exec(s"tar -xvf ${f.getName}", null, newDir)
        })
      }
      else {
        logger.warning(s"Skipping ${submissionDir.getName}")
      }
    })

    feedbackTemplateFile.close()
  }
}
