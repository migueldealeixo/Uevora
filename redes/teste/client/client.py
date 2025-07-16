import socket
import json
import threading
import sys
import termios
import tty
import os
import getpass
import time

SERVER_IP = "127.0.0.1"
SERVER_PORT = 12345
TCP_PORT = 12347
server_address = (SERVER_IP, SERVER_PORT)

def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

def display_welcome():
    clear_screen()
    print("\n" + "="*50)
    print("Bem-Vindo ao Sistema de Chat".center(50))
    print("="*50 + "\n")
    print("1. Login")
    print("2. Registar")
    print("3. Sair\n")

def display_login_header():
    clear_screen()
    print("\n" + "="*50)
    print("ÁREA DE LOGIN".center(50))
    print("="*50 + "\n")

def display_register_header():
    clear_screen()
    print("\n" + "="*50)
    print("ÁREA DE REGISTO".center(50))
    print("="*50 + "\n")

def get_auth_choice():
    while True:
        try:
            choice = int(input("Escolha uma opção (1-3): "))
            if 1 <= choice <= 3:
                return choice
            print("Opção inválida. Tente novamente.")
        except ValueError:
            print("Por favor, insira um número.")

def get_credentials(auth_type):
    if auth_type == 1:  # Login
        display_login_header()
    else:  # Register
        display_register_header()
    
    username = input("Username: ")
    password = getpass.getpass("Password: ")
    return username, password

def authenticate():
    while True:
        display_welcome()
        choice = get_auth_choice()

        if choice == 3:
            print("\nA sair...")
            sys.exit()

        username, password = get_credentials(choice)

        client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

        auth_msg = {
            "type": "auth" if choice == 1 else "register",
            "username": username,
            "password": password
        }

        client_socket.sendto(json.dumps(auth_msg).encode(), server_address)

        try:
            client_socket.settimeout(5)
            data, _ = client_socket.recvfrom(1024)
            response = json.loads(data.decode())
            
            if response.get("type") == "error":
                print(f"\n[ERRO] {response['message']}")
                input("Pressione Enter para tentar novamente...")
                continue
            elif response.get("type") == "info" and response.get("message") == "AUTH_SUCCESS":
                clear_screen()
                print(f"\n{'='*50}")
                print("CHAT ATIVO".center(50))
                print(f"Utilizador: {username}")
                print(f"Modo: Público")
                print("Comandos: /priv, /create_group, /gmode, /sendfile, /public, /exit")
                print(f"{'='*50}\n")
                return client_socket, username
            elif response.get("type") == "info" and response.get("message") == "REGISTER_SUCCESS":
                print("\n[SUCESSO] Registo completo. Por favor faça login.")
                input("Pressione Enter para continuar...")
                continue
            else:
                print("\n[ERRO] Resposta inesperada do servidor")
                input("Pressione Enter para tentar novamente...")
                continue

        except socket.timeout:
            print("\n[ERRO] O servidor não respondeu")
            input("Pressione Enter para tentar novamente...")
            continue
        finally:
            client_socket.settimeout(None)

current_mode = "public"  
current_recipient = None
msg_counter = 0
my_message_buffer = ""



def heartbeat():
    while True:
        time.sleep(5)  
        try:
            beat_msg = {"type": "heartbeat", "username": username}
            client_socket.sendto(json.dumps(beat_msg).encode(), server_address)
        except:
            pass

threading.Thread(target=heartbeat, daemon=True).start()
def receive():
    partial_msgs = {}
    current_sender = None
    
    while True:
        try:
            data, _ = client_socket.recvfrom(1024)
            msg = json.loads(data.decode())

            if msg["type"] == "char":
                sender = msg["username"]
                char = msg["char"]

                if sender == username:
                    continue

                if current_sender != sender:
                    if current_sender and current_sender in partial_msgs and partial_msgs[current_sender]:
                        partial_msgs[current_sender] = ""
                    
                    current_sender = sender
                    sys.stdout.write(f"\n{sender}> ")
                    partial_msgs[sender] = ""

                if sender not in partial_msgs:
                    partial_msgs[sender] = ""

                if ord(char) in (10, 13):
                    partial_msgs[sender] = ""
                    current_sender = None
                    sys.stdout.write(f"\n{username}> {my_message_buffer}")
                elif ord(char) in (127, 8):
                    if partial_msgs[sender]:
                        partial_msgs[sender] = partial_msgs[sender][:-1]
                        sys.stdout.write('\b \b')
                else:
                    partial_msgs[sender] += char
                    sys.stdout.write(char)
                
                sys.stdout.flush()

            elif msg["type"] == "presence":
                print(f'\n\n*** {msg["username"]} {msg["event"]} ***')
                current_sender = None
                sys.stdout.write(f"\n{username}> {my_message_buffer}")
                sys.stdout.flush()

            elif msg["type"] == "file_notification":
                print(f'\n\n[FICHEIRO] {msg["message"]}')
                current_sender = None
                sys.stdout.write(f"\n{username}> {my_message_buffer}")
                sys.stdout.flush()

            elif msg["type"] == "error":
                print(f"\n\n[Erro] {msg['message']}")
                current_sender = None
                sys.stdout.write(f"\n{username}> {my_message_buffer}")
                sys.stdout.flush()

            elif msg["type"] == "info":
                print(f'\n\n[Info] {msg["message"]}')
                current_sender = None
                sys.stdout.write(f"\n{username}> {my_message_buffer}")
                sys.stdout.flush()

        except Exception as e:
            print(f"\n[Erro] na receção: {e}")
            break

