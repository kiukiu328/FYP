# import json
#
# from flask import Flask, request
#
# app = Flask(__name__)
#
# voltage = 0
#
#
# @app.route('/get')
# def get():
#     t = {
#         'voltage': voltage
#     }
#     return json.dumps(t)
#
#
# @app.route('/setVoltage', methods=['GET'])
# def setVoltage():
#     global voltage
#     voltage = request.args['voltage']
#     return 'voltage set'
#
#
# if __name__ == '__main__':
#     app.run(host='0.0.0.0')

