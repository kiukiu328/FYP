import pymysql
from datetime import datetime

def save_video(file):
    dt = datetime.now()
    StartTime = dt.strftime('%Y-%m-%d %H:%M:%S')
    db = pymysql.connect(
        host="127.0.0.1",
        port=3306,
        user="root",
        passwd="",
        db="fyp_project"
    )

    cursor = db.cursor()
    sql_format = "insert into alert_record(alertRecord_time,VideoPath,RecordIcon,video_state) values(\"{record_time}\",\"{videoPath}\",\"{IconPath}\",{video_state})"
    sql = sql_format.format(record_time=StartTime,videoPath=file,IconPath=file,video_state=0)
    print(sql)
    cursor.execute(sql)
    db.commit()
    cursor.close()
    db.close()
