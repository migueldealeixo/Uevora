document.getElementById("showPedidosButton").addEventListener("click", function() {
    var pedidosLista = document.getElementById("pedidosLista");
    if (pedidosLista.style.display === "none") {
        pedidosLista.style.display = "block";
    } else {
        pedidosLista.style.display = "none";
    }
});