def send_file(recipient, filepath):
    try:
        if not os.path.isfile(filepath):
            print(f"[Erro] Ficheiro '{filepath}' não existe.")
            return

        filename = os.path.basename(filepath)
        file_size = os.path.getsize(filepath)
        
        start_msg = {
            "type": "file_start",
            "to": recipient,
            "filename": filename
        }
        client_socket.sendto(json.dumps(start_msg).encode(), server_address)
        
        print(f"[Info] A enviar '{filename}' ({file_size} bytes) para {recipient}...")
        
        tcp_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        tcp_socket.settimeout(30)  # Timeout de 30 segundos
        tcp_socket.connect((SERVER_IP, TCP_PORT))

        header = {
            "type": "file",
            "filename": filename,
            "from": username,
            "to": recipient
        }
        
        header_data = json.dumps(header).encode()
        tcp_socket.send(header_data.ljust(1024))

        with open(filepath, "rb") as f:
            bytes_sent = 0
            while True:
                chunk = f.read(4096)
                if not chunk:
                    break
                tcp_socket.sendall(chunk)
                bytes_sent += len(chunk)
                
                if file_size > 10000:  
                    progress = (bytes_sent / file_size) * 100
                    print(f"\r[Info] Progresso: {progress:.1f}% ({bytes_sent}/{file_size} bytes)", end='', flush=True)

        try:
            tcp_socket.settimeout(5)
            response = tcp_socket.recv(1024).decode()
            result = json.loads(response)
            
            if result.get("status") == "success":
                print(f"\n[Sucesso] '{filename}' enviado com sucesso para {recipient}!")
            else:
                print(f"\n[Erro] Falha no envio: {result.get('message', 'Erro desconhecido')}")
                
        except socket.timeout:
            print(f"\n[Aviso] '{filename}' enviado, mas sem confirmação do servidor.")
        except Exception as e:
            print(f"\n[Aviso] '{filename}' enviado, mas erro na confirmação: {e}")

        tcp_socket.close()

    except Exception as e:
        print(f"\n[Erro] Falha no envio do ficheiro: {e}")

