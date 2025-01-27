document.getElementById("submitPedido").addEventListener("click", function(event) {
    event.preventDefault();
  
    var origem = document.getElementById("origem").value;
    var destino = document.getElementById("destino").value;
    var data = document.getElementById("data").value;
  
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/passageiro", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  
    xhr.onreadystatechange = function() {
      if (xhr.readyState == 4 && xhr.status == 200) {
        // Handle the response
        var response = xhr.responseText;
        var messageElement = document.getElementById("message");
  
        if (response.includes("redirect:/entrar")) {
          messageElement.innerHTML = "Erro: Você não tem permissão para fazer isso.";
          messageElement.style.color = "red";
        } else {
          messageElement.innerHTML = "Pedido enviado com sucesso!";
          messageElement.style.color = "green";
        }
  
        messageElement.style.display = "block";
      }
    };
  
    xhr.send("origem=" + encodeURIComponent(origem) + "&destino=" + encodeURIComponent(destino) + "&data=" + encodeURIComponent(data));
  });

  
  
  document.getElementById("showPedidosButton").addEventListener("click", function() {
    var pedidosLista = document.getElementById("pedidosLista");
    if (pedidosLista.style.display === "none") {
        pedidosLista.style.display = "block";
    } else {
        pedidosLista.style.display = "none";
    }
});