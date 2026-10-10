(function () {
    const metrics = window.FINTRACKER_METRICS;
    if (!metrics || typeof Chart === "undefined") {
        return;
    }
    const trend = metrics.monthlyTrend || [];
    const expenses = metrics.expenseByCategory || [];
    const trendCanvas = document.getElementById("trendChart");
    const expenseCanvas = document.getElementById("expenseChart");
    if (trendCanvas) {
        new Chart(trendCanvas, {
            type: "line",
            data: {
                labels: trend.map(function (p) { return p.label; }),
                datasets: [
                    { label: "Доходы", data: trend.map(function (p) { return p.income; }), borderColor: "#0f766e", tension: 0.3 },
                    { label: "Расходы", data: trend.map(function (p) { return p.expense; }), borderColor: "#b91c1c", tension: 0.3 }
                ]
            },
            options: { responsive: true, plugins: { legend: { position: "bottom" } } }
        });
    }
    if (expenseCanvas) {
        new Chart(expenseCanvas, {
            type: "doughnut",
            data: {
                labels: expenses.map(function (c) { return c.name; }),
                datasets: [{ data: expenses.map(function (c) { return c.value; }), backgroundColor: ["#0f766e", "#14b8a6", "#f59e0b", "#b91c1c", "#6366f1"] }]
            },
            options: { plugins: { legend: { position: "bottom" } } }
        });
    }
})();
