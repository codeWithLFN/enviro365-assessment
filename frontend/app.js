const API_BASE = "http://localhost:8080/api/v1";

const investorIdInput = document.getElementById("investorId");
const loadPortfolioBtn = document.getElementById("loadPortfolioBtn");
const portfolioDetails = document.getElementById("portfolioDetails");
const productsTableBody = document.getElementById("productsTableBody");
const withdrawalHistoryBody = document.getElementById("withdrawalHistoryBody");
const withdrawalForm = document.getElementById("withdrawalForm");
const withdrawalMessage = document.getElementById("withdrawalMessage");
const downloadCsvBtn = document.getElementById("downloadCsvBtn");

const tabButtons = document.querySelectorAll(".tab-btn");
const tabPanes = document.querySelectorAll(".tab-pane");

let currentInvestorId = null;
let withdrawalHistory = [];

tabButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const targetId = button.dataset.tab;

    tabButtons.forEach((btn) => {
      btn.classList.remove("bg-slate-900", "text-white");
      btn.classList.add("bg-slate-100", "text-slate-700");
    });

    tabPanes.forEach((pane) => pane.classList.add("hidden"));

    button.classList.remove("bg-slate-100", "text-slate-700");
    button.classList.add("bg-slate-900", "text-white");

    document.getElementById(targetId).classList.remove("hidden");
  });
});

loadPortfolioBtn.addEventListener("click", loadPortfolio);
withdrawalForm.addEventListener("submit", submitWithdrawal);
downloadCsvBtn.addEventListener("click", downloadCsv);

renderWithdrawalHistory();

async function loadPortfolio() {
  const investorId = investorIdInput.value.trim();

  clearWithdrawalMessage();

  if (!investorId) {
    currentInvestorId = null;
    showPortfolioError("Please enter an investor ID.");
    renderProducts([]);
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/portfolio/${investorId}`);
    const data = await parseResponseSafely(response);

    if (!response.ok) {
      throw new Error(extractErrorMessage(data, "Failed to load portfolio."));
    }

    currentInvestorId = investorId;
    renderPortfolio(data);
    renderProducts(data?.investmentProducts || []);
    switchToTab("portfolioTab");
  } catch (error) {
    currentInvestorId = null;
    showPortfolioError(error.message);
    renderProducts([]);
  }
}

function renderPortfolio(data) {
  portfolioDetails.innerHTML = `
    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Investor ID</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${escapeHtml(data?.investorId ?? "-")}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Full Name</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${escapeHtml(`${data?.firstName ?? ""} ${data?.lastName ?? ""}`.trim() || "-")}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Email</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${escapeHtml(data?.email ?? "-")}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Date of Birth</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${escapeHtml(data?.dateOfBirth ?? "-")}</p>
      </div>
    </div>
  `;
}

function renderProducts(products) {
  if (!products || !products.length) {
    productsTableBody.innerHTML = `
      <tr>
        <td colspan="4" class="px-4 py-6 text-center text-slate-400">
          No investment products found.
        </td>
      </tr>
    `;
    return;
  }

  productsTableBody.innerHTML = products.map((product) => `
    <tr class="hover:bg-slate-50">
      <td class="px-4 py-3 text-slate-700">${escapeHtml(product?.productId ?? "-")}</td>
      <td class="px-4 py-3 font-medium text-slate-900">${escapeHtml(product?.productName ?? "-")}</td>
      <td class="px-4 py-3 text-slate-700">${escapeHtml(product?.productType ?? "-")}</td>
      <td class="px-4 py-3 text-slate-700">${escapeHtml(product?.balance ?? "-")}</td>
    </tr>
  `).join("");
}

async function submitWithdrawal(event) {
  event.preventDefault();

  const productId = document.getElementById("productId").value.trim();
  const amount = document.getElementById("amount").value.trim();

  if (!productId || !amount) {
    showWithdrawalMessage("Please enter product ID and amount.", false);
    switchToTab("withdrawalTab");
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/portfolio/withdrawals`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        productId: Number(productId),
        amount: Number(amount)
      })
    });

    const data = await parseResponseSafely(response);

    if (!response.ok) {
      throw new Error(extractErrorMessage(data, "Withdrawal failed."));
    }

    showWithdrawalMessage(
      `Withdrawal submitted successfully. Withdrawal ID: ${data?.withdrawalId ?? "N/A"}`,
      true
    );

    withdrawalHistory.unshift({
      withdrawalId: data?.withdrawalId ?? "-",
      productId: productId,
      amount: amount,
      status: data?.status ?? "PENDING"
    });

    renderWithdrawalHistory();
    withdrawalForm.reset();
    switchToTab("historyTab");
  } catch (error) {
    showWithdrawalMessage(error.message, false);
    switchToTab("withdrawalTab");
  }
}