def send():
    global current_mode, current_recipient, msg_counter, my_message_buffer

    try:
        old_settings = termios.tcgetattr(sys.stdin)
        tty.setcbreak(sys.stdin)

        sys.stdout.write(f'{username}> ')
        sys.stdout.flush()

        while True:
            c = sys.stdin.read(1)
            if not c:
                continue

            if c == '/':
                termios.tcsetattr(sys.stdin, termios.TCSADRAIN, old_settings)
                print("\n/", end='', flush=True)
                command = input().strip()
                
                if command.startswith("priv "):
                    try:
                        _, recipient = command.split(" ", 1)
                        current_mode = "private"
                        current_recipient = recipient
                        print(f"\n*** Modo privado para {recipient} ***")
                    except:
                        print("[Erro] Uso: /priv <utilizador>")
                
                elif command.startswith("create_group "):
                    try:
                        parts = command.split()
                        group_name = parts[1]
                        members = parts[2:]  
                        
                        create_group_msg = {
                            "type": "group_create",
                            "group": group_name,
                            "members": members
                        }
                        client_socket.sendto(json.dumps(create_group_msg).encode(), server_address)
                        print(f"Pedido de criação do grupo '{group_name}' enviado.")
                    except:
                        print("[Erro] Uso: /create_group <nome> <membro1> <membro2> ...")
                

                elif command.startswith("remove_member "):
                    try:
                        _, group, member = command.split(" ", 2)
                        remove_msg = {
                            "type": "group_remove",
                            "group": group,
                            "member": member
                        }
                        client_socket.sendto(json.dumps(remove_msg).encode(), server_address)
                        print(f"[Info] Pedido para remover '{member}' do grupo '{group}' enviado.")
                    except:
                        print("[Erro] Uso: /remove_member <grupo> <utilizador>")

                elif command.startswith("delete_group "):
                    try:
                        _, group = command.split(" ", 1)
                        delete_msg = {
                            "type": "group_delete",
                            "group": group
                        }
                        client_socket.sendto(json.dumps(delete_msg).encode(), server_address)
                        print(f"[Info] Pedido para apagar o grupo '{group}' enviado.")
                    except:
                        print("[Erro] Uso: /delete_group <grupo>")

                elif command.startswith("gmode "):
                    try:
                        _, group = command.split(" ", 1)
                        current_mode = "group"
                        current_recipient = group
                        print(f"*** Modo grupo: {group} ***")
                    except:
                        print("[Erro] Uso: /gmode <grupo>")
                
                elif command.startswith("sendfile "):
                    try:
                        parts = command.split(" ", 2)
                        if len(parts) != 3:
                            raise ValueError("Argumentos insuficientes")
                        _, recipient, filepath = parts
                        
                        filepath = os.path.expanduser(filepath)
                        send_file(recipient, filepath)
                    except ValueError:
                        print("[Erro] Uso: /sendfile <utilizador> <caminho_para_ficheiro>")
                    except Exception as e:
                        print(f"[Erro] {e}")
                # Adicionar novo comando
                elif command.startswith("accept_file "):
                    try:
                        _, sender, filename = command.split(" ", 2)
                        accept_msg = {
                            "type": "file_accept",
                            "from": sender,
                            "filename": filename
                        }
                        client_socket.sendto(json.dumps(accept_msg).encode(), server_address)
                        print(f"[Info] Aceitando ficheiro '{filename}' de {sender}")
                    except:
                        print("[Erro] Uso: /accept_file <remetente> <nome_ficheiro>")
                elif command.startswith("add_member "):
                    try:
                        parts = command.split(" ", 2)
                        group = parts[1]
                        member = parts[2]
                        add_msg = {
                            "type": "group_add",
                            "group": group,
                            "member": member
                        }
                        client_socket.sendto(json.dumps(add_msg).encode(), server_address)
                        print(f"[Info] Membro '{member}' adicionado ao grupo '{group}'")
                    except:
                        print("[Erro] Uso: /add_member <grupo> <membro>")

                elif msg["type"] == "file_available":
                    print(f'\n\n[FICHEIRO] {msg["message"]}')
                    print(f"Use /accept_file {msg['from']} {msg['filename']} para receber")
                    current_sender = None
                    sys.stdout.write(f"\n{username}> {my_message_buffer}")
                    sys.stdout.flush()



                elif command == "public":
                    current_mode = "public"
                    current_recipient = None
                    my_message_buffer = ""
                    print("*** Modo público ***")
                
                elif command == "exit":
                    exit_msg = {"type": "exit", "username": username}
                    client_socket.sendto(json.dumps(exit_msg).encode(), server_address)
                    print("A sair...")
                    sys.exit()
                
                else:
                    print("Comandos disponíveis:")
                    print("  priv <utilizador>     - Mensagem privada")
                    print("  create_group <nome> <membros> - Criar grupo")
                    print("  remove_member <nome> <membro> - Remover Membro")
                    print("  delete_group <nome>    -Apagar grupo")
                    print("  gmode <grupo>         - Mensagem de grupo")
                    print("  sendfile <user> <ficheiro> - Enviar ficheiro")
                    print("  public                - Voltar ao modo público")
                    print("  exit                  - Sair do aplicativo")
                
                sys.stdout.write(f"{username}> {my_message_buffer}")
                sys.stdout.flush()
                tty.setcbreak(sys.stdin)
                continue

            if current_mode == "private" and current_recipient:
                private_msg = {
                    "type": "private_msg",
                    "to": current_recipient,
                    "msg": c,
                    "username": username
                }
                client_socket.sendto(json.dumps(private_msg).encode(), server_address)
                if ord(c) in (10, 13):
                    sys.stdout.write(f"\n{username}> ")
                elif ord(c) in (8, 127):
                    sys.stdout.write('\b \b')
                else:
                    sys.stdout.write(c)
                sys.stdout.flush()
            
            elif current_mode == "group" and current_recipient:
                group_msg = {
                    "type": "group_msg",
                    "group": current_recipient,
                    "char": c,
                    "username": username
                }
                client_socket.sendto(json.dumps(group_msg).encode(), server_address)
                if ord(c) in (10, 13):
                    sys.stdout.write(f"\n{username}> ")
                elif ord(c) in (8, 127):
                    sys.stdout.write('\b \b')
                else:
                    sys.stdout.write(c)
                sys.stdout.flush()
            
            elif current_mode == "public":
                msg_counter += 1
                char_msg = {"type": "char", "char": c, "username": username, "counter": msg_counter}
                client_socket.sendto(json.dumps(char_msg).encode(), server_address)
                
                if ord(c) in (10, 13):
                    my_message_buffer = ""
                    sys.stdout.write(f"\n{username}> ")
                elif ord(c) in (8, 127):
                    if my_message_buffer:
                        my_message_buffer = my_message_buffer[:-1]
                        sys.stdout.write('\b \b')
                else:
                    my_message_buffer += c
                    sys.stdout.write(c)
                sys.stdout.flush()

    except KeyboardInterrupt:
        exit_msg = {"type": "exit", "username": username}
        client_socket.sendto(json.dumps(exit_msg).encode(), server_address)
        client_socket.close()
        print("\n\nSaiu.")
        sys.exit()

    finally:
        termios.tcsetattr(sys.stdin, termios.TCSADRAIN, old_settings)

client_socket, username = authenticate()
threading.Thread(target=receive, daemon=True).start()
send()

