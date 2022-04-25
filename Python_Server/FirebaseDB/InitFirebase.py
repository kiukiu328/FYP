import firebase_admin
from firebase_admin import credentials

def init():
    try:
        firebase_admin.get_app()
    except ValueError:
        cred = credentials.Certificate("../src/serviceAccountKey.json")
        # firebase_admin.initialize_app(cred, {"databaseURL": 'https://test2-e7bfd-default-rtdb.firebaseio.com/'})
        firebase_admin.initialize_app(cred, {"databaseURL": 'https://fyp2021-cbba2-default-rtdb.firebaseio.com'})