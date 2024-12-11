package loki.edu.yogaclassadmin.database.repository.sqlite

import android.content.ContentValues
import android.content.Context
import loki.edu.yogaclassadmin.database.sqlite.DatabaseHelper
import loki.edu.yogaclassadmin.model.YogaClass

class SQLiteYogaClassRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)


    fun getFirstClass(): YogaClass? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CLASSES,
            null,
            null,
            null,
            null,
            null,
            null,
            "1" // Limit query to one result
        )
        return if (cursor.moveToFirst()) {
            val yogaClass = YogaClass(
                id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                capacity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAPACITY)),
                class_type_id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_TYPE)),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)),
                image_url = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGE_URL))
            )
            cursor.close()
            yogaClass
        } else {
            cursor.close()
            null
        }
    }

    // Update an existing YogaClass in the database
    fun updateClass(yogaClass: YogaClass): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TITLE, yogaClass.title)
            put(DatabaseHelper.COLUMN_DATE, yogaClass.date)
            put(DatabaseHelper.COLUMN_TIME, yogaClass.time)
            put(DatabaseHelper.COLUMN_CAPACITY, yogaClass.capacity)
            put(DatabaseHelper.COLUMN_CLASS_TYPE, yogaClass.class_type_id)
            put(DatabaseHelper.COLUMN_PRICE, yogaClass.price)
            put(DatabaseHelper.COLUMN_DESCRIPTION, yogaClass.description)
            put(DatabaseHelper.COLUMN_IMAGE_URL, yogaClass.image_url)
        }
        return db.update(
            DatabaseHelper.TABLE_CLASSES,
            values,
            "${DatabaseHelper.COLUMN_CLASS_ID} = ?",
            arrayOf(yogaClass.id)
        )
    }


    // Add a new YogaClass to the database
    fun addClass(yogaClass: YogaClass): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CLASS_ID, yogaClass.id)
            put(DatabaseHelper.COLUMN_TITLE, yogaClass.title)
            put(DatabaseHelper.COLUMN_DATE, yogaClass.date)
            put(DatabaseHelper.COLUMN_TIME, yogaClass.time)
            put(DatabaseHelper.COLUMN_CAPACITY, yogaClass.capacity)
            put(DatabaseHelper.COLUMN_CLASS_TYPE, yogaClass.class_type_id)
            put(DatabaseHelper.COLUMN_PRICE, yogaClass.price)
            put(DatabaseHelper.COLUMN_DESCRIPTION, yogaClass.description)
        }
        return db.insert(DatabaseHelper.TABLE_CLASSES, null, values)
    }

    // Retrieve all YogaClass records from the database
    fun getClasses(): List<YogaClass> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_CLASSES, null, null, null, null, null, null)
        val classes = mutableListOf<YogaClass>()

        if (cursor.moveToFirst()) {
            do {
                val yogaClass = YogaClass(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIME)),
                    capacity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAPACITY)),
                    class_type_id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CLASS_TYPE)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRICE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION))
                )
                classes.add(yogaClass)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return classes
    }

    fun deleteClass(classId: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete(DatabaseHelper.TABLE_CLASSES, "${DatabaseHelper.COLUMN_CLASS_ID} = ?", arrayOf(classId))
    }

    // Delete all YogaClass records
    fun deleteAllClasses() {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_CLASSES, null, null)
    }

}
