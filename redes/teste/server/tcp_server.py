import socket
import threading
import json
import os

SERVER_IP = "0.0.0.0"
TCP_PORT = 12347
UDP_PORT = 12345  # Para comunicar com o servidor principal
BUFFER_SIZE = 4096

# ALTERAÇÃO: Mudar para pasta Downloads no diretório home
SAVE_DIR = os.path.expanduser("~/Downloads")

if not os.path.exists(SAVE_DIR):
    os.makedirs(SAVE_DIR)

def notify_main_server(sender, recipient, filename):
    """Notifica o servidor principal sobre o ficheiro recebido"""
    try:
        udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        notification = {
            "type": "file_received",
            "from": sender,
            "to": recipient,
            "filename": filename
        }
        udp_socket.sendto(json.dumps(notification).encode(), ("127.0.0.1", UDP_PORT))
        udp_socket.close()
    except Exception as e:
        print(f"[Erro] Falha ao notificar servidor principal: {e}")

def handle_file_transfer(conn, addr):
    try:
        # Receber header
        header_data = conn.recv(1024).decode().strip()
        header = json.loads(header_data)

        filename = header["filename"]
        sender = header["from"]
        recipient = header["to"]

        # Criar nome único para o ficheiro
        timestamp = int(time.time())
        safe_filename = f"{recipient}_{timestamp}_{filename}"
        filepath = os.path.join(SAVE_DIR, safe_filename)

        print(f"[TCP] A receber '{filename}' de {sender} para {recipient}...")
        print(f"[TCP] A guardar em: {filepath}")

        # Receber dados do ficheiro
        with open(filepath, "wb") as f:
            total_received = 0
            while True:
                data = conn.recv(BUFFER_SIZE)
                if not data:
                    break
                f.write(data)
                total_received += len(data)

        print(f"[TCP] Ficheiro guardado como {safe_filename} ({total_received} bytes)")
        
        # Notificar o servidor principal
        notify_main_server(sender, recipient, safe_filename)
        
        # Enviar confirmação ao cliente
        confirmation = {"status": "success", "message": "Ficheiro recebido com sucesso"}
        conn.send(json.dumps(confirmation).encode())

    except Exception as e:
        print(f"[Erro] no envio de ficheiro: {e}")
        try:
            error_response = {"status": "error", "message": str(e)}
            conn.send(json.dumps(error_response).encode())
        except:
            pass
    finally:
        conn.close()

def start_tcp_server():
    tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    tcp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    tcp_socket.bind((SERVER_IP, TCP_PORT))
    tcp_socket.listen(5)
    print(f"[TCP] Servidor de ficheiros a ouvir em {SERVER_IP}:{TCP_PORT}")
    print(f"[TCP] Ficheiros serão guardados em: {SAVE_DIR}")

    try:
        while True:
            conn, addr = tcp_socket.accept()
            print(f"[TCP] Nova conexão de {addr}")
            threading.Thread(target=handle_file_transfer, args=(conn, addr), daemon=True).start()
    except KeyboardInterrupt:
        print("\n[TCP] Servidor a parar...")
    finally:
        tcp_socket.close()

if __name__ == "__main__":
    import time
    start_tcp_server()