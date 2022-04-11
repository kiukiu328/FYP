import socket

def get_WLAN_address():
    return socket.gethostbyname(socket.gethostname())
# print(get_WLAN_address())