import java.io.File
import java.nio.file.Files

object postprocess extends processor {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      println("Usage: postprocess <path-to-submissions-directory> <path-to-feedback-directory>")
      System.exit(1)
    }

    val submissionsDir = new File(args(0))
    val feedbackDir = new File(args(1))

    feedbackDir.mkdir()

    logger.info("Processing submissions in " + submissionsDir.getAbsolutePath)
    submissionsDir.listFiles().filter(_.isDirectory).foreach(submissionDir => {
      val feedbackFileSrc = submissionDir.listFiles().filter(_.getName.startsWith("feedback")).head
      val feedbackFileDst = new File(feedbackDir.getAbsolutePath + File.separator + feedbackFileSrc.getName)

      Files.copy(feedbackFileSrc.toPath, feedbackFileDst.toPath)

      logger.info(feedbackFileSrc.getAbsolutePath + " -> " + feedbackFileDst.getAbsolutePath)
    })
  }
}
