(function () {
    const forms = document.querySelectorAll(".needs-validation");
    Array.from(forms).forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add("was-validated");
        }, false);
    });

    window.fintrackerNotify = function (message, type) {
        const wrap = document.createElement("div");
        wrap.className = "alert alert-" + (type || "success");
        wrap.setAttribute("role", "alert");
        wrap.textContent = message;
        wrap.style.position = "fixed";
        wrap.style.right = "16px";
        wrap.style.top = "16px";
        wrap.style.zIndex = "2000";
        document.body.appendChild(wrap);
        setTimeout(function () {
            wrap.remove();
        }, 3000);
    };

    window.fintrackerCsrf = function () {
        const token = document.querySelector('meta[name="_csrf"]');
        const header = document.querySelector('meta[name="_csrf_header"]');
        return {
            token: token ? token.content : "",
            header: header ? header.content : "X-CSRF-TOKEN"
        };
    };
})();
