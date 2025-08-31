import React, { useEffect, useMemo, useState } from "react";
import "./AdminDashBoard.css";
import AdminNavbar from "./AdminNavbar";


export default function AdminDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

const endpoint = `${import.meta.env.VITE_API_BASE_URL}/admin/dashboard`;

useEffect(() => {
  let alive = true;

  (async () => {
    try {
      setLoading(true);
      setErr("");

    
      const token = localStorage.getItem("token"); 
    

      const res = await fetch(endpoint, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`, // 👈 send token
        },
        credentials: "include", // keep this if your API also uses cookies
      });

      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const json = await res.json();
      if (alive) setData(json);

    } catch (e) {
      if (alive) setErr(e.message || "Failed to load");
    } finally {
      if (alive) setLoading(false);
    }
  })();

  return () => {
    alive = false;
  };
}, [endpoint]);


  const kpis = useMemo(() => {
    if (!data) return [];
    return [
      { label: "Total Suggestions", value: data.totalSuggestions ?? 0 },
      { label: "Open", value: data.openSuggestions ?? 0 },
      { label: "Under Review", value: data.underReviewSuggestions ?? 0 },
      { label: "Implemented", value: data.implementedSuggestions ?? 0 },
      { label: "Rejected", value: data.rejectedSuggestions ?? 0 },
      { label: "Votes", value: data.totalVotes ?? 0 },
      { label: "Unique Voters", value: data.uniqueVoters ?? 0 },
      {
        label: "Avg Votes/Suggestion",
        value: (data.averageVotesPerSuggestion ?? 0).toFixed(1),
      },
      { label: "Employees", value: data.totalEmployees ?? 0 },
    ];
  }, [data]);

  const suggestionBreakdown = useMemo(() => {
    if (!data) return [];
    const parts = [
      { key: "Open", value: data.openSuggestions ?? 0, color: "var(--c-open)" },
      {
        key: "Under Review",
        value: data.underReviewSuggestions ?? 0,
        color: "var(--c-review)",
      },
      {
        key: "Implemented",
        value: data.implementedSuggestions ?? 0,
        color: "var(--c-impl)",
      },
      {
        key: "Rejected",
        value: data.rejectedSuggestions ?? 0,
        color: "var(--c-rej)",
      },
      {
        key: "Anonymous",
        value: data.anonymousSuggestions ?? 0,
        color: "var(--c-anon)",
      },
    ];
    const total = parts.reduce((s, p) => s + p.value, 0) || 1;
    return { parts, total };
  }, [data]);

  const statusChangeStats = useMemo(() => {
    if (!data?.statusChangeStatistics) return [];
    const entries = Object.entries(data.statusChangeStatistics).map(
      ([status, count]) => ({ status, count })
    );
    const max = Math.max(...entries.map((e) => e.count), 1);
    return { entries, max };
  }, [data]);

  const adminActivity = useMemo(() => {
    if (!data?.adminActivityStatistics) return [];
    return Object.entries(data.adminActivityStatistics)
      .map(([admin, count]) => ({ admin, count }))
      .sort((a, b) => b.count - a.count);
  }, [data]);

  return (
    <>
    <AdminNavbar />

    <div className="adm-wrap">
      <header className="adm-header">
        <div className="adm-title">
          <h1>Admin Dashboard</h1>
          <p className="adm-subtitle">
            Overview of suggestions, votes, and admin activity
          </p>
        </div>
        
      </header>

      {loading && <div className="adm-state">Loading dashboard…</div>}
      {err && !loading && (
        <div className="adm-error">
          <strong>Failed to load:</strong> {String(err)}
        </div>
      )}

      {!loading && !err && data && (
        <>
          {/* KPI CARDS */}
          <section className="adm-grid">
            {kpis.map((k) => (
              <article key={k.label} className="adm-card kpi">
                <div className="kpi-label">{k.label}</div>
                <div className="kpi-value">{formatNumber(k.value)}</div>
              </article>
            ))}
          </section>

          {/* BREAKDOWN + RECENT */}
          <section className="adm-two-col">
            <article className="adm-card">
              <h2>Suggestion Breakdown</h2>
              <div className="stacked-bar" role="img" aria-label="Suggestion breakdown">
                {suggestionBreakdown.parts.map((p) => {
                  const pct = (p.value / suggestionBreakdown.total) * 100;
                  return (
                    <div
                      key={p.key}
                      className="stacked-segment"
                      style={{ width: `${pct}%`, background: p.color }}
                      title={`${p.key}: ${p.value} (${pct.toFixed(0)}%)`}
                    />
                  );
                })}
              </div>
              <ul className="legend">
                {suggestionBreakdown.parts.map((p) => (
                  <li key={p.key}>
                    <span className="dot" style={{ background: p.color }} />
                    <span>{p.key}</span>
                    <span className="legend-val">{formatNumber(p.value)}</span>
                  </li>
                ))}
              </ul>
            </article>

            <article className="adm-card">
              <h2>Recent (Last 7 Days)</h2>
              <div className="recent-grid">
                <div className="recent-item">
                  <div className="recent-label">New Suggestions</div>
                  <div className="recent-value">
                    {formatNumber(data.recentSuggestions7Days ?? 0)}
                  </div>
                </div>
                <div className="recent-item">
                  <div className="recent-label">Votes</div>
                  <div className="recent-value">
                    {formatNumber(data.recentVotes7Days ?? 0)}
                  </div>
                </div>
                <div className="recent-item">
                  <div className="recent-label">Status Changes</div>
                  <div className="recent-value">
                    {formatNumber(data.recentStatusChanges7Days ?? 0)}
                  </div>
                </div>
              </div>
              <p className="muted">
                Activity snapshot based on the past 7 days.
              </p>
            </article>
          </section>

          {/* STATUS CHANGES */}
          <section className="adm-card">
            <h2>Status Change Statistics</h2>
            <div className="barlist">
              {statusChangeStats.entries.map((e) => {
                const w = (e.count / statusChangeStats.max) * 100;
                return (
                  <div key={e.status} className="barlist-row">
                    <div className="barlist-label">{e.status}</div>
                    <div className="barlist-bar">
                      <div
                        className="bar"
                        style={{
                          width: `${w}%`,
                          background: colorForStatus(e.status),
                        }}
                      />
                    </div>
                    <div className="barlist-val">{formatNumber(e.count)}</div>
                  </div>
                );
              })}
            </div>
          </section>

          {/* ADMIN ACTIVITY */}
        
        </>
      )}
    </div>
        </>
  );
}

/* ---------- helpers ---------- */

function formatNumber(n) {
  if (n === null || n === undefined) return "0";
  const num = typeof n === "string" ? Number(n) : n;
  if (Number.isNaN(num)) return String(n);
  return new Intl.NumberFormat().format(num);
}

function colorForStatus(status) {
  const key = String(status || "").toUpperCase();
  if (key.includes("REJECT")) return "var(--c-rej)";
  if (key.includes("PROGRESS")) return "var(--c-progress)";
  if (key.includes("OPEN")) return "var(--c-open)";
  if (key.includes("REVIEW")) return "var(--c-review)";
  if (key.includes("IMPLEMENT")) return "var(--c-impl)";
  return "var(--c-muted)";
}
