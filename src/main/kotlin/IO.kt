import java.io.File
import java.io.IOException
import java.time.LocalDate

fun getRecordFile(): File {
    val basePath = System.getProperty("user.dir")
    val dataFolder = File(basePath, "data")
    if (!dataFolder.exists()) createFolder(dataFolder)
    val todayFolder = File(dataFolder, LocalDate.now().toString())
    if (!todayFolder.exists()) createFolder(todayFolder)
    return File(todayFolder, "records.json")
}

fun writeToFile(file: File, json: String) {
    try {
        file.writeText(json)
        println("Successfully write ${file.absolutePath}")
    } catch (e: IOException) {
        println("Failed to write digest file")
    }
}

fun createFolder(todayFolder: File) {
    val created = todayFolder.mkdir()
    val message = if (created) {
        "Folder created successfully at: ${todayFolder.absolutePath}"
    } else {
        "Folder already exists at: ${todayFolder.absolutePath}"
    }
    println(message)
}