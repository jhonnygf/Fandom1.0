package edu.jonathangf.fandom.utils

import android.content.Context
import android.util.Log
import edu.jonathangf.fandom.R
import edu.jonathangf.fandom.model.Fandom
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader

var fandomMutableList = mutableListOf<Fandom>()

/**
 * Utils.kt file
 * Method that reads a csv file and extracts the data to create objects and add them to a list,
 * checks if they are marked as favorites or if they have been removed from the list, sorts them
 * alphabetically and updates the global list.
 * @author Jonathan Gómez Fraile
 *
 * @param context Application context required to access resources and files.
 * @return A mutable list of Fandom items.
 */

fun readRawFile(context: Context): MutableList<Fandom> {

    val fandoms = mutableListOf<Fandom>()
    try {
        val entrada = InputStreamReader(
            context.resources.openRawResource(R.raw.data)
        )
        val br = BufferedReader(entrada)

        val deletedIds = readFandomOptions(context, R.string.filenameDeleted)
        val favoriteIds = readFandomOptions(context, R.string.filenameFavs)

        var linea = br.readLine()
        while (!linea.isNullOrEmpty()) {
            val parts = linea.split(";")
            if (parts.size >= 6) {
                val id = parts[0].toIntOrNull()
                if (id == null) {
                    Log.e("Utils", "ID inválido en línea: $linea")
                    linea = br.readLine()
                    continue
                }
                val name = parts[1]
                val universe = parts[2]
                val description = parts[3]
                val image = parts[4]
                val info = parts[5]

                if (deletedIds.contains(id)) {
                    linea = br.readLine()
                    continue
                }

                val isFav = favoriteIds.contains(id)

                val fandom = Fandom(
                    id = id,
                    name = name,
                    universe = universe,
                    description = description,
                    image = image,
                    info = info,
                    fav = isFav,
                    visible = true
                )
                fandoms.add(fandom)
            } else {
                Log.e("Utils", "Línea con formato incorrecto: $linea")
            }
            linea = br.readLine()
        }
        br.close()
        entrada.close()

        fandoms.sortBy { it.name }

    } catch (e: IOException) {
        Log.e("Utils", "Error al leer el archivo CSV: ${e.message}")
    }

    fandomMutableList = fandoms

    return fandoms
}

/**
 * Utils.kt file
 * Update the options file by adding or removing a fandom ID.
 * @author Jonathan Gómez Fraile
 *
 * @param context Application context required to access files.
 * @param file Reference to the string resource containing the file name
 * @param fandomId ID of the fandom to add or remove from the options file.
 * @param isAdding Indicates whether to add true or remove false the fandom from the options file.
 */

fun updateFilesOptions(context: Context, file: Int, fandomId: Int, isAdding: Boolean) {

    try {
        val currentList = readFandomOptions(context, file)

        if (isAdding) {
            if (!currentList.contains(fandomId)) {
                currentList.add(fandomId)
            }
        } else {
            currentList.remove(fandomId)
        }

        writeFandomOptions(context, file, currentList)

    } catch (e: Exception) {
        Log.e("Utils", "Error updating options file: ${e.message}")
    }
}

/**
 * Utils.kt file
 * Method to delete favorite and deleted option files when refreshing the screen.
 * @author Jonathan Gómez Fraile
 *
 * @param context Application context required to access files.
 */

fun deleteFilesOptions(context: Context) {
    try {
        val favoritesFileName = context.getString(R.string.filenameFavs)
        val deletedFileName = context.getString(R.string.filenameDeleted)

        context.deleteFile(favoritesFileName)
        context.deleteFile(deletedFileName)

    } catch (e: Exception) {
        Log.e("Utils", "Error al eliminar los ficheros de opciones: ${e.message}")
    }
}

/**
 * Utils.kt file
 * Method that reads a favorites or deleted file and returns a list of Fandom IDs. Checks if it
 * exists, if it exists it opens it and reads its contents, if it does not exist it returns an
 * empty list.
 * @author Jonathan Gómez Fraile
 *
 * @param context Application context required to access files and resources.
 * @param file Name of the file.
 * @return A mutable list of integers with the IDs of the Fandoms.
 */

fun readFandomOptions(context: Context, file: Int):
        MutableList<Int> {
    val list = mutableListOf<Int>()
    try {
        val fileName = context.getString(file)
        Log.d("Utils", "Intentando abrir el archivo: $fileName")

        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            Log.d("Utils", "El archivo $fileName no existe, se devolverá una lista vacía")
            return mutableListOf()
        }
        val fileInputStream = context.openFileInput(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(fileInputStream))
        var line: String?

        while (bufferedReader.readLine().also { line = it } != null) {
            val id = line!!.toIntOrNull()
            if (id != null) {
                list.add(id)
            }
        }

        bufferedReader.close()
    } catch (e: FileNotFoundException) {
        Log.e("Utils", "Error FILENOTFOUND al leer el fichero de opciones: ${e.message}")
    } catch (e: IOException) {
        Log.e("Utils", "Error IO al leer el fichero de opciones: ${e.message}")
    }
    return list
}

/**
 * Utils.kt file
 * Write a list of Fandom IDs to a file. Open the file and if it exists, create it.
 * @author Jonathan Gómez Fraile
 *
 * @param context Application context required to access resources and files.
 * @param file Name of the file.
 * @param listOfFandomIds Mutable list of integers with the IDs of the Fandoms.
 */

fun writeFandomOptions(context: Context, file: Int, listOfFandomIds: MutableList<Int>) {
    try {
        val fileName = context.getString(file)
        Log.d("Utils", "Intentando abrir el archivo: $fileName")

        val fileOutputStream: FileOutputStream =
            context.openFileOutput(fileName, Context.MODE_PRIVATE)

        listOfFandomIds.forEach { id ->
            fileOutputStream.write("$id\n".toByteArray())
        }

        fileOutputStream.close()
    } catch (e: Exception) {
        Log.e("Utils", "Error al escribir en el fichero de opciones: ${e.message}")
    }
}

