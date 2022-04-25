# import socket
#
# def get_WLAN_address():
#     return socket.gethostbyname(socket.gethostname())
# print(get_WLAN_address())


import psutil


def get_WLAN_address():
    wlan_address = ""
    info = psutil.net_if_addrs()
    try:
        wlan = info['WLAN']
    except:
        wlan = info['Wi-Fi']

    for i in wlan:
        for j in i:
            if '192.168' in str(j):
                print(j)
                wlan_address = j

    if (len(wlan_address) != 0):
        print("address init success")
        return wlan_address
    else:
        print("network_none_found")
        return ""
