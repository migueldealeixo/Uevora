import socket
import threading
import json
import os
import hashlib
import time

HEARTBEAT_INTERVAL = 10 
client_last_seen = {}  
client_counters = {}

SERVER_IP = "0.0.0.0"
SERVER_PORT = 12345
clients = {}
users_file = "users.json"
groups_file = "groups.json"

def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()

def send_error(addr, message):
    error_msg = {"type": "error", "message": message}
    server_socket.sendto(json.dumps(error_msg).encode(), addr)

def send_info(addr, message):
    info_msg = {"type": "info", "message": message}
    server_socket.sendto(json.dumps(info_msg).encode(), addr)

def broadcast(msg, exclude=None):
    for client_addr in clients:
        if client_addr != exclude:
            server_socket.sendto(json.dumps(msg).encode(), client_addr)

def send_to_user(username, message):
    """Envia mensagem para um utilizador específico"""
    for addr, user in clients.items():
        if user == username:
            server_socket.sendto(json.dumps(message).encode(), addr)
            return True
    return False

def handle_client(data, addr):
    global client_last_seen
    client_last_seen[addr] = time.time()
    try:
        msg = json.loads(data.decode())
        msg_type = msg.get("type")

        if msg_type in ["auth", "register"]:
            username = msg.get("username")
            password = msg.get("password")
            hashed = hash_password(password)

            if msg_type == "auth":
                if username not in users:
                    send_error(addr, "Utilizador não existe.")
                    return
                if users[username] != hashed:
                    send_error(addr, "Password incorreta.")
                    return
                
                clients[addr] = username
                print(f"[+] {username} entrou ({addr})")
                broadcast({"type": "presence", "event": "entrou no chat", "username": username}, exclude=addr)
                send_info(addr, "AUTH_SUCCESS")
                
            else:  # register
                if username in users:
                    send_error(addr, "Utilizador já existe.")
                    return
                    
                users[username] = hashed
                save_users()
                send_info(addr, "REGISTER_SUCCESS")

        elif msg_type == "char" and addr in clients:
            username = clients.get(addr)
            counter = msg.get("counter", 0)

            if username:
                last = client_counters.get(username, 0)
                if counter != last + 1:
                    print(f"[!] Possível perda de mensagens de {username}: esperava {last+1}, recebeu {counter}")
                client_counters[username] = counter

            broadcast(msg, exclude=addr)

        elif msg_type == "exit":
            username = clients.pop(addr, "???")
            print(f"[-] {username} saiu ({addr})")
            broadcast({"type": "presence", "event": "saiu do chat", "username": username})

        elif msg_type == "private_msg":
            sender = clients.get(addr)
            recipient = msg.get("to")
            text = msg.get("msg")

            recipient_addr = None
            for c_addr, uname in clients.items():
                if uname == recipient:
                    recipient_addr = c_addr
                    break

            if recipient_addr:
                private_msg = {"type": "char", "char": text, "username": f"[PRIVADO] {sender}"}
                server_socket.sendto(json.dumps(private_msg).encode(), recipient_addr)
            else:
                send_error(addr, f"Utilizador '{recipient}' não encontrado.")

        elif msg_type == "group_create":
            group = msg["group"]
            username = clients[addr]
            members = msg.get("members", [])
            
            if username not in members:
                members.append(username)

            if group in groups:
                send_error(addr, "Grupo já existe.")
                return

            groups[group] = {"owner": username, "members": members}
            save_groups()
            send_info(addr, f"Grupo '{group}' criado.")

        elif msg_type == "group_add":
            group = msg["group"]
            member = msg["member"]
            username = clients[addr]

            if group not in groups:
                send_error(addr, "Grupo não existe.")
                return

            if groups[group]["owner"] != username:
                send_error(addr, "Só o criador pode adicionar membros.")
                return

            if member not in groups[group]["members"]:
                groups[group]["members"].append(member)
                save_groups()
                send_info(addr, f"{member} adicionado a {group}.")

        elif msg_type == "group_msg":
            group = msg["group"]
            char = msg.get("char", "")
            username = clients[addr]

            if group not in groups:
                send_error(addr, "Grupo não existe.")
                return

            if username not in groups[group]["members"]:
                send_error(addr, "Não tens acesso ao grupo.")
                return

            for member in groups[group]["members"]:
                if member == username:
                    continue
                    
                for c_addr, uname in clients.items():
                    if uname == member:
                        group_msg = {
                            "type": "char",
                            "char": char,
                            "username": f"[{group}] {username}"
                        }
                        server_socket.sendto(json.dumps(group_msg).encode(), c_addr)
                        break
        elif msg_type == "group_remove":
            group = msg["group"]
            member = msg["member"]
            username = clients[addr]

            if group not in groups:
                send_error(addr, "Grupo não existe.")
                return

            if groups[group]["owner"] != username:
                send_error(addr, "Só o criador pode remover membros.")
                return

            if member not in groups[group]["members"]:
                send_error(addr, f"{member} não está no grupo.")
                return

            groups[group]["members"].remove(member)
            save_groups()
            send_info(addr, f"{member} removido de {group}.")

            send_to_user(member, {
                "type": "info",
                "message": f"Foste removido do grupo '{group}' por {username}."
            })

        elif msg_type == "group_delete":
            group = msg["group"]
            username = clients[addr]

            if group not in groups:
                send_error(addr, "Grupo não existe.")
                return

            if groups[group]["owner"] != username:
                send_error(addr, "Só o criador pode apagar o grupo.")
                return

            members_to_notify = groups[group]["members"][:]
            del groups[group]
            save_groups()
            send_info(addr, f"Grupo '{group}' apagado com sucesso.")

            for member in members_to_notify:
                if member == username:
                    continue
                send_to_user(member, {
                    "type": "info",
                    "message": f"O grupo '{group}' foi apagado por {username}."
                })

        elif msg_type == "file_start":
            sender = clients.get(addr)
            recipient = msg.get("to")
            filename = msg.get("filename")
            
            if send_to_user(recipient, {
                "type": "file_notification",
                "message": f"{sender} está a enviar o ficheiro '{filename}'...",
                "from": sender,
                "filename": filename
            }):
                send_info(addr, f"A enviar '{filename}' para {recipient}...")
            else:
                send_error(addr, f"Utilizador '{recipient}' não encontrado.")

        elif msg_type == "file_received":
            # Notificação de ficheiro recebido (vem do servidor TCP)
            sender = msg.get("from")
            recipient = msg.get("to")
            filename = msg.get("filename")
            
            if send_to_user(recipient, {
                "type": "file_notification", 
                "message": f"Ficheiro '{filename}' recebido de {sender} e guardado na pasta ~/Downloads",
                "from": sender,
                "filename": filename
            }):
                print(f"[FILE] Notificação enviada: {sender} -> {recipient} ({filename})")

        else:
            if addr in clients:
                send_error(addr, "Ação não reconhecida.")

    except Exception as e:
        print(f"Erro ao processar mensagem de {addr}: {e}")