function renderWithdrawalHistory() {
  if (!withdrawalHistory.length) {
    withdrawalHistoryBody.innerHTML = `
      <tr>
        <td colspan="4" class="px-4 py-6 text-center text-slate-400">
          No withdrawal history loaded yet.
        </td>
      </tr>
    `;
    return;
  }

  withdrawalHistoryBody.innerHTML = withdrawalHistory.map((item) => `
    <tr class="hover:bg-slate-50">
      <td class="px-4 py-3 text-slate-700">${escapeHtml(item.withdrawalId)}</td>
      <td class="px-4 py-3 text-slate-700">${escapeHtml(item.productId)}</td>
      <td class="px-4 py-3 text-slate-700">${escapeHtml(item.amount)}</td>
      <td class="px-4 py-3">
        <span class="${getStatusBadgeClass(item.status)}">
          ${escapeHtml(item.status)}
        </span>
      </td>
    </tr>
  `).join("");
}

async function downloadCsv() {
  const investorId = currentInvestorId || investorIdInput.value.trim();

  if (!investorId) {
    showWithdrawalMessage("Please load or enter an investor ID first.", false);
    switchToTab("exportTab");
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/statements/export?investorId=${investorId}`);

    if (!response.ok) {
      const data = await parseResponseSafely(response);
      throw new Error(extractErrorMessage(data, "Failed to download CSV."));
    }

    const blob = await response.blob();
    const blobUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = blobUrl;
    link.download = `statements-investor-${investorId}.csv`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(blobUrl);

    showWithdrawalMessage("CSV downloaded successfully.", true);
    switchToTab("exportTab");
  } catch (error) {
    showWithdrawalMessage(error.message, false);
    switchToTab("exportTab");
  }
}

function showWithdrawalMessage(message, success) {
  withdrawalMessage.className = success
    ? "mt-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700"
    : "mt-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700";

  withdrawalMessage.textContent = message;
}

function clearWithdrawalMessage() {
  withdrawalMessage.className = "mt-4 text-sm";
  withdrawalMessage.textContent = "";
}

function showPortfolioError(message) {
  portfolioDetails.innerHTML = `
    <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
      ${escapeHtml(message)}
    </div>
  `;
}

function switchToTab(targetId) {
  tabButtons.forEach((btn) => {
    btn.classList.remove("bg-slate-900", "text-white");
    btn.classList.add("bg-slate-100", "text-slate-700");
  });

  tabPanes.forEach((pane) => pane.classList.add("hidden"));

  const activeButton = document.querySelector(`[data-tab="${targetId}"]`);
  const activePane = document.getElementById(targetId);

  if (activeButton) {
    activeButton.classList.remove("bg-slate-100", "text-slate-700");
    activeButton.classList.add("bg-slate-900", "text-white");
  }

  if (activePane) {
    activePane.classList.remove("hidden");
  }
}

async function parseResponseSafely(response) {
  const contentType = response.headers.get("content-type") || "";

  if (contentType.includes("application/json")) {
    try {
      return await response.json();
    } catch {
      return null;
    }
  }

  try {
    const text = await response.text();
    return text ? { message: text } : null;
  } catch {
    return null;
  }
}

function extractErrorMessage(data, fallbackMessage) {
  if (!data) {
    return fallbackMessage;
  }

  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (data.message && typeof data.message === "string") {
    return data.message;
  }

  if (data.errors && typeof data.errors === "object") {
    const validationMessage = Object.values(data.errors)
      .filter(Boolean)
      .join(", ");

    if (validationMessage) {
      return validationMessage;
    }
  }

  return fallbackMessage;
}

function getStatusBadgeClass(status) {
  const normalizedStatus = String(status || "").toUpperCase();

  if (normalizedStatus === "APPROVED") {
    return "inline-flex rounded-full bg-emerald-100 px-3 py-1 text-xs font-medium text-emerald-700";
  }

  if (normalizedStatus === "REJECTED" || normalizedStatus === "FAILED") {
    return "inline-flex rounded-full bg-red-100 px-3 py-1 text-xs font-medium text-red-700";
  }

  return "inline-flex rounded-full bg-amber-100 px-3 py-1 text-xs font-medium text-amber-700";
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}