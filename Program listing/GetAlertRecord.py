import pymysql
from datetime import datetime
import json
def getAlertReocrd():
    db = pymysql.connect(
        host="127.0.0.1",
        port=3306,
        user="root",
        passwd="",
        db="fyp_project"
    )

    cursor = db.cursor()

    sql = "SELECT * FROM alert_record ORDER BY alertRecord_time DESC;"

    # print(sql)
    cursor.execute(sql)
    data_list = cursor.fetchall()
    cursor.close()
    db.close()
    return data_list


def getDataDict():
    data = getAlertReocrd()
    dict_data = {"record_index":[],
                 "record_date":[],
                 "video_path":[],
                 "icon_path":[],
                 "video_state":[]}
    for i in data:
        dict_data["record_index"].append(str(i[0]))
        dict_data["record_date"].append(str(i[1]))
        dict_data["video_path"].append(str(i[2]))
        dict_data["icon_path"].append(str(i[3]))
        dict_data["video_state"].append(i[4]==1)
    return dict_data

def getDataJson():
    dict_data=getDataDict()
    json_data = json.dumps(dict_data)
    return json_data
