(function () {
    "use strict";

    var raiz = document.documentElement;
    var TEMAS = { light: "bi bi-moon-stars", dark: "bi bi-sun" };

    function pintarIconoTema(tema) {
        var icono = document.getElementById("themeIcon");
        if (icono) {
            icono.className = TEMAS[tema] || TEMAS.light;
        }
    }

    function aplicarTema(tema) {
        raiz.setAttribute("data-bs-theme", tema);
        pintarIconoTema(tema);
    }

    var temaInicial = raiz.getAttribute("data-bs-theme") || "light";
    pintarIconoTema(temaInicial);

    document.addEventListener("click", function (evento) {

        if (evento.target.closest("#btnTheme")) {
            var actual = raiz.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
            localStorage.setItem("sgac-theme", actual);
            aplicarTema(actual);
            return;
        }

        if (evento.target.closest("#btnSidebar")) {
            var movil = window.matchMedia("(max-width: 991.98px)").matches;
            if (movil) {
                document.body.classList.toggle("sidebar-open");
            } else {
                raiz.classList.toggle("sidebar-collapsed");
                localStorage.setItem(
                    "sgac-sidebar",
                    raiz.classList.contains("sidebar-collapsed") ? "1" : "0"
                );
            }
            return;
        }

        if (evento.target.closest("#sidebarBackdrop")) {
            document.body.classList.remove("sidebar-open");
        }
    });

    document.addEventListener("keydown", function (evento) {
        if (evento.key === "Escape") {
            document.body.classList.remove("sidebar-open");
        }
    });
})();
