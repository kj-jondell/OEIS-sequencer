#TODO: send list as is, do mod etc in supercollider

from pythonosc import udp_client
import argparse
import pyoeis
import random

#Random OEIS sequence
oeis_client = pyoeis.OEISClient()
msg = oeis_client.get_by_id('a{}'.format(random.randint(0,10000)))
unsigned_list = msg.unsigned_list
unsigned_list = [x%2147483647 for x in unsigned_list]
print("{}: \n{}".format(msg.name, unsigned_list))

#Setting up network things
parser = argparse.ArgumentParser()
parser.add_argument("--ip", default="127.0.0.1")
parser.add_argument("--port", default=57120)
args = parser.parse_args()

#UDP client
client = udp_client.SimpleUDPClient(args.ip, args.port)
client.send_message("/degree", unsigned_list)
