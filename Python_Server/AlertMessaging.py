import firebase_admin
from firebase_admin import credentials, messaging
from firebase_admin import db

try:
    firebase_admin.get_app()
except ValueError as e:
    cred = credentials.Certificate("./src/serviceAccountKey2.json")
    # firebase_admin.initialize_app(cred, {"databaseURL": 'https://test2-e7bfd-default-rtdb.firebaseio.com/'},
    #                               name="alert_message")
    firebase_admin.initialize_app(cred, {"databaseURL": 'https://fyp2021-cbba2-default-rtdb.firebaseio.com'},
                                  name="alert_message")

title = None
body = None


def send():
    global title, body

    ##获取token
    ref = db.reference('AndroidToken')
    registration_token = ref.get()

    message = messaging.Message(
        android=messaging.AndroidConfig(
            priority="high",
            data={
                "title": title,
                'body': body,
            }
        )
        ,
        token=registration_token
    )

    # Send a message to the device corresponding to the provided
    # registration token.
    response = messaging.send(message, False)

    # Response is a message ID string.
    print('Successfully sent message:',registration_token, response)


def messaging_send(t="default_title", b="default_body"):
    global title, body
    title = t
    body = b
    send()
