import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

if not firebase_admin._apps:
    cred = credentials.Certificate("../src/serviceAccountKey.json")
    firebase_admin.initialize_app(cred, {"databaseURL": 'https://test2-e7bfd-default-rtdb.firebaseio.com/'},name="update_address")
def save_network_info(address):
    ref = db.reference()

    ref.update({
        "ip_address":address
    })
