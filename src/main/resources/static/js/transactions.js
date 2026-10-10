(function () {
    const form = document.getElementById("ajax-tx-form");
    if (!form) {
        return;
    }
    form.addEventListener("submit", function (event) {
        event.preventDefault();
        if (!form.checkValidity()) {
            form.classList.add("was-validated");
            return;
        }
        const payload = {
            amount: Number(form.amount.value),
            type: form.type.value,
            categoryId: Number(form.categoryId.value),
            date: form.date.value,
            description: form.description.value
        };
        const csrf = window.fintrackerCsrf();
        const headers = { "Content-Type": "application/json" };
        headers[csrf.header] = csrf.token;
        fetch("/api/transactions", {
            method: "POST",
            headers: headers,
            body: JSON.stringify(payload)
        }).then(function (response) {
            if (!response.ok) {
                return response.json().then(function (body) {
                    throw new Error(body.error || "Ошибка сохранения");
                });
            }
            window.fintrackerNotify("Транзакция добавлена через API");
            window.location.reload();
        }).catch(function (error) {
            window.fintrackerNotify(error.message, "danger");
        });
    });
})();
