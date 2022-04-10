import pymysql
from datetime import datetime


def updateVideoState(fileName,state):
    db = pymysql.connect(
        host="127.0.0.1",
        port=3306,
        user="root",
        passwd="",
        db="fyp_project"
    )
    cursor = db.cursor()
    sql_format = "update alert_record set video_state = {video_state} where VideoPath= \"{VideoPath}\";"
    sql = sql_format.format(video_state=state,VideoPath=fileName)

    cursor.execute(sql)
    db.commit()
    cursor.close()
    db.close()