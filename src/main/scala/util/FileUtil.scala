package util

import org.apache.commons.io.{IOUtils, FileUtils}
import java.net.URLConnection
import java.io.File
import org.apache.commons.compress.archivers.zip.{ZipArchiveEntry, ZipArchiveOutputStream}
import util.ControlUtil._

object FileUtil {
  
  def getMimeType(name: String): String =
    defining(URLConnection.getFileNameMap()){ fileNameMap =>
      fileNameMap.getContentTypeFor(name) match {
        case null     => "application/octet-stream"
        case mimeType => mimeType
      }
    }

  def isImage(name: String): Boolean = getMimeType(name).startsWith("image/")
  
  def isLarge(size: Long): Boolean = (size > 1024 * 1000)
  
  def isText(content: Array[Byte]): Boolean = !content.contains(0)

  def createZipFile(dest: File, dir: File): Unit = {
    def addDirectoryToZip(out: ZipArchiveOutputStream, dir: File, path: String): Unit = {
      dir.listFiles.map { file =>
        if(file.isFile){
          out.putArchiveEntry(new ZipArchiveEntry(path + "/" + file.getName))
          out.write(FileUtils.readFileToByteArray(file))
          out.closeArchiveEntry
        } else if(file.isDirectory){
          addDirectoryToZip(out, file, path + "/" + file.getName)
        }
      }
    }

    using(new ZipArchiveOutputStream(dest)){ out =>
      addDirectoryToZip(out, dir, dir.getName)
    }
  }

  def getExtension(name: String): String =
    name.lastIndexOf('.') match {
      case i if(i >= 0) => name.substring(i + 1)
      case _ => ""
    }

}