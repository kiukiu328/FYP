package com.fyp.evhelper.reminder

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import java.lang.Exception

import com.fyp.evhelper.MainActivity
import com.fyp.evhelper.R

class ReminderMainPage : Fragment() {
    lateinit var vehLicCv: CardView
    lateinit var driLicCv:CardView
    lateinit var eleVehMaiCv:CardView
    lateinit var ctcCv:CardView
    lateinit var mainPageBtn: Button
    lateinit var dropTableBtn:Button
    lateinit var db: SQLiteDatabase
    lateinit var sql: String
    lateinit var cursor: Cursor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var v = inflater.inflate(R.layout.fragment_reminder_main_page, container, false)

        (getActivity() as AppCompatActivity).getSupportActionBar()?.hide()
        driLicCv = v.findViewById<CardView>(R.id.driLicCv)
        vehLicCv = v.findViewById<CardView>(R.id.vehLicCv)
        dropTableBtn = v.findViewById<Button>(R.id.dropTableBtn)
        eleVehMaiCv = v.findViewById<CardView>(R.id.eleVehMaiCv)
        mainPageBtn = v.findViewById<Button>(R.id.mainPageBtn)
        ctcCv = v.findViewById<CardView>(R.id.ctcCv)
        val click_sound: MediaPlayer = MediaPlayer.create(context,R.raw.click_sound)

        db = SQLiteDatabase.openDatabase(
            "/data/data/com.fyp.evhelper/LicenceDB",
            null,
            SQLiteDatabase.CREATE_IF_NECESSARY
        )

        //driving licence reminder page a,a\\

        //driving licence reminder page a,a\\
        driLicCv.setOnClickListener {
            try {
                click_sound.start()
                db = SQLiteDatabase.openDatabase(
                    "/data/data/com.fyp.evhelper/LicenceDB",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
                cursor = db.rawQuery("SELECT * FROM DrivingLicence WHERE driLicDataNo = 1", null)
                val intent = Intent(context, DriLicConfirm::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                //Toast.makeText(MainActivity.this,"test, no data",Toast.LENGTH_LONG).show();
                val intent = Intent(context, DriLicAddData::class.java)
                startActivity(intent)
            }
        }
        //a,a end\\

        //top bar b,b\\
        //a,a end\\

        //top bar b,b\\
        mainPageBtn.setOnClickListener {
            MainActivity.homePage()
        }
        //b,b end\\
        //vehicle licence reminder page c,c\\
        //b,b end\\
        //vehicle licence reminder page c,c\\
        vehLicCv.setOnClickListener {
            click_sound.start()
            try {
                db = SQLiteDatabase.openDatabase(
                    "/data/data/com.fyp.evhelper/LicenceDB",
                    null,
                    SQLiteDatabase.OPEN_READWRITE
                )
                cursor = db.rawQuery("SELECT * FROM VehicleLicence WHERE vehLicDataNo = 1", null)
                val intent = Intent(context, VehLicConfirm::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                //Toast.makeText(MainActivity.this,"test, no data",Toast.LENGTH_LONG).show();
                val intent = Intent(context, VehLicAddData::class.java)
                startActivity(intent)
            }
        }
        //c,c end\\
        //drop table (for testing) d,d\\
        //c,c end\\
        //drop table (for testing) d,d\\
        dropTableBtn.setOnClickListener {
            click_sound.start()
            try {
                db = SQLiteDatabase.openDatabase(
                    "/data/data/com.fyp.evhelper/LicenceDB",
                    null,
                    SQLiteDatabase.CREATE_IF_NECESSARY
                )
                sql = "DROP TABLE IF EXISTS DrivingLicence;"
                db.execSQL(sql)
                sql = "DROP TABLE IF EXISTS VehicleLicence;"
                db.execSQL(sql)
                sql = "DROP TABLE IF EXISTS DCTC;"
                db.execSQL(sql)
                sql = "DROP TABLE IF EXISTS EVM;"
                db.execSQL(sql)
                Toast.makeText(context, "Data Cleared", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "error", Toast.LENGTH_LONG).show()
            }
        }
        //d,d end\\
        //electric vehicle maintenance reminder page e,e\\
        //d,d end\\
        //electric vehicle maintenance reminder page e,e\\
        eleVehMaiCv.setOnClickListener {
            click_sound.start()
            val intent = Intent(context, EleVehMai::class.java)
            startActivity(intent)
        }
        //e,e end\\
        //annual Vehicle Examination reminder page f,f\\
        //e,e end\\
        //annual Vehicle Examination reminder page f,f\\
        ctcCv.setOnClickListener {
            click_sound.start()
            val intent = Intent(context, CTCConfirm::class.java)
            startActivity(intent)
        }
        return v
    }


}