from service import CrossPlatformService
from service.ttypes import InvalidOperationException, CrossPlatformResource
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol


def main():
    # Make socket with Buffering
    transport = TSocket.TSocket('localhost', 9090)
    transport = TTransport.TBufferedTransport(transport)

    # Wrap in a protocol
    protocol = TBinaryProtocol.TBinaryProtocol(transport)

    # Create a client to use the protocol encoder
    client = CrossPlatformService.Client(protocol)

    # Connect!
    transport.open()

    try:
        rr = client.ping()
        print('result: %s' % rr)
        cpr = client.get(1)
        print('cpr get result: %s' % cpr)
    except InvalidOperation as e:
        print('InvalidOperation: %r' % e)

    # Close!
    transport.close()


if __name__ == '__main__':
    try:
        main()
    except Thrift.TException as tx:
        print('%s' % tx.message)
