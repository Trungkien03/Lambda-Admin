package loki.edu.yogaclassadmin.database.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Create User table
        db.execSQL("CREATE TABLE $TABLE_USER (" +
                "$COLUMN_USER_ID TEXT PRIMARY KEY, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_ROLE TEXT, " +
                "$COLUMN_SPECIALIZATION TEXT, " +
                "$COLUMN_CONTACT TEXT)")

        // Create Classes table
        db.execSQL("CREATE TABLE $TABLE_CLASSES (" +
                "$COLUMN_CLASS_ID TEXT PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_IMAGE_URL TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_DATE TEXT, " +
                "$COLUMN_TIME TEXT, " +
                "$COLUMN_CAPACITY INTEGER, " +
                "$COLUMN_CLASS_TYPE TEXT, " +
                "$COLUMN_PRICE REAL, " +
                "$COLUMN_STATUS TEXT)")

        // Create Instance table
        db.execSQL("CREATE TABLE $TABLE_INSTANCE (" +
                "$COLUMN_INSTANCE_ID TEXT PRIMARY KEY, " +
                "$COLUMN_CLASS_ID TEXT, " +
                "$COLUMN_INSTANCE_DATE TEXT, " +
                "$COLUMN_INSTRUCTOR TEXT, " +
                "$COLUMN_NOTES TEXT)")

        // Create Booking table
        db.execSQL("CREATE TABLE $TABLE_BOOKING (" +
                "$COLUMN_BOOKING_ID TEXT PRIMARY KEY, " +
                "$COLUMN_INSTANCE_ID TEXT, " +
                "$COLUMN_USER_EMAIL TEXT, " +
                "$COLUMN_BOOKING_DATE TEXT, " +
                "$COLUMN_STATUS TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLASSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_INSTANCE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKING")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "YogaClassAdmin.db"

        const val TABLE_USER = "User"
        const val COLUMN_USER_ID = "userId"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_NAME = "name"
        const val COLUMN_ROLE = "role"
        const val COLUMN_SPECIALIZATION = "specialization"
        const val COLUMN_CONTACT = "contact"

        const val TABLE_CLASSES = "Classes"
        const val COLUMN_CLASS_ID = "classId"
        const val COLUMN_TITLE = "title"
        const val COLUMN_IMAGE_URL = "imageUrl"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_CAPACITY = "capacity"
        const val COLUMN_CLASS_TYPE = "classType"
        const val COLUMN_PRICE = "price"
        const val COLUMN_STATUS = "status"

        const val TABLE_INSTANCE = "Instance"
        const val COLUMN_INSTANCE_ID = "instanceId"
        const val COLUMN_INSTANCE_DATE = "instanceDate"
        const val COLUMN_INSTRUCTOR = "instructor"
        const val COLUMN_NOTES = "notes"

        const val TABLE_BOOKING = "Booking"
        const val COLUMN_BOOKING_ID = "bookingId"
        const val COLUMN_USER_EMAIL = "userEmail"
        const val COLUMN_BOOKING_DATE = "bookingDate"
    }
}