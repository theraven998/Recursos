// Cloudflare Worker de ejemplo: recibe GET /uso desde el teléfono y lo reenvía a tu PC
// por un servicio Workers VPC (túnel Cloudflare → 127.0.0.1:3430).
// El token se valida aquí (secreto TOKEN) y otra vez en tu PC.
export default {
  async fetch(req, env) {
    const url = new URL(req.url);
    if (req.method !== "GET" || url.pathname !== "/uso") return new Response("no existe", { status: 404 });
    const dado = (req.headers.get("Authorization") || "").replace(/^Bearer\s+/, "");
    if (!env.TOKEN || dado !== env.TOKEN) return Response.json({ error: "no autorizado" }, { status: 401 });
    try {
      const r = await env.PC.fetch("http://127.0.0.1:3430/uso", { headers: { Authorization: `Bearer ${dado}` } });
      return new Response(r.body, { status: r.status, headers: { "Content-Type": "application/json; charset=utf-8", "Cache-Control": "no-store" } });
    } catch (e) {
      return Response.json({ error: "PC apagado o sin conexión" }, { status: 502 });
    }
  },
};
