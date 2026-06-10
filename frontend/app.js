const API_BASE = "http://localhost:8080/api/v1";

const investorIdInput = document.getElementById("investorId");
const loadPortfolioBtn = document.getElementById("loadPortfolioBtn");
const portfolioDetails = document.getElementById("portfolioDetails");
const productsTableBody = document.getElementById("productsTableBody");
const withdrawalHistoryBody = document.getElementById("withdrawalHistoryBody");
const withdrawalForm = document.getElementById("withdrawalForm");
const withdrawalMessage = document.getElementById("withdrawalMessage");
const downloadCsvBtn = document.getElementById("downloadCsvBtn");

let currentInvestorId = null;
let withdrawalHistory = [];

loadPortfolioBtn.addEventListener("click", loadPortfolio);
withdrawalForm.addEventListener("submit", submitWithdrawal);
downloadCsvBtn.addEventListener("click", downloadCsv);

async function loadPortfolio() {
  const investorId = investorIdInput.value.trim();

  if (!investorId) {
    showPortfolioError("Please enter an investor ID.");
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/portfolio/${investorId}`);

    if (!response.ok) {
      throw new Error("Failed to load portfolio.");
    }

    const data = await response.json();
    currentInvestorId = investorId;

    renderPortfolio(data);
    renderProducts(data.investmentProducts || []);
  } catch (error) {
    currentInvestorId = null;
    portfolioDetails.innerHTML = `
      <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
        ${error.message}
      </div>
    `;
    productsTableBody.innerHTML = "";
  }
}

function renderPortfolio(data) {
  portfolioDetails.innerHTML = `
    <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Investor ID</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${data.investorId ?? "-"}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Full Name</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${data.firstName ?? ""} ${data.lastName ?? ""}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Email</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${data.email ?? "-"}</p>
      </div>
      <div class="rounded-xl bg-slate-50 p-4">
        <p class="text-xs font-medium uppercase tracking-wide text-slate-500">Date of Birth</p>
        <p class="mt-1 text-sm font-semibold text-slate-900">${data.dateOfBirth ?? "-"}</p>
      </div>
    </div>
  `;
}

function renderProducts(products) {
  if (!products.length) {
    productsTableBody.innerHTML = `
      <tr>
        <td colspan="4" class="px-4 py-6 text-center text-slate-400">
          No investment products found.
        </td>
      </tr>
    `;
    return;
  }

  productsTableBody.innerHTML = products.map(product => `
    <tr class="hover:bg-slate-50">
      <td class="px-4 py-3 text-slate-700">${product.productId ?? "-"}</td>
      <td class="px-4 py-3 font-medium text-slate-900">${product.productName ?? "-"}</td>
      <td class="px-4 py-3 text-slate-700">${product.productType ?? "-"}</td>
      <td class="px-4 py-3 text-slate-700">${product.balance ?? "-"}</td>
    </tr>
  `).join("");
}

async function submitWithdrawal(event) {
  event.preventDefault();

  const productId = document.getElementById("productId").value.trim();
  const amount = document.getElementById("amount").value.trim();

  if (!productId || !amount) {
    showWithdrawalMessage("Please enter product ID and amount.", false);
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

    const data = await parseJsonSafely(response);

    if (!response.ok) {
      throw new Error(data?.message || "Withdrawal failed.");
    }

    showWithdrawalMessage(
      `Withdrawal submitted successfully. Withdrawal ID: ${data.withdrawalId ?? "N/A"}`,
      true
    );

    withdrawalHistory.unshift({
      withdrawalId: data.withdrawalId ?? "-",
      productId: productId,
      amount: amount,
      status: data.status ?? "PENDING"
    });

    renderWithdrawalHistory();
    withdrawalForm.reset();
  } catch (error) {
    showWithdrawalMessage(error.message, false);
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

  withdrawalHistoryBody.innerHTML = withdrawalHistory.map(item => `
    <tr class="hover:bg-slate-50">
      <td class="px-4 py-3 text-slate-700">${item.withdrawalId}</td>
      <td class="px-4 py-3 text-slate-700">${item.productId}</td>
      <td class="px-4 py-3 text-slate-700">${item.amount}</td>
      <td class="px-4 py-3">
        <span class="inline-flex rounded-full bg-amber-100 px-3 py-1 text-xs font-medium text-amber-700">
          ${item.status}
        </span>
      </td>
    </tr>
  `).join("");
}

async function downloadCsv() {
  const investorId = currentInvestorId || investorIdInput.value.trim();

  if (!investorId) {
    alert("Please load or enter an investor ID first.");
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/statements/export?investorId=${investorId}`);

    if (!response.ok) {
      throw new Error("Failed to download CSV.");
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
  } catch (error) {
    alert(error.message);
  }
}

function showWithdrawalMessage(message, success) {
  withdrawalMessage.className = success
    ? "mt-4 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700"
    : "mt-4 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700";

  withdrawalMessage.textContent = message;
}

function showPortfolioError(message) {
  portfolioDetails.innerHTML = `
    <div class="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
      ${message}
    </div>
  `;
}

async function parseJsonSafely(response) {
  const text = await response.text();
  if (!text) return null;

  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}