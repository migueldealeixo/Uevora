SRC_DIR = /home/miguel/uni/4ano/sd/sd-t01-51653-52215
BUILD_DIR = build
JAVAC = javac
JAVA = java
CLASSPATH = resources/postgresql.jar
RMI_PORT = 9000
DB_NAME = trabalho1
DB_USER = user1
DB_PASS = umaPas

all: compile

compile:
	$(JAVAC) -cp $(CLASSPATH) -d $(BUILD_DIR) $(SRC_DIR)/*.java

clean:
	rm -rf $(BUILD_DIR)

rmi:
	rmiregistry -J-classpath -J$(BUILD_DIR) $(RMI_PORT)

server:
	$(JAVA) -cp $(BUILD_DIR):$(CLASSPATH) -Djdbc.drivers=org.postgresql.Driver servidor $(RMI_PORT) localhost $(DB_NAME) $(DB_USER) $(DB_PASS)

client:
	$(JAVA) -cp $(BUILD_DIR):$(CLASSPATH) clienteServer localhost $(RMI_PORT)

admin:
	$(JAVA) -cp $(BUILD_DIR):$(CLASSPATH) clienteAdmin localhost $(RMI_PORT)

.PHONY: all compile clean rmi server client admin
