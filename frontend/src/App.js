import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

/**
 * NEON LEDGER: District 01 - Main Terminal Interface
 * Portfolio demo interface for the Neon Ledger API.
 */
function App() {
  const apiBaseUrl =
    window.RUNTIME_CONFIG?.API_BASE_URL ||
    process.env.REACT_APP_API_BASE_URL ||
    'http://localhost:8080';
  const senderAccount = process.env.REACT_APP_SENDER_ACCOUNT || 'SK111222333';

  const [balance, setBalance] = useState(0);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [authenticated, setAuthenticated] = useState(false);
  const [credentials, setCredentials] = useState({ username: '', password: '' });

  // AI Assistant State
  const [aiMsg, setAiMsg] = useState("CONNECTION_ESTABLISHED. DISTRICT_01_NODE ONLINE.");
  const [chatInput, setChatInput] = useState("");

  // Transaction Form State
  const [formData, setFormData] = useState({
    receiverIban: '',
    amount: ''
  });

  const authConfig = {
    auth: {
      username: credentials.username,
      password: credentials.password
    }
  };

  /**
   * AI Logic
   */
  const askAi = (topic) => {
    const command = topic || chatInput.toLowerCase();

    if (command.includes("balance") || command === "STAV_KONTA") {
      setAiMsg(`CURRENT_LIQUIDITY: ${balance.toFixed(2)} CREDITS. STANDBY.`);
    } else if (command.includes("agent") || command === "SUPPORT") {
      setAiMsg("HUMAN_AGENTS_REPLACED. CONTACT EMERGENCY UPLINK: +421 900 666 001.");
    } else if (command.includes("security") || command === "SECURITY") {
      setAiMsg("DEMO_BASIC_AUTH ACTIVE. USE HTTPS OUTSIDE LOCAL DEVELOPMENT.");
    } else {
      setAiMsg("COMMAND_NOT_RECOGNIZED. ATTEMPTING NEURAL RECOVERY...");
    }
    setChatInput("");
  };

  /**
   * FETCH LEDGER: Gets transaction history from /api/transactions/ledger
   */
  const fetchLedger = async () => {
    try {
      const res = await axios.get(`${apiBaseUrl}/api/transactions/ledger`, authConfig);
      // Backend vracia list transakcií, otočíme ho aby najnovšie boli hore
      setTransactions(res.data.reverse()); 
      const balanceRes = await axios.get(
        `${apiBaseUrl}/api/transactions/balance/${senderAccount}`,
        authConfig
      );
      setBalance(Number(balanceRes.data));
    } catch (err) {
      console.error("LEDGER_FETCH_ERROR:", err);
      setAiMsg("🚨 ALERT: BACKEND_UNREACHABLE. CHECK_SYSTEM_LOGS.");
    }
  };

  useEffect(() => {
    if (authenticated) {
      fetchLedger();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [authenticated]);

  const login = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      await axios.get(
        `${apiBaseUrl}/api/transactions/balance/${senderAccount}`,
        authConfig
      );
      setAuthenticated(true);
      setAiMsg("ACCESS_GRANTED. DISTRICT_01_NODE ONLINE.");
    } catch (err) {
      setAiMsg("ACCESS_DENIED. CHECK_DEMO_CREDENTIALS.");
    } finally {
      setLoading(false);
    }
  };

  /**
   * EXECUTE TRANSFER: Sends data to /api/transactions/transfer
   */
  const executeTransfer = async (e) => {
    e.preventDefault();

    // Validácia
    if (!formData.receiverIban || !formData.amount) {
      setAiMsg("🚨 CRITICAL_ERROR: MANDATORY_FIELDS_MISSING.");
      return;
    }

    setLoading(true);

    try {
      await axios.post(
        `${apiBaseUrl}/api/transactions/transfer`,
        {
          from: senderAccount,
          to: formData.receiverIban,
          amount: formData.amount
        },
        authConfig
      );

      setAiMsg("TRANSACTION_AUTHORIZED. CREDITS_TRANSFERED_SUCCESSFULLY.");

      // Reset formulára a refresh dát
      setFormData({
        receiverIban: '', amount: ''
      });
      
      await fetchLedger();

    } catch (err) {
      console.error("TRANSFER_ERROR:", err);
      const errorMsg = err.response?.data?.message || "TRANSMISSION_FAILURE";
      setAiMsg(`🚨 CRITICAL_ERROR: ${errorMsg}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="neon-wrapper">
      <header className="cyber-header">
        <div className="logo">NEON<span>LEDGER</span></div>
        <div className="balance-box">
          <small>AVAILABLE_LIQUIDITY</small>
          <div className="amount">{balance.toFixed(2)} CR</div>
        </div>
      </header>

      {!authenticated ? (
        <main className="login-container">
          <section className="card login-card">
            <h2>NODE_AUTHENTICATION</h2>
            <form onSubmit={login}>
              <input
                aria-label="Username"
                placeholder="USERNAME"
                autoComplete="username"
                value={credentials.username}
                onChange={e => setCredentials({...credentials, username: e.target.value})}
              />
              <input
                aria-label="Password"
                type="password"
                placeholder="PASSWORD"
                autoComplete="current-password"
                value={credentials.password}
                onChange={e => setCredentials({...credentials, password: e.target.value})}
              />
              <button type="submit" className="btn-main" disabled={loading}>
                {loading ? "CONNECTING..." : "CONNECT_TO_NODE"}
              </button>
            </form>
          </section>
        </main>
      ) : (
      <main className="main-grid">
        <section className="card terminal">
          <h2 className="glitch" data-text="TRANSFER_TERMINAL">TRANSFER_TERMINAL</h2>
          <form onSubmit={executeTransfer} noValidate>
            <div className="input-group">
              <input
                placeholder="DESTINATION_IBAN (e.g. SK999888777)"
                value={formData.receiverIban}
                onChange={e => setFormData({...formData, receiverIban: e.target.value})}
              />
              <input
                type="number"
                min="0.01"
                step="0.01"
                placeholder="CREDIT_AMOUNT"
                value={formData.amount}
                onChange={e => setFormData({...formData, amount: e.target.value})}
              />
            </div>

            <button type="submit" className="btn-main" disabled={loading}>
              {loading ? "PROCESSING..." : "EXECUTE_TRANSFER"}
            </button>
          </form>
        </section>

        <section className="card ledger">
          <h2>LEDGER_HISTORY_LOG</h2>
          <div className="history-list">
            {transactions.length === 0 ? (
              <div className="empty-state">NO_DATA_FOUND_IN_NODE</div>
            ) : (
              transactions.map((t, index) => (
                <div key={t.id || index} className="history-item animate-fade">
                  <div className="t-info">
                    <span className="t-name">{t.receiverIban}</span>
                    <span className="t-date">{t.createdAt ? new Date(t.createdAt).toLocaleString() : "NOW"}</span>
                  </div>
                  <span className="t-amount out">-{parseFloat(t.amount).toFixed(2)} CR</span>
                </div>
              ))
            )}
          </div>
        </section>
      </main>
      )}

      <footer className="ai-console">
        <div className="ai-header">NEURAL_ASSISTANT_V4</div>
        <div className="ai-body">
          <div className="ai-text">{aiMsg}</div>
        </div>
        <div className="quick-actions">
          <span className="tag" onClick={() => askAi('STAV_KONTA')}>[ CHECK_BALANCE ]</span>
          <span className="tag" onClick={() => askAi('SECURITY')}>[ SECURITY_STATUS ]</span>
          <span className="tag" onClick={() => askAi('SUPPORT')}>[ UPLINK_AGENT ]</span>
        </div>
        <div className="ai-input">
          <input
            value={chatInput}
            onChange={e => setChatInput(e.target.value)}
            placeholder="Enter command..."
            onKeyPress={(e) => e.key === 'Enter' && askAi()}
          />
          <button onClick={() => askAi()}>RUN</button>
        </div>
      </footer>
    </div>
  );
}

export default App;
