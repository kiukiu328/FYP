import requests

serverIP = '192.168.1.171'
printed = False


def sendAlert(id):
    global printed
    url = f'http://{serverIP}:5000/locationAlert'
    myobj = {'id': id}
    x = requests.post(url, data=myobj)
    if not printed:
        print(x.text)
        printed = True
    printed = (x.text == 'Timer not yet')


def sendToken(id, token):
    url = f'http://{serverIP}:5000/sendToken'
    myobj = {'id': id, 'token': token}
    x = requests.post(url, data=myobj)
    print(x.text)


if __name__ == '__main__':
    sendAlert('250011f40e21833f')