# Carregamento inicial dos utilizadores
if os.path.exists(users_file):
    with open(users_file, "r") as f:
        users = json.load(f)
else:
    users = {}

def save_users():
    with open(users_file, "w") as f:
        json.dump(users, f)

# Carregamento inicial dos grupos
if os.path.exists(groups_file):
    with open(groups_file, "r") as f:
        groups = json.load(f)
else:
    groups = {}

def save_groups():
    with open(groups_file, "w") as f:
        json.dump(groups, f)

def monitor_clients():
    while True:
        now = time.time()
        for addr in list(clients):
            if now - client_last_seen.get(addr, 0) > 20 * HEARTBEAT_INTERVAL:
                username = clients.pop(addr, "???")
                print(f"[!] Timeout: {username} inativo ({addr})")
                broadcast({"type": "presence", "event": "saiu do chat", "username": username})
                client_last_seen.pop(addr, None)
        time.sleep(HEARTBEAT_INTERVAL)

# Iniciar monitor de clientes
threading.Thread(target=monitor_clients, daemon=True).start()

# Configurar socket principal
server_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
server_socket.bind((SERVER_IP, SERVER_PORT))
print(f"Servidor a correr em {SERVER_IP}:{SERVER_PORT}")

while True:
    data, addr = server_socket.recvfrom(1024)
    threading.Thread(target=handle_client, args=(data, addr), daemon=True).start()