const { useState } = React;

const services = [
  {
    key: "pecas",
    title: "Pecas",
    endpoint: "/api/pecas",
    tone: "from-amber-400/30 to-orange-500/10",
    createFields: [
      { key: "numeroIdentificacao", label: "Numero de identificacao", placeholder: "PC-2001" },
      { key: "nome", label: "Nome", placeholder: "Bateria" },
      { key: "descricao", label: "Descricao", placeholder: "Bateria 60Ah" }
    ],
    queryByKey: { label: "Numero", route: "numero", placeholder: "PC-2001" }
  },
  {
    key: "clientes",
    title: "Clientes",
    endpoint: "/api/clientes",
    tone: "from-sky-400/30 to-cyan-500/10",
    createFields: [
      { key: "cpf", label: "CPF", placeholder: "12345678901" },
      { key: "nome", label: "Nome", placeholder: "Ana Paula" }
    ],
    queryByKey: { label: "CPF", route: "cpf", placeholder: "12345678901" }
  },
  {
    key: "representantes",
    title: "Representantes",
    endpoint: "/api/representantes",
    tone: "from-emerald-400/30 to-teal-500/10",
    createFields: [
      { key: "cpf", label: "CPF", placeholder: "45678901234" },
      { key: "nome", label: "Nome", placeholder: "Diego Ribeiro" }
    ],
    queryByKey: { label: "CPF", route: "cpf", placeholder: "45678901234" }
  }
];

async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json"
    },
    ...options
  });

  const text = await response.text();
  let body;

  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = text;
  }

  if (!response.ok) {
    throw new Error(`${response.status} ${response.statusText}${text ? `: ${text}` : ""}`);
  }

  return body;
}

function pretty(data) {
  if (data == null) {
    return "Sem conteudo.";
  }
  if (typeof data === "string") {
    return data;
  }
  return JSON.stringify(data, null, 2);
}

function ServicePanel({ service, index }) {
  const [form, setForm] = useState(
    service.createFields.reduce((acc, field) => ({ ...acc, [field.key]: "" }), {})
  );
  const [searchName, setSearchName] = useState("");
  const [searchId, setSearchId] = useState("");
  const [searchKey, setSearchKey] = useState("");
  const [output, setOutput] = useState("Execute uma acao para ver o resultado.");
  const [loading, setLoading] = useState(false);

  const updateField = (field, value) => {
    setForm((current) => ({ ...current, [field]: value }));
  };

  const run = async (fn) => {
    setLoading(true);
    try {
      const data = await fn();
      setOutput(pretty(data));
    } catch (error) {
      setOutput(String(error.message || error));
    } finally {
      setLoading(false);
    }
  };

  const create = () => {
    const payload = Object.fromEntries(
      Object.entries(form)
        .map(([key, value]) => [key, String(value || "").trim()])
        .filter(([, value]) => value !== "")
    );

    run(() => request(service.endpoint, { method: "POST", body: JSON.stringify(payload) }));
  };

  const listAll = () => run(() => request(service.endpoint));
  const byName = () => {
    if (!searchName.trim()) {
      setOutput("Informe um nome para busca.");
      return;
    }
    run(() => request(`${service.endpoint}/buscar?nome=${encodeURIComponent(searchName.trim())}`));
  };

  const byId = () => {
    if (!searchId.trim()) {
      setOutput("Informe um ID valido.");
      return;
    }
    run(() => request(`${service.endpoint}/id/${encodeURIComponent(searchId.trim())}`));
  };

  const byKey = () => {
    if (!searchKey.trim()) {
      setOutput(`Informe ${service.queryByKey.label}.`);
      return;
    }
    run(() => request(`${service.endpoint}/${service.queryByKey.route}/${encodeURIComponent(searchKey.trim())}`));
  };

  return (
    <section
      className="animate-rise rounded-3xl border border-slate-700/70 bg-panel/70 p-6 shadow-2xl backdrop-blur"
      style={{ animationDelay: `${index * 120}ms` }}
    >
      <div className={`mb-5 rounded-2xl bg-gradient-to-r ${service.tone} p-4`}>
        <h2 className="text-xl font-semibold tracking-tight text-white">{service.title}</h2>
        <p className="mt-1 text-sm text-slate-200">Endpoint base: {service.endpoint}</p>
      </div>

      <div className="space-y-4">
        <div className="grid gap-3">
          {service.createFields.map((field) => (
            <label key={field.key} className="text-sm text-slate-300">
              <span className="mb-1 block">{field.label}</span>
              <input
                className="w-full rounded-xl border border-slate-600 bg-slate-900/80 px-3 py-2 text-slate-100 outline-none transition focus:border-accent"
                value={form[field.key]}
                onChange={(event) => updateField(field.key, event.target.value)}
                placeholder={field.placeholder}
              />
            </label>
          ))}
          <button
            type="button"
            onClick={create}
            className="rounded-xl bg-accent px-4 py-2 font-semibold text-ink transition hover:brightness-110 disabled:opacity-60"
            disabled={loading}
          >
            Cadastrar
          </button>
        </div>

        <div className="grid gap-2 sm:grid-cols-2">
          <button
            type="button"
            onClick={listAll}
            className="rounded-xl border border-slate-500 bg-slate-900/70 px-3 py-2 text-sm font-medium transition hover:border-slate-300"
            disabled={loading}
          >
            Listar todos
          </button>

          <div className="flex gap-2">
            <input
              className="min-w-0 flex-1 rounded-xl border border-slate-600 bg-slate-900/80 px-3 py-2 text-sm outline-none focus:border-accent"
              placeholder="Buscar por nome"
              value={searchName}
              onChange={(event) => setSearchName(event.target.value)}
            />
            <button
              type="button"
              onClick={byName}
              className="rounded-xl border border-slate-500 px-3 py-2 text-sm transition hover:border-slate-200"
              disabled={loading}
            >
              Buscar
            </button>
          </div>

          <div className="flex gap-2">
            <input
              className="min-w-0 flex-1 rounded-xl border border-slate-600 bg-slate-900/80 px-3 py-2 text-sm outline-none focus:border-accent"
              placeholder="Consultar por ID"
              value={searchId}
              onChange={(event) => setSearchId(event.target.value)}
            />
            <button
              type="button"
              onClick={byId}
              className="rounded-xl border border-slate-500 px-3 py-2 text-sm transition hover:border-slate-200"
              disabled={loading}
            >
              ID
            </button>
          </div>

          <div className="flex gap-2">
            <input
              className="min-w-0 flex-1 rounded-xl border border-slate-600 bg-slate-900/80 px-3 py-2 text-sm outline-none focus:border-accent"
              placeholder={`Consultar por ${service.queryByKey.label}`}
              value={searchKey}
              onChange={(event) => setSearchKey(event.target.value)}
            />
            <button
              type="button"
              onClick={byKey}
              className="rounded-xl border border-slate-500 px-3 py-2 text-sm transition hover:border-slate-200"
              disabled={loading}
            >
              {service.queryByKey.label}
            </button>
          </div>
        </div>

        <pre className="max-h-64 overflow-auto rounded-2xl border border-slate-700 bg-slate-950/80 p-4 font-mono text-xs leading-6 text-mint">
          {loading ? "Carregando..." : output}
        </pre>
      </div>
    </section>
  );
}

function App() {
  return (
    <main className="mx-auto w-full max-w-7xl px-4 py-8 md:px-8 md:py-12">
      <div className="grid gap-6 lg:grid-cols-3">
        {services.map((service, index) => (
          <ServicePanel key={service.key} service={service} index={index} />
        ))}
      </div>
    </main>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<App />);
