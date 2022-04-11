import firebase_admin
from firebase_admin import credentials, messaging

cred = credentials.Certificate("./src/serviceAccountKeyPolice.json")
firebase_admin.initialize_app(cred)
tokens = ['eGDJVKXbQvW4rUtWAhIto0:APA91bG1SMFo2D8rBkZPQCd-2p__9xEB4m8AJfFxBmS3L8xt0CULHKspgdFeDMGhJYrH9WpI2E99r7xBoERXqIZmdCUAKEbIa_K469GwMPA99chpprdoIpW5ksdMkdvaZ6U6r36pTx1K']


def sendPush(title, msg, registration_token, dataObject=None):
    # See documentation on defining a message payload.
    message = messaging.MulticastMessage(
        notification=messaging.Notification(
            title=title,
            body=msg
        ),
        data=dataObject,
        tokens=registration_token,
    )

    # Send a message to the device corresponding to the provided
    # registration token.
    response = messaging.send_multicast(message)
    # Response is a message ID string.
    print('Successfully sent message:', response)
    print('response.success_count:', response.success_count)


if __name__ == '__main__':
    sendPush('title','python push',tokens)
