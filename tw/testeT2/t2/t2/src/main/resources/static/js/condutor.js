document.getElementById("showPedidosButton").addEventListener("click", function() {
    var pedidosLista = document.getElementById("pedidosLista");
    if (pedidosLista.style.display === "none") {
        pedidosLista.style.display = "block";
    } else {
        pedidosLista.style.display = "none";
    }
});

document.getElementById("submitRota").addEventListener("click", function(event) {
    event.preventDefault();

    var origem = document.getElementById("origem").value;
    var destino = document.getElementById("destino").value;
    var lugaresDisponiveis = document.getElementById("lugaresDisponiveis").value;
    var dataPedido = document.getElementById("dataPedido").value;

    if (!origem || !destino || !lugaresDisponiveis || !dataPedido) {
        var messageElement = document.getElementById("message");
        messageElement.innerHTML = "Todos os campos são obrigatórios!";
        messageElement.style.color = "red";
        messageElement.style.display = "block";
        return;
    }

    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/condutor", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            var messageElement = document.getElementById("message");
            if (xhr.status === 200) {
                messageElement.innerHTML = "Rota registrada com sucesso!";
                messageElement.style.color = "green";
            } else if (xhr.status === 403) {
                messageElement.innerHTML = "Erro: Você não tem permissão para realizar esta ação.";
                messageElement.style.color = "red";
            } else {
                messageElement.innerHTML = "Erro ao registrar a rota. Por favor, tente novamente.";
                messageElement.style.color = "red";
            }
            messageElement.style.display = "block";
        }
    };

    // Send the form data
    var params = `origem=${encodeURIComponent(origem)}&destino=${encodeURIComponent(destino)}&lugaresDisponiveis=${encodeURIComponent(lugaresDisponiveis)}&dataPedido=${encodeURIComponent(dataPedido)}`;
    xhr.send(params);
});